package proeza.finapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = {"chargeDetails", "portfolio"})
@Entity(name = "fin_MovimientoActivo")
@Table(name = "fin_movimiento_activo", indexes = {
        @Index(columnList = "cartera_id"),
        @Index(columnList = "activo_id")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", length = 1)
public class AssetMovement extends IdEntity<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "cartera_id", referencedColumnName = "id")
    private Portfolio portfolio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "activo_id", referencedColumnName = "id")
    private Asset asset;

    @Column(name="cantidad", nullable = false)
    private Integer quantity;

    @Column(name="precio", nullable = false, scale = 3, precision = 10)
    private BigDecimal price;

    @Column(name="fecha", nullable = false)
    private LocalDateTime date;

    @Column(name="cargos", scale = 3, precision = 10)
    private BigDecimal charges;

    @JsonIgnore
    @OneToMany(mappedBy = "movement", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<MovementCharge> chargeDetails = new HashSet<>();

    @JsonIgnore
    public BigDecimal getOperado() {
        return BigDecimal.valueOf(price.doubleValue() * quantity);
    }

    @JsonIgnore
    public BigDecimal getMovementTotal() {
        return charges.add(getOperado());
    }

    public void addCargo(Charge c, double monto) {
        BigDecimal toAdd = BigDecimal.valueOf(monto).setScale(3, RoundingMode.CEILING);
        charges = charges == null ? toAdd : charges.add(toAdd);
        MovementCharge cm = new MovementCharge();
        cm.setCharge(c);
        cm.setMovement(this);
        cm.setAmount(toAdd);
        chargeDetails.add(cm);
    }
}
