package proeza.finapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

@NoArgsConstructor
@DiscriminatorValue("V")
@Entity(name = "fin_Venta")
public class Sale extends AssetMovement {

    /**
     * @return el monto operado quitando los cargos del movimiento
     */
    @JsonIgnore
    public BigDecimal getNetAmount() {
        return getOperated().subtract(getCharges());
    }

    public void setAsset(Asset asset) {
        super.setAsset(asset);
    }
}