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
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"portfolio", "sales", "buys"})
@Entity(name = "fin_Activo")
@Table(name = "fin_activo", indexes = {
        @Index(columnList = "cartera_id"),
        @Index(columnList = "instrumento_id")
})
public class Asset extends IdEntity<Long> {

    public Asset(Portfolio portfolio, Instrument instrument) {
        this.portfolio = portfolio;
        this.instrument = instrument;
        this.tenencia = 0;
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
    private List<Buy> buys = new ArrayList<>();

    @Getter
    @Column
    private BigDecimal ppc;

    @Column
    private Integer tenencia;

    /**
     * Agrega la compra a las compras del activo y adicionalmente la imputa en los valores del activo. Tenencia, ppc, etc.
     *
     * @param buy la compra a imputar
     */
    @Transient
    public void addBuy(Buy buy) {
        if (buy != null && buys.stream().noneMatch(c -> Objects.equals(c, buy))) {
            buys.add(buy);
            imputarCompra(buy);
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
    private void imputarCompra(Buy buy) {
        int cantidad = 0;
        double volumen = 0;
        for (Buy c : buys) {
            cantidad += c.getQuantity();
            volumen += c.getQuantity() * c.getPrice().doubleValue();
        }
        ppc = BigDecimal.valueOf(volumen / cantidad);
        tenencia += buy.getQuantity();
    }

    @Transient
    private void imputarVenta(Sale sale) {
        sales.add(sale);
        tenencia -= sale.getQuantity();
    }
}