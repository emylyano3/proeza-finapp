package proeza.finapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"portfolio", "sales", "buyouts", "breadcrumb"})
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

    @JsonIgnore
    @Where(clause = "restante > 0")
    @OneToMany(mappedBy = "asset", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AssetBreadcrumb> breadcrumb = new ArrayList<>();

    @Getter
    @Column(name="ppc", scale = 2)
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
            chargeBuyout(buyout);
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
            chargeSale(sale);
        }
    }

    @Transient
    private void chargeBuyout(Buyout buyout) {
        int quantity = 0;
        double volume = 0;
        for (Buyout c : buyouts) {
            quantity += c.getQuantity();
            volume += c.getQuantity() * c.getPrice().doubleValue();
        }
        avgPrice = BigDecimal.valueOf(volume / quantity)
                             .setScale(ValueScale.PRICE_SCALE.getScale(), ValueScale.PRICE_SCALE.getRoundingMode());
        holding += buyout.getQuantity();
    }

    @Transient
    private void chargeSale(Sale sale) {
        sales.add(sale);
        holding -= sale.getQuantity();
    }

    @Transient
    public void addBreadcrumb(AssetBreadcrumb bc) {
        breadcrumb.add(bc);
    }
}