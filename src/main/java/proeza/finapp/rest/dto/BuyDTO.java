package proeza.finapp.rest.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BuyDTO {
    @NotBlank
    @Size(min = 1, max = 10)
    private String ticker;
    @NotNull
    private Long idCartera;
    @NotNull
    @PositiveOrZero
    private Integer cantidad;
    @NotNull
    @Positive
    private BigDecimal precio;
    private LocalDateTime fecha;
}
