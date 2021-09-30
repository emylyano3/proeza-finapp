package proeza.finapp.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "fin_activo_historial", indexes = {
        @Index(columnList = "id"),
        @Index(columnList = "activo_id")
})
@Entity(name = "fin_HistoriaActivo")
public class AssetBreadcrumb extends IdEntity<Long> {

    @ManyToOne
    @JoinColumn(name = "activo_id", referencedColumnName = "id", nullable = false)
    private Asset asset;

    @Column(name = "vendido", nullable = false)
    private int sold;

    @Column(name = "restante", nullable = false)
    private int remains;

    @Column(name = "utilidad")
    private BigDecimal utility;

    @Column(name = "precio_compra", nullable = false)
    private BigDecimal buyPrice;

    @Column(name = "precio_venta_promedio")
    private BigDecimal avgSalePrice;

    @Column(name = "cargos", nullable = false)
    private BigDecimal charges;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime buyoutDate;

    @Transient
    public BigDecimal getUtility(BigDecimal price) {
        return price.divide(buyPrice, ValueScale.ASSET_PRICE_SCALE.getScale(), ValueScale.ASSET_PRICE_SCALE.getRoundingMode())
                    .subtract(BigDecimal.ONE);
    }

    @Transient
    public void updateWithSale(int quantitySold, BigDecimal price, BigDecimal charges) {
        this.avgSalePrice = calculateAverageSalePrice(this.sold, this.avgSalePrice, quantitySold, price);//700
        this.remains -= quantitySold; //5
        this.sold += quantitySold;//5
        this.buyoutDate = LocalDateTime.now();
        this.charges = this.charges == null ? charges : this.charges.add(charges);
        this.utility = calculateUtility(sold, avgSalePrice, buyPrice, charges);//422.94
    }

    private BigDecimal calculateAverageSalePrice(int previouslySold, BigDecimal oldAvgPrice, int currentlySold, BigDecimal price) {
        oldAvgPrice = oldAvgPrice == null ? BigDecimal.ZERO : oldAvgPrice;
        double totalPayed = oldAvgPrice.doubleValue() * previouslySold + price.doubleValue() * currentlySold;
        return BigDecimal.valueOf(totalPayed / (previouslySold + currentlySold))
                         .setScale(ValueScale.ASSET_PRICE_SCALE.getScale(), ValueScale.ASSET_PRICE_SCALE.getRoundingMode());
    }

    private BigDecimal calculateUtility(int quantitySold, BigDecimal avgSalePrice, BigDecimal buyPrice, BigDecimal charges) {
        return BigDecimal.valueOf(quantitySold * avgSalePrice.doubleValue() - quantitySold * buyPrice.doubleValue())
                         .subtract(charges);
    }

    @Transient
    public BigDecimal getDailyUtilitySinceBuyDate(BigDecimal price) {
        ValueScale scale = ValueScale.ASSET_PRICE_SCALE;
        Duration duration = Duration.between(buyoutDate, LocalDateTime.now());
        return price.divide(buyPrice, scale.getScale(), scale.getRoundingMode())
                    .subtract(BigDecimal.ONE)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(duration.toDays()), scale.getScale(), scale.getRoundingMode());
    }
}