package proeza.finapp.entities;

import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@NoArgsConstructor
@DiscriminatorValue("V")
@Entity(name = "fin_Venta")
public class Venta extends MovimientoActivo {

}