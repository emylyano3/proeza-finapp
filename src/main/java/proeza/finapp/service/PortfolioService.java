package proeza.finapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proeza.finapp.domain.*;
import proeza.finapp.repository.AssetBreadcrumbRepository;
import proeza.finapp.repository.InstrumentRepository;
import proeza.finapp.repository.PortfolioRepository;
import proeza.finapp.rest.dto.BuyDTO;
import proeza.finapp.rest.dto.SellDTO;
import proeza.finapp.rest.translator.BuyTranslator;
import proeza.finapp.rest.translator.SaleTranslator;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@Transactional
public class PortfolioService {

    @Autowired
    private SaleTranslator translator;

    @Autowired
    private BuyTranslator buyTranslator;

    @Autowired
    private PortfolioRepository portfolioRepo;

    @Autowired
    private InstrumentRepository instrumentRepo;

    public Portfolio create(Portfolio portfolio) {
        Objects.requireNonNull(portfolio);
        boolean exists = Optional.ofNullable(portfolio.getId())
                                 .map(portfolioRepo::findById)
                                 .isPresent();
        return exists ? portfolio : portfolioRepo.save(portfolio);
    }

    @Autowired
    private AssetBreadcrumbRepository assetBreadcrumbRepo;

    public List<AssetBreadcrumb> getAssetBreadCrumb(Long portfolioId, String ticker) {
        Portfolio portfolio = portfolioRepo.findById(portfolioId)
                                           .orElseThrow(entityNotFound("Portfolio", portfolioId));
        Instrument instrument = this.instrumentRepo.findByTicker(ticker)
                                                   .orElseThrow(entityNotFound("Instrument", ticker));
        return assetBreadcrumbRepo.findByAssetPortfolioAndAssetInstrumentAndRemainsGreaterThan(portfolio, instrument, 0);
    }

    private Supplier<EntityNotFoundException> entityNotFound(String entityName, Object key) {
        throw new EntityNotFoundException(String.format("%s not found: %s", entityName, key));
    }

    public Portfolio sell(SellDTO sellDTO) {
        Objects.requireNonNull(sellDTO);
        //TODO Verificar si es un movimiento intradiario para aplicar o no los nuevos cargos.
        //TODO Agregar en el broker un flag para saber si aplica o no la exencion de cargo en movs intradiarios
        Sale sale = translator.toDomain(sellDTO);
        sale.getPortfolio().getBroker().getCharges().forEach(c -> {
            double totalCargo = (c.getApplicableRate() - 1) * sale.getOperado().doubleValue();
            sale.addCargo(c, totalCargo);
        });
        sale.getPortfolio().update(sale.getAsset());
        Account account = sale.getPortfolio().getAccount();
        Deposit deposit = new Deposit();
        deposit.setAccount(account);
        deposit.setDate(LocalDateTime.now());
        deposit.setAmount(sale.getNetAmount());
        account.apply(deposit);
        return sale.getPortfolio();
    }

    public Portfolio buy(BuyDTO buyDTO) {
        Objects.requireNonNull(buyDTO);
        Buyout buyout = buyTranslator.toDomain(buyDTO);
        buyout.getPortfolio().getBroker().getCharges().forEach(c -> {
            double totalCargo = (c.getApplicableRate() - 1) * buyout.getOperado().doubleValue();
            buyout.addCargo(c, totalCargo);
        });
        buyout.getPortfolio().update(buyout.getAsset());
        Account account = buyout.getPortfolio().getAccount();
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setAccount(account);
        withdrawal.setDate(LocalDateTime.now());
        withdrawal.setAmount(buyout.getMovementTotal());
        account.apply(withdrawal);
        return buyout.getPortfolio();
    }
}
