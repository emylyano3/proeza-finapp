package proeza.finapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity(name = "fin_Cuenta")
@Table(name = "fin_cuenta")
public class Cuenta extends IdEntity<Long> {

    @Column(nullable = false, scale = 2, precision = 10)
    private BigDecimal saldo;

    @JsonIgnore
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL)
    private Set<Cartera> carteras = new HashSet<>(0);

    @JsonIgnore
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL)
    private Set<MovimientoCuenta> movimientosCuenta = new HashSet<>(0);

    @Transient
    public void acreditar(BigDecimal monto) {
        saldo = saldo.add(monto);
    }

    @Transient
    public void debitar(BigDecimal monto) {
        saldo = saldo.subtract(monto);
    }

    /**
     * Agrega el movimiento a la cuenta y actualiza el saldo de acuerdo al tipo de movimiento.
     */
    @Transient
    public void addMovimiento(MovimientoCuenta movimiento) {
        movimientosCuenta.add(movimiento);
        if (movimiento instanceof Deposito) {
            movimiento.getCuenta().acreditar(movimiento.getMonto());
        } else {
            movimiento.getCuenta().debitar(movimiento.getMonto());
        }
    }
}
