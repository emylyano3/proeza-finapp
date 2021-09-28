package proeza.finapp.rest.handler;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import proeza.finapp.exception.ApiError;
import proeza.finapp.exception.BusinessException;
import proeza.finapp.exception.ErrorTypes;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException e) {
        return mapToNotFoundStatus(e);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleResourceNotFoundException(EntityNotFoundException e) {
        return mapToNotFoundStatus(e);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleBusinessException(BusinessException e) {
        return buildResponseEntity(ApiError.builder()
                                           .cause(Optional.ofNullable(e.getCause()).map(Throwable::getMessage).orElse("n/a"))
                                           .message(e.getMessage())
                                           .httpStatus(e.getErrorType().getHttpStatus())
                                           .code(e.getErrorType().getCode())
                                           .build());
    }

    @NotNull
    private ResponseEntity<ApiError> mapToNotFoundStatus(Exception e) {
        return buildResponseEntity(ApiError.builder()
                                           .message(e.getMessage())
                                           .cause(Optional.ofNullable(e.getCause()).map(Throwable::getMessage).orElse("n/a"))
                                           .httpStatus(ErrorTypes.RESOURCE_NOT_FOUND.getHttpStatus())
                                           .code(ErrorTypes.RESOURCE_NOT_FOUND.getCode())
                                           .build());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().stream()
         .map(err -> err instanceof FieldError ? (FieldError) err : null)
         .filter(Objects::nonNull)
         .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return errors;
    }

    @NotNull
    private ResponseEntity<ApiError> buildResponseEntity(ApiError error) {
        return new ResponseEntity<>(error, error.getHttpStatus());
    }
}
