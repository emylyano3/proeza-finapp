package proeza.finapp.exception;

import proeza.finapp.domain.Buyout;
import proeza.finapp.domain.Sale;

public class BusinessErrorFactory {

    public static BusinessError accountInsufficientFundsToPurchaseError(Buyout buyout) {
        return BusinessError.builder()
                            .message(String.format(
                                    "The account %s has insufficient funds to process the instrument %s purchase",
                                    buyout.getPortfolio().getAccount().getNumber(),
                                    buyout.getAsset().getInstrument().getTicker()))
                            .type(ErrorTypes.INSUFFICIENT_FUNDS)
                            .build();
    }

    public static BusinessError portfolioInsufficientAssetsError(Sale sale) {
        return BusinessError.builder()
                            .message(String.format(
                                    "The portfolio %s has insufficient assets of type %s to process the sale",
                                    sale.getPortfolio().getName(),
                                    sale.getAsset().getInstrument().getTicker()))
                            .type(ErrorTypes.INSUFFICIENT_ASSETS)
                            .build();
    }
}
