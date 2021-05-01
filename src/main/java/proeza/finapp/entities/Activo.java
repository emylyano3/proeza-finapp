package proeza.finapp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Where;
import org.jetbrains.annotations.NotNull;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity(name = "fin_Activo")
@Table(name = "fin_activo", indexes = {
        @Index(columnList = "cartera_id"),
        @Index(columnList = "instrumento_id")
})
public class Activo extends IdEntity<Long> {

    public Activo(Cartera cartera, Instrumento instrumento) {
        this.cartera = cartera;
        this.instrumento = instrumento;
        this.tenencia = 0;
    }

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "cartera_id", referencedColumnName = "id")
    private Cartera cartera;

    @ManyToOne(optional = false)
    @JoinColumn(name = "instrumento_id", referencedColumnName = "id")
    private Instrumento instrumento;

    @JsonIgnore
    @Where(clause = "tipo = 'V'")
    @OneToMany(mappedBy = "activo", fetch = FetchType.LAZY)
    private List<Venta> ventas = new ArrayList<>();

    @JsonIgnore
    @Where(clause = "tipo = 'C'")
    @OneToMany(mappedBy = "activo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Compra> compras = new ArrayList<>();

    @Getter
    @Column
    private BigDecimal ppc;

    @Column
    private Integer tenencia;

    @Transient
    public void addMovimiento(MovimientoActivo movimiento) {
        addMovementActions.get(movimiento.getClass()).accept(movimiento);
    }

    @Transient
    private Map<Class<?>, Consumer<MovimientoActivo>> addMovementActions = Map.of(
            Venta.class, getVentaAction(),
            Compra.class, getCompraAction()
    );

    @NotNull
    @Transient
    private Consumer<MovimientoActivo> getCompraAction() {
        return movimientoActivo -> {
            Compra compra = (Compra) movimientoActivo;
            compras.add(compra);
            int cantidad = 0;
            double volumen = 0;
            for (Compra c : compras) {
                cantidad += c.getCantidad();
                volumen += c.getCantidad() * c.getPrecio().doubleValue();
            }
            ppc = BigDecimal.valueOf(volumen / cantidad);
            tenencia += compra.getCantidad();
        };
    }

    @NotNull
    @Transient
    private Consumer<MovimientoActivo> getVentaAction() {
        return movimientoActivo -> {
            Venta venta = (Venta) movimientoActivo;
            ventas.add(venta);
            tenencia -= venta.getCantidad();
        };
    }
}