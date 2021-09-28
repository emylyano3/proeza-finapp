package proeza.finapp.exception;

public class ExceptionFactory {
    public static BusinessException newSellException(BusinessError e) {
        return new BusinessException(e.getMessage(), e.getType());
    }
}
