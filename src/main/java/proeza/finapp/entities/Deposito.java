package proeza.finapp.entities;

import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

@NoArgsConstructor
@DiscriminatorValue("D")
@Entity(name = "fin_Deposito")
public class Deposito extends MovimientoCuenta {
    public Deposito(Cuenta cuenta, BigDecimal monto) {
        super(cuenta, monto, null);
    }
}