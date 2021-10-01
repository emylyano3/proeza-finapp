package proeza.finapp.service.command;

import io.vavr.control.Either;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import proeza.finapp.domain.*;
import proeza.finapp.exception.BusinessError;
import proeza.finapp.exception.BusinessErrorFactory;
import proeza.finapp.patterns.command.ICommand;

import javax.transaction.Transactional;
import java.math.BigDecimal;
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
        calculateCharges();
        var opValidationError = processValidations();
        if (opValidationError.isPresent()) {
            return Either.left(opValidationError.get());
        }
        updatePortfolio();
        createBreadcrumb();
        doAccountWithdrawal();
        return Either.right(buyout.getPortfolio());
    }

    private void createBreadcrumb() {
        AssetBreadcrumb breadcrumb = new AssetBreadcrumb();
        breadcrumb.setAsset(buyout.getAsset());
        breadcrumb.setSold(0);
        breadcrumb.setRemains(buyout.getQuantity());
        breadcrumb.setBuyoutCharges(buyout.getCharges());
        breadcrumb.setUtility(BigDecimal.ZERO);
        breadcrumb.setAvgSalePrice(BigDecimal.ZERO);
        buyout.getAsset().addBreadcrumb(breadcrumb);
    }

    private void calculateCharges() {
        buyout.getPortfolio().getBroker().getCharges().forEach(c -> {
            double totalCharge = (c.getApplicableRate() - 1) * buyout.getOperated().doubleValue();
            buyout.addCharge(c, totalCharge);
        });
    }

    private void updatePortfolio() {
        buyout.getPortfolio().update(buyout.getAsset());
    }

    private void doAccountWithdrawal() {
        Account account = buyout.getPortfolio().getAccount();
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setAccount(account);
        withdrawal.setDate(LocalDateTime.now());
        withdrawal.setAmount(buyout.getMovementTotal());
        account.apply(withdrawal);
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
