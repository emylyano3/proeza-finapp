package proeza.finapp.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "fin_MovimientoCuenta")
@Table(name = "fin_movimiento_cuenta", indexes = {
        @Index(columnList = "cuenta_id")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", length = 1)
public abstract class MovimientoCuenta extends IdEntity<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "cuenta_id", referencedColumnName = "id")
    private Cuenta cuenta;

    @Column(nullable = false, scale = 2, precision = 10)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDateTime fecha;
}
