package proeza.finapp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "movement")
@Entity(name = "fin_CargoMovimiento")
@Table(name = "fin_cargo_movimiento", indexes = {
        @Index(columnList = "cargo_id"),
        @Index(columnList = "movimiento_id")
})
public class MovementCharge extends IdEntity<Long> {

    @ManyToOne
    @JoinColumn(name = "cargo_id", referencedColumnName = "id")
    private Charge charge;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "movimiento_id", referencedColumnName = "id")
    private AssetMovement movement;

    @Column(name="monto", nullable = false, scale = 3, precision = 10)
    private BigDecimal amount;
}