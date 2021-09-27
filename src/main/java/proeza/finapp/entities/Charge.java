package proeza.finapp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@ToString(exclude = "broker")
@Entity(name = "fin_Cargo")
@Table(name = "fin_cargo", indexes = {@Index(columnList = "broker_id")})
public class Charge extends IdEntity<Long> {

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "broker_id", referencedColumnName = "id")
    private Broker broker;

    @Column(name="tipo", nullable = false)
    private String type;

    @Column(name="tasa", nullable = false, scale = 6, precision = 6)
    private BigDecimal rate;

    @Column(name="iva", scale = 6, precision = 6)
    private BigDecimal tax;

    public ChargeType getType() {
        return type == null ? null : ChargeType.fromId(type);
    }

    public void setType(ChargeType type) {
        this.type = type == null ? null : type.getId();
    }

    /**
     * Devuelve la tasa total aplicable sobre el movimiento.
     * Ejemplo: monto * tasaAplicable = total
     */
    @Transient
    @JsonIgnore
    public double getApplicableRate() {
        return 1 + (1 + tax.doubleValue()) * rate.doubleValue();
    }
}