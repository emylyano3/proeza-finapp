package proeza.finapp.entities;

import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

@NoArgsConstructor
@DiscriminatorValue("V")
@Entity(name = "fin_Venta")
public class Venta extends MovimientoActivo {

    /**
     * @return el monto operado quitando los cargos del movimiento
     */
    public BigDecimal getMontoNeto () {
        return getOperado().subtract(getMontoCargos());
    }
}