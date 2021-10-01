package proeza.finapp.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SellDTO {
    @NotBlank
    @Size(min = 1, max = 10)
    private String ticker;
    @NotNull
    private Long idCartera;
    @NotNull
    @PositiveOrZero
    private Integer cantidad;
    @NotNull
    @PositiveOrZero
    private BigDecimal precio;
    private LocalDateTime fecha;
}
