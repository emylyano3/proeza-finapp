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
import proeza.finapp.exception.ErrorTypes;
import proeza.finapp.patterns.command.ICommand;

import java.time.LocalDateTime;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class BuyCommand implements ICommand<Portfolio, BusinessError> {

    private Buyout buyout;

    @Override
    public Either<BusinessError, Portfolio> execute() {
        buyout.getPortfolio().getBroker().getCharges().forEach(c -> {
            double totalCargo = (c.getApplicableRate() - 1) * buyout.getOperado().doubleValue();
            buyout.addCargo(c, totalCargo);
        });
        if (buyout.getMovementTotal().doubleValue() > buyout.getPortfolio().getAccount().getBalance().doubleValue()) {
            //TODO Manejar esta validacion dentro de Buyout con spring
            return Either.left(BusinessError.builder()
                                            .message(String.format(
                                                    "The account %s has insufficient funds to process the instrument %s purchase",
                                                    buyout.getPortfolio().getAccount().getNumber(),
                                                    buyout.getAsset().getInstrument().getTicker()))
                                            .type(ErrorTypes.INSUFFICIENT_FUNDS)
                                            .build());
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

    public BuyCommand withBuyout(Buyout buyout) {
        this.buyout = buyout;
        return this;
    }
}
