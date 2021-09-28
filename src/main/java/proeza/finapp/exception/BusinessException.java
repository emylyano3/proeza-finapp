package proeza.finapp.exception;

import lombok.Getter;

@Getter
public class BusinessException extends Exception{
    private final ErrorTypes errorType;

    public BusinessException(String message, ErrorTypes errorType) {
        super(message);
        this.errorType = errorType;
    }
}
