package proeza.finapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    public BigDecimal getMontoNeto () {
        return getOperado().subtract(getCargos());
    }

    public void setActivo (Activo activo) {
        super.setActivo(activo);
        getActivo().addVenta(this);
    }
}