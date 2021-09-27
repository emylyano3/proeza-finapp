package proeza.finapp.rest.handler;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException e) {
        ApiError error = ApiError.builder()
                                 .message(e.getMessage())
                                 .status(HttpStatus.NOT_FOUND)
                                 .build();
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleResourceNotFoundException(EntityNotFoundException e) {
        ApiError error = ApiError.builder()
                                 .message(e.getMessage())
                                 .status(HttpStatus.NOT_FOUND)
                                 .build();
        return new ResponseEntity<>(error, error.getStatus());
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
}
