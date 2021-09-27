package proeza.finapp.domain;

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
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"portfolio", "sales", "buyouts"})
@Entity(name = "fin_Activo")
@Table(name = "fin_activo", indexes = {
        @Index(columnList = "cartera_id"),
        @Index(columnList = "instrumento_id")
})
public class Asset extends IdEntity<Long> {

    public Asset(Portfolio portfolio, Instrument instrument) {
        this.portfolio = portfolio;
        this.instrument = instrument;
        this.holding = 0;
    }

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "cartera_id", referencedColumnName = "id")
    private Portfolio portfolio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "instrumento_id", referencedColumnName = "id")
    private Instrument instrument;

    @JsonIgnore
    @Where(clause = "tipo = 'V'")
    @OneToMany(mappedBy = "asset", fetch = FetchType.LAZY)
    private List<Sale> sales = new ArrayList<>();

    @JsonIgnore
    @Where(clause = "tipo = 'C'")
    @OneToMany(mappedBy = "asset", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Buyout> buyouts = new ArrayList<>();

    @Getter
    @Column(name="ppc")
    private BigDecimal avgPrice;

    @Column(name="tenencia")
    private Integer holding;

    /**
     * Agrega la compra a las compras del activo y adicionalmente la imputa en los valores del activo. Tenencia, ppc, etc.
     *
     * @param buyout la compra a imputar
     */
    @Transient
    public void addBuyout(Buyout buyout) {
        if (buyout != null && buyouts.stream().noneMatch(c -> Objects.equals(c, buyout))) {
            buyouts.add(buyout);
            imputarCompra(buyout);
        }
    }

    /**
     * Agrega la venta a las ventas del activo y adicionalmente la imputa actualizando la tenencia.
     *
     * @param sale la venta a imputar
     */
    @Transient
    public void addSale(Sale sale) {
        if (sale != null && sales.stream().noneMatch(v -> Objects.equals(v, sale))) {
            sales.add(sale);
            imputarVenta(sale);
        }
    }

    @Transient
    private void imputarCompra(Buyout buyout) {
        int cantidad = 0;
        double volumen = 0;
        for (Buyout c : buyouts) {
            cantidad += c.getQuantity();
            volumen += c.getQuantity() * c.getPrice().doubleValue();
        }
        avgPrice = BigDecimal.valueOf(volumen / cantidad);
        holding += buyout.getQuantity();
    }

    @Transient
    private void imputarVenta(Sale sale) {
        sales.add(sale);
        holding -= sale.getQuantity();
    }
}