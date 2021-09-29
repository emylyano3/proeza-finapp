package proeza.finapp.service.command;

import io.vavr.control.Either;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import proeza.finapp.domain.Account;
import proeza.finapp.domain.Buyout;
import proeza.finapp.domain.Portfolio;
import proeza.finapp.domain.Withdrawal;
import proeza.finapp.exception.BusinessError;
import proeza.finapp.exception.BusinessErrorFactory;
import proeza.finapp.patterns.command.ICommand;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class BuyCommand implements ICommand<Portfolio, BusinessError> {

    private Buyout buyout;

    private final Map<Predicate<Buyout>, Function<Buyout, BusinessError>> validations = Map.of(
            this::insufficientFunds, BusinessErrorFactory::accountInsufficientFundsToPurchaseError
    );

    @Override
    @Transactional
    public Either<BusinessError, Portfolio> execute() {
        buyout.getPortfolio().getBroker().getCharges().forEach(c -> {
            double totalCargo = (c.getApplicableRate() - 1) * buyout.getOperado().doubleValue();
            buyout.addCargo(c, totalCargo);
        });
        var opValidationError = processValidations();
        if (opValidationError.isPresent()) {
            return Either.left(opValidationError.get());
        }
        buyout.getPortfolio().update(buyout.getAsset());
        Account account = buyout.getPortfolio().getAccount();
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setAccount(account);
        withdrawal.setDate(LocalDateTime.now());
        withdrawal.setAmount(buyout.getMovementTotal());
        account.apply(withdrawal);
        return Either.right(buyout.getPortfolio());
    }

    private Optional<BusinessError> processValidations() {
        return validations.entrySet().stream()
                          .filter(e -> e.getKey().test(buyout))
                          .findAny()
                          .map(Map.Entry::getValue)
                          .map(f -> f.apply(buyout));
    }

    private boolean insufficientFunds(Buyout buyout) {
        return buyout.getMovementTotal().doubleValue() > buyout.getPortfolio().getAccount().getBalance().doubleValue();
    }

    public BuyCommand withBuyout(Buyout buyout) {
        this.buyout = buyout;
        return this;
    }
}
