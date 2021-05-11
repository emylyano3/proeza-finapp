package proeza.finapp.entities;

import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

@NoArgsConstructor
@DiscriminatorValue("E")
@Entity(name = "fin_Extraccion")
public class Extraccion extends MovimientoCuenta {
    public Extraccion(Cuenta cuenta, BigDecimal monto) {
        super(cuenta, monto, null);
    }
}