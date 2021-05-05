package proeza.finapp.entities;

import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@NoArgsConstructor
@DiscriminatorValue("D")
@Entity(name = "fin_Deposito")
public class Deposito extends MovimientoCuenta {
}