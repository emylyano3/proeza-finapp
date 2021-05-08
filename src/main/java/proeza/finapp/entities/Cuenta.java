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
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "fin_Cuenta")
@Table(name = "fin_cuenta")
public class Cuenta extends IdEntity<Long> {

    @Column(nullable = false, scale = 2, precision = 10)
    private BigDecimal saldo;

    @JsonIgnore
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL)
    private List<Cartera> carteras = new ArrayList<>(0);

    @JsonIgnore
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL)
    private List<MovimientoCuenta> movimientosCuenta = new ArrayList<>(0);

    @Transient
    public void acreditar(BigDecimal monto) {
        saldo = saldo.add(monto);
    }

    @Transient
    public void debitar(BigDecimal monto) {
        saldo = saldo.subtract(monto);
    }

    /**
     * Agrega el deposito a los movimientos asociados a la cuenta y actualiza el saldo.
     *
     * @param deposito El deposito a impactar en la cuenta
     */
    @Transient
    public void addDeposito(Deposito deposito) {
        movimientosCuenta.add(deposito);
        acreditar(deposito.getMonto());
    }

    /**
     * Agrega la extraccion a los movimientos asociados a la cuenta y actualiza el saldo.
     *
     * @param extraccion La extraccion a impactar en la cuenta
     */
    @Transient
    public void addExtraccion(Extraccion extraccion) {
        movimientosCuenta.add(extraccion);
        debitar(extraccion.getMonto());
    }
}
