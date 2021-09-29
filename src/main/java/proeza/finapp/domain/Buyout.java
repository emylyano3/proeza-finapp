package proeza.finapp.domain;

import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@NoArgsConstructor
@DiscriminatorValue("C")
@Entity(name = "fin_Compra")
public class Buyout extends AssetMovement {

    public void setAsset(Asset asset) {
        super.setAsset(asset);
    }
}