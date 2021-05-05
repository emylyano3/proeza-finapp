package proeza.finapp.entities;

import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@NoArgsConstructor
@DiscriminatorValue("E")
@Entity(name = "fin_Extraccion")
public class Extraccion extends MovimientoCuenta {
    public Extraccion(MovimientoActivo movimientoActivo) {
        super(movimientoActivo);
    }
}