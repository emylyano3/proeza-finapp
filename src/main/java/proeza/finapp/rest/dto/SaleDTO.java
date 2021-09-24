package proeza.finapp.rest.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SaleDTO {
    private String ticker;
    private Long idCartera;
    private Integer cantidad;
    private BigDecimal precio;
    private LocalDateTime fecha;
}
