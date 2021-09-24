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

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false, scale = 6, precision = 6)
    private BigDecimal tasa;

    @Column(scale = 6, precision = 6)
    private BigDecimal iva;

    public ChargeType getTipo() {
        return tipo == null ? null : ChargeType.fromId(tipo);
    }

    public void setTipo(ChargeType tipo) {
        this.tipo = tipo == null ? null : tipo.getId();
    }

    /**
     * Devuelve la tasa total aplicable sobre el movimiento.
     * Ejemplo: monto * tasaAplicable = total
     */
    @Transient
    @JsonIgnore
    public double getTasaAplicable() {
        return 1 + (1 + iva.doubleValue()) * tasa.doubleValue();
    }
}