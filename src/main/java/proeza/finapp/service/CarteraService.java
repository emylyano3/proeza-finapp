package proeza.finapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proeza.finapp.entities.*;
import proeza.finapp.repository.PortfolioRepository;
import proeza.finapp.rest.dto.BuyDTO;
import proeza.finapp.rest.dto.SaleDTO;
import proeza.finapp.rest.translator.CompraTranslator;
import proeza.finapp.rest.translator.VentaTranslator;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class CarteraService {

    @Autowired
    private VentaTranslator translator;

    @Autowired
    private CompraTranslator compraTranslator;

    @Autowired
    private PortfolioRepository portfolioRepository;

    public Portfolio crear(Portfolio portfolio) {
        Objects.requireNonNull(portfolio);
        boolean exists = Optional.ofNullable(portfolio.getId())
                                 .map(portfolioRepository::findById)
                                 .isPresent();
        return exists ? portfolio : portfolioRepository.save(portfolio);
    }

    public Portfolio sale(SaleDTO saleDTO) {
        //TODO Validar con annotations
        Objects.requireNonNull(saleDTO);
        Objects.requireNonNull(saleDTO.getIdCartera());
        Objects.requireNonNull(saleDTO.getTicker());
        Objects.requireNonNull(saleDTO.getCantidad());
        Objects.requireNonNull(saleDTO.getPrecio());
        //TODO Verificar si es un movimiento intradiario para aplicar o no los nuevos cargos.
        //TODO Agregar en el broker un flag para saber si aplica o no la exencion de cargo en movs intradiarios
        Sale sale = translator.toDomain(saleDTO);
        sale.getPortfolio().getBroker().getCharges().forEach(c -> {
            double totalCargo = (c.getTasaAplicable() - 1) * sale.getOperado().doubleValue();
            sale.addCargo(c, totalCargo);
        });
        sale.getPortfolio().update(sale.getAsset());
        Account account = sale.getPortfolio().getAccount();
        Deposit deposit = new Deposit();
        deposit.setAccount(account);
        deposit.setDate(LocalDateTime.now());
        deposit.setAmount(sale.getNetAmount());
        account.addDeposito(deposit);
        return sale.getPortfolio();
    }

    public Portfolio buy(BuyDTO buyDTO) {
        Objects.requireNonNull(buyDTO);
        Objects.requireNonNull(buyDTO.getIdCartera());
        Objects.requireNonNull(buyDTO.getTicker());
        Objects.requireNonNull(buyDTO.getCantidad());
        Objects.requireNonNull(buyDTO.getPrecio());
        Buy buy = compraTranslator.toDomain(buyDTO);
        buy.getPortfolio().getBroker().getCharges().forEach(c -> {
            double totalCargo = (c.getTasaAplicable() - 1) * buy.getOperado().doubleValue();
            buy.addCargo(c, totalCargo);
        });
        buy.getPortfolio().update(buy.getAsset());
        Account account = buy.getPortfolio().getAccount();
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setAccount(account);
        withdrawal.setDate(LocalDateTime.now());
        withdrawal.setAmount(buy.getMovementTotal());
        account.addExtraccion(withdrawal);
        return buy.getPortfolio();
    }
}
