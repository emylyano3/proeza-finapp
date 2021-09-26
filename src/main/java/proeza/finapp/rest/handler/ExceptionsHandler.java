package proeza.finapp.rest.handler;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
}
