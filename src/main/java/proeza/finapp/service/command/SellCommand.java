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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SellCommand implements ICommand<Portfolio, BusinessError> {

    private Sale sale;

    private final Map<Predicate<Sale>, Function<Sale, BusinessError>> validations = Map.of(
            this::insufficientAssets, BusinessErrorFactory::portfolioInsufficientAssetsError
    );

    @Override
    @Transactional
    public Either<BusinessError, Portfolio> execute() {
        Objects.requireNonNull(sale, "Sale can not be null");
        calculateCharges();
        var opValidationError = processValidations();
        if (opValidationError.isPresent()) {
            return Either.left(opValidationError.get());
        }
        updatePortfolio();
        updateBreadcrumb();
        doAccountDeposit();
        return Either.right(sale.getPortfolio());
    }

    private void doAccountDeposit() {
        Account account = sale.getPortfolio().getAccount();
        Deposit deposit = new Deposit();
        deposit.setAccount(account);
        deposit.setDate(LocalDateTime.now());
        deposit.setAmount(sale.getNetAmount());
        account.apply(deposit);
    }

    private void updatePortfolio() {
        sale.getPortfolio().update(sale.getAsset());
    }

    private void updateBreadcrumb() {
        List<AssetBreadcrumb> sortedBreadcrumb = sale.getAsset().getBreadcrumb().stream()
                                                     .sorted(this::moreUtilityComparator)
                                                     .collect(Collectors.toList());
        int i = 0, toProrate = sale.getQuantity();
        while (toProrate > 0 && i < sortedBreadcrumb.size()) {
            int sold = Math.min(toProrate, sortedBreadcrumb.get(i).getRemains());
            toProrate -= sold;
            sortedBreadcrumb.get(i).updateWithSale(sold, sale.getPrice(), sale.getCharges());
        }
    }

    /**
     * Devuelve -1, 0, 1 si el breadcrumb_1 tiene una utilidad diaria mayor, igual o menor al breadcrumb_2
     */
    private int moreUtilityComparator(AssetBreadcrumb _1, AssetBreadcrumb _2) {
        return -_1.getDailyUtilitySinceBuyDate(sale.getPrice()).compareTo(_2.getDailyUtilitySinceBuyDate(sale.getPrice()));
    }

    private void calculateCharges() {
        //TODO Verificar si es un movimiento intradiario para aplicar o no los nuevos cargos.
        //TODO Agregar en el broker un flag para saber si aplica o no la exencion de cargo en movs intradiarios
        sale.getPortfolio().getBroker().getCharges().forEach(c -> {
            double totalCargo = (c.getApplicableRate() - 1) * sale.getOperated().doubleValue();
            sale.addCharge(c, totalCargo);
        });
    }

    private Optional<BusinessError> processValidations() {
        return validations.entrySet().stream()
                          .filter(e -> e.getKey().test(sale))
                          .findAny()
                          .map(Map.Entry::getValue)
                          .map(f -> f.apply(sale));
    }

    private boolean insufficientAssets(Sale sale) {
        return sale.getQuantity() > sale.getAsset().getHolding() ||
                sale.getQuantity() > sale.getAsset().getBreadcrumb().stream()
                                         .map(AssetBreadcrumb::getRemains)
                                         .reduce(0, Integer::sum);
    }

    public SellCommand withSale(Sale sale) {
        this.sale = sale;
        return this;
    }
}
