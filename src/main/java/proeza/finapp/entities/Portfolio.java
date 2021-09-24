package proeza.finapp.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString(exclude = "assets")
@Entity(name = "fin_Cartera")
@Table(name = "fin_cartera", indexes = {@Index(columnList = "broker_id")})
public class Portfolio extends IdEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "broker_id", referencedColumnName = "id")
    private Broker broker;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cuenta_id", referencedColumnName = "id")
    private Account account;

    @JsonManagedReference
    @OneToMany(mappedBy = "portfolio", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Asset> assets = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Portfolio portfolio = (Portfolio) o;
        return Objects.equals(broker, portfolio.broker) && Objects.equals(assets, portfolio.assets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(broker, assets);
    }

    public void update(Asset asset) {
        if (!getAssets().contains(asset) && asset.getHolding() > 0)
            getAssets().add(asset);
        else if (getAssets().contains(asset) && asset.getHolding() == 0) {
            getAssets().remove(asset);
            asset.setPortfolio(null);
        }
    }
}