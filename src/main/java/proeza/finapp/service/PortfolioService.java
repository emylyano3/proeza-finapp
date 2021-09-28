package proeza.finapp.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import proeza.finapp.domain.*;
import proeza.finapp.exception.BusinessException;
import proeza.finapp.exception.ExceptionFactory;
import proeza.finapp.patterns.command.Invoker;
import proeza.finapp.repository.AssetBreadcrumbRepository;
import proeza.finapp.repository.InstrumentRepository;
import proeza.finapp.repository.PortfolioRepository;
import proeza.finapp.rest.dto.BuyDTO;
import proeza.finapp.rest.dto.SellDTO;
import proeza.finapp.rest.translator.BuyTranslator;
import proeza.finapp.rest.translator.SaleTranslator;
import proeza.finapp.service.command.SellCommand;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Log4j2
@Service
@Transactional
public class PortfolioService {

    @Autowired
    private SaleTranslator saleTranslator;

    @Autowired
    private ApplicationContext appContext;

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

    public Portfolio sell(SellDTO sellDTO) throws BusinessException {
        Objects.requireNonNull(sellDTO);
        SellCommand command = this.appContext.getBean(SellCommand.class)
                                             .withSale(saleTranslator.toDomain(sellDTO));
        return new Invoker().invoke(command)
                            .peek(p -> log.info("Sell executed ok with data: {}", sellDTO))
                            .peekLeft(e -> log.info("Sell not executed due to error: {})", e.getMessage()))
                            .getOrElseThrow(ExceptionFactory::newSellException);
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
