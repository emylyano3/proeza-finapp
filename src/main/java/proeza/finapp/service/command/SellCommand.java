package proeza.finapp.service.command;

import io.vavr.control.Either;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import proeza.finapp.domain.Account;
import proeza.finapp.domain.Deposit;
import proeza.finapp.domain.Portfolio;
import proeza.finapp.domain.Sale;
import proeza.finapp.exception.BusinessError;
import proeza.finapp.exception.ErrorTypes;
import proeza.finapp.patterns.command.ICommand;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SellCommand implements ICommand<Portfolio, BusinessError> {

    private Sale sale;

    @Override
    public Either<BusinessError, Portfolio> execute() {
        //TODO Verificar si es un movimiento intradiario para aplicar o no los nuevos cargos.
        //TODO Agregar en el broker un flag para saber si aplica o no la exencion de cargo en movs intradiarios
        Objects.requireNonNull(sale, "Sale can not be null");
        sale.getPortfolio().getBroker().getCharges().forEach(c -> {
            double totalCargo = (c.getApplicableRate() - 1) * sale.getOperado().doubleValue();
            sale.addCargo(c, totalCargo);
        });
        if (sale.getQuantity() > sale.getAsset().getHolding()) {
            //TODO Manejar esta validacion dentro de Sale con spring
            return Either.left(BusinessError.builder()
                                            .message("Not enough assets holding to sell")
                                            .type(ErrorTypes.INSUFFICIENT_ASSETS)
                                            .build());
        }
        sale.getPortfolio().update(sale.getAsset());
        Account account = sale.getPortfolio().getAccount();
        Deposit deposit = new Deposit();
        deposit.setAccount(account);
        deposit.setDate(LocalDateTime.now());
        deposit.setAmount(sale.getNetAmount());
        account.apply(deposit);
        return Either.right(sale.getPortfolio());
    }

    public SellCommand withSale(Sale sale) {
        this.sale = sale;
        return this;
    }
}
