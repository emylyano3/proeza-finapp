package proeza.finapp.service.command;

import io.vavr.control.Either;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import proeza.finapp.domain.*;
import proeza.finapp.exception.BusinessError;
import proeza.finapp.exception.BusinessErrorFactory;
import proeza.finapp.patterns.command.ICommand;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SellCommand implements ICommand<Portfolio, BusinessError> {

    private Sale sale;

    private final Map<Predicate<Sale>, Function<Sale, BusinessError>> validations = Map.of(
            this::insufficientAssets, BusinessErrorFactory::portfolioInsufficientAssetsError
    );

    @Override
    public Either<BusinessError, Portfolio> execute() {
        //TODO Verificar si es un movimiento intradiario para aplicar o no los nuevos cargos.
        //TODO Agregar en el broker un flag para saber si aplica o no la exencion de cargo en movs intradiarios
        Objects.requireNonNull(sale, "Sale can not be null");
        sale.getPortfolio().getBroker().getCharges().forEach(c -> {
            double totalCargo = (c.getApplicableRate() - 1) * sale.getOperado().doubleValue();
            sale.addCargo(c, totalCargo);
        });
        var opValidationError = processValidations();
        if (opValidationError.isPresent()) {
            return Either.left(opValidationError.get());
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

    private Optional<BusinessError> processValidations() {
        return validations.entrySet().stream()
                          .filter(e -> e.getKey().test(sale))
                          .findAny()
                          .map(Map.Entry::getValue)
                          .map(f -> f.apply(sale));
    }

    private boolean insufficientAssets(Sale sale) {
        return sale.getQuantity() > sale.getAsset().getHolding();
    }

    public SellCommand withSale(Sale sale) {
        this.sale = sale;
        return this;
    }
}
