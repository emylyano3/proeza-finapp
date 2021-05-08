package proeza.finapp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Where;

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

    /**
     * Agrega la compra a las compras del activo y adicionalmente la imputa en los valores del activo. Tenencia, ppc, etc.
     *
     * @param compra la compra a imputar
     */
    @Transient
    public void addCompra(Compra compra) {
        compras.add(compra);
        imputarCompra(compra);
    }

    /**
     * Agrega la venta a las ventas del activo y adicionalmente la imputa actualizando la tenencia.
     *
     * @param venta la venta a imputar
     */
    @Transient
    public void addVenta(Venta venta) {
        ventas.add(venta);
        imputarVenta(venta);
    }

    @Transient
    private void imputarCompra(Compra compra) {
        compras.add(compra);
        int cantidad = 0;
        double volumen = 0;
        for (Compra c : compras) {
            cantidad += c.getCantidad();
            volumen += c.getCantidad() * c.getPrecio().doubleValue();
        }
        ppc = BigDecimal.valueOf(volumen / cantidad);
        tenencia += compra.getCantidad();
    }

    @Transient
    private void imputarVenta(Venta venta) {
        ventas.add(venta);
        tenencia -= venta.getCantidad();
    }
}