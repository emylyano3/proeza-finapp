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

import static proeza.finapp.domain.DecimalType.ASSET_PRICE;
import static proeza.finapp.domain.DecimalType.RATE;

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

    @Column(name = "tasa_utilidad")
    private BigDecimal utilityRate;

    @Column(name = "precio_compra", nullable = false)
    private BigDecimal buyPrice;

    @Column(name = "precio_venta_promedio")
    private BigDecimal avgSalePrice;

    @Column(name = "cargos_compra", nullable = false)
    private BigDecimal buyoutCharges;

    @Column(name = "cargos_venta")
    private BigDecimal saleCharges;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime buyoutDate;

    @Transient
    public BigDecimal getUtility(BigDecimal price) {
        return price.divide(buyPrice, ASSET_PRICE.scale(), ASSET_PRICE.roundingMode())
                    .subtract(BigDecimal.ONE);
    }

    @Transient
    public void updateWithSale(int quantityOperated, BigDecimal price, BigDecimal saleCharges) {
        this.avgSalePrice = calculateAverageSalePrice(this.sold, this.avgSalePrice, quantityOperated, price);//700
        this.remains -= quantityOperated; //5
        this.sold += quantityOperated;//5
        this.buyoutDate = LocalDateTime.now();
        this.saleCharges = this.saleCharges == null ? saleCharges : this.saleCharges.add(saleCharges);//24.558
        BigDecimal operatedTotalCharges = calculateOperatedTotalCharges(this.sold, this.saleCharges, this.buyoutCharges, this.sold + this.remains);//45.978
        BigDecimal operatedTotalSpent = calculateOperatedTotalSpent(this.sold, this.buyPrice, operatedTotalCharges);//3098.478
        BigDecimal operatedTotalIncome = calculateOperatedTotalIncome(this.sold, this.avgSalePrice);//3500
        this.utility = calculateNetUtility(operatedTotalIncome, operatedTotalSpent);//401.522
        this.utilityRate = calculateUtilityRate(this.utility, operatedTotalSpent);// 0.12
    }

    /**
     * Calcula el precio de venta promedio contamplando las ventas realizadas anteriormente (precio y cantidad).
     *
     * @param previouslySold Las unidades vendidas previamente
     * @param oldPrice       El precio al que se vendieron las unidades anteriores
     * @param currentlySold  Lo que se vendio en la operacion actual
     * @param currentPrice   El precio operado en la operacion actual
     */
    private BigDecimal calculateAverageSalePrice(int previouslySold, BigDecimal oldPrice, int currentlySold, BigDecimal currentPrice) {
        oldPrice = oldPrice == null ? BigDecimal.ZERO : oldPrice;
        double totalPayed = oldPrice.doubleValue() * previouslySold + currentPrice.doubleValue() * currentlySold;
        return BigDecimal.valueOf(totalPayed / (previouslySold + currentlySold))
                         .setScale(ASSET_PRICE.scale(), ASSET_PRICE.roundingMode());
    }

    /**
     * Calcula el total gastado en la unidades operadas
     * @param quantityOperated La cantidad de unidades operadas
     * @param buyPrice El precio de compra de las unidades
     * @param charges Los cargos asociados a la compra venta de las unidades de operadas
     */
    private BigDecimal calculateOperatedTotalSpent(int quantityOperated, BigDecimal buyPrice, BigDecimal charges) {
        return BigDecimal.valueOf(buyPrice.doubleValue() * quantityOperated + charges.doubleValue());
    }

    /**
     * Calcula los cargos de lo operado hasta el momento, considerando los cargos por venta mas los cargos por compra de solo las unidades operadas en la venta.
     *
     * @param quantityOperated Las unidades operadas
     * @param saleCharges      Los cargos por la venta de las unidades operadas
     * @param buyoutCharges    Los cargos por la compra de todas las unidades
     * @param totalPosition    El total de unidades que se compraron
     */
    private BigDecimal calculateOperatedTotalCharges(int quantityOperated, BigDecimal saleCharges, BigDecimal buyoutCharges, int totalPosition) {
        return BigDecimal.valueOf(saleCharges.doubleValue() + buyoutCharges.doubleValue() / totalPosition * quantityOperated);
    }

    /**
     * Calcula la entrada bruta por la venta de las unidades
     */
    private BigDecimal calculateOperatedTotalIncome(int quantityOperated, BigDecimal salePrice) {
        return BigDecimal.valueOf(salePrice.doubleValue() * quantityOperated);
    }

    /**
     * Calcula la utilidad neta de las unidades operadas, contemplando los costos por la compra del activo, los cargos por la compra y venta
     * (solo de los activos operados) y el precio promedio de venta
     *
     * @param in  Lo entrada bruta por la venta de las unidades
     * @param out La salida bruta por la compra de las unidades
     */
    private BigDecimal calculateNetUtility(BigDecimal in, BigDecimal out) {
        return in.subtract(out);
    }

    /**
     * Calcula la tasa de rendimiento
     */
    private BigDecimal calculateUtilityRate(BigDecimal netUtility, BigDecimal totalSpent) {
        return netUtility.divide(totalSpent, RATE.scale(), RATE.roundingMode());
    }

    @Transient
    public BigDecimal getDailyUtilityRateSinceBuyDate(BigDecimal price) {
        Duration duration = Duration.between(buyoutDate, LocalDateTime.now());
        return price.divide(buyPrice, RATE.scale(), RATE.roundingMode())
                    .subtract(BigDecimal.ONE)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(duration.toDays()), RATE.scale(), RATE.roundingMode());
    }
}