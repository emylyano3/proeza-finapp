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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity(name = "fin_MovimientoActivo")
@Table(name = "fin_movimiento_activo", indexes = {
        @Index(columnList = "cartera_id"),
        @Index(columnList = "activo_id"),
        @Index(columnList = "instrumento_id")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", length = 1)
public class MovimientoActivo extends IdEntity<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "cartera_id", referencedColumnName = "id")
    private Cartera cartera;

    @ManyToOne(optional = false)
    @JoinColumn(name = "activo_id", referencedColumnName = "id")
    private Activo activo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "instrumento_id", referencedColumnName = "id")
    private Instrumento instrumento;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, scale = 2, precision = 10)
    private BigDecimal precio;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(scale = 2, precision = 10)
    private BigDecimal montoCargos;

    @JsonIgnore
    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<CargoMovimiento> detalleCargos = new HashSet<>();

    public void addCargo(Cargo c, BigDecimal monto) {
        montoCargos = montoCargos == null ? monto : montoCargos.add(monto);
        CargoMovimiento cm = new CargoMovimiento();
        cm.setCargo(c);
        cm.setMovimiento(this);
        cm.setMonto(monto);
        detalleCargos.add(cm);
    }
}
