package proeza.finapp.entities;

import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@NoArgsConstructor
@DiscriminatorValue("C")
@Entity(name = "fin_Compra")
public class Compra extends MovimientoActivo {

    public void setActivo (Activo activo) {
        super.setActivo(activo);
        getActivo().addCompra(this);
    }
}