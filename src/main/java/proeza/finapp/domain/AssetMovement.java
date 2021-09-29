package proeza.finapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.*;
import java.math.BigDecimal;
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
    private Set<ChargeMovement> chargeDetails = new HashSet<>();

    /**
     * El precio del activo * cantidad
     */
    @JsonIgnore
    public BigDecimal getOperated() {
        return BigDecimal.valueOf(price.doubleValue() * quantity);
    }

    /**
     * El volumen operado + los cargos
     */
    @JsonIgnore
    public BigDecimal getMovementTotal() {
        return charges.add(getOperated());
    }

    public void addCharge(Charge c, double amount) {
        BigDecimal toAdd = BigDecimal.valueOf(amount)
                                     .setScale(ValueScale.CHARGE_SCALE.getScale(), ValueScale.CHARGE_SCALE.getRoundingMode());
        charges = charges == null ? toAdd : charges.add(toAdd);
        ChargeMovement cm = new ChargeMovement();
        cm.setCharge(c);
        cm.setMovement(this);
        cm.setAmount(toAdd);
        chargeDetails.add(cm);
    }
}
