package proeza.finapp.exception;

import org.springframework.http.HttpStatus;

public enum ErrorTypes {
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "1000"),
    INSUFFICIENT_ASSETS(HttpStatus.CONFLICT, "2000")
    ;

    private final HttpStatus httpStatus;
    private final String code;

    ErrorTypes(HttpStatus httpStatus, String code) {
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

}
