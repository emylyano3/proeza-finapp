package proeza.finapp.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BusinessError {
    private ErrorTypes type;
    private String message;
}
