package proeza.finapp.exception;

import proeza.finapp.domain.Instrument;
import proeza.finapp.domain.Portfolio;

import javax.persistence.EntityNotFoundException;

public class ExceptionFactory {
    public static BusinessException newBusinessException(BusinessError e) {
        return new BusinessException(e.getMessage(), e.getType());
    }

    public static EntityNotFoundException newEntityNotFoundException(String name) {
        return new EntityNotFoundException(String.format("%s not found", name));
    }

    public static EntityNotFoundException newInstrumentNotFoundException() {
        return newEntityNotFoundException(Instrument.class.getSimpleName());
    }

    public static EntityNotFoundException newPortfolioNotFoundException() {
        return newEntityNotFoundException(Portfolio.class.getSimpleName());
    }
}
