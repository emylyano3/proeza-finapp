package proeza.finapp.entities;

import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@NoArgsConstructor
@DiscriminatorValue("C")
@Entity(name = "fin_Compra")
public class Buy extends AssetMovement {

    public void setAsset(Asset asset) {
        super.setAsset(asset);
        getAsset().addBuy(this);
    }
}