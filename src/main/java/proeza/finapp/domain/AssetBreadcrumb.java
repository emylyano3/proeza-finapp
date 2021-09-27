package proeza.finapp.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.*;
import java.math.BigDecimal;

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
    @JoinColumn(name = "activo_id", referencedColumnName = "id")
    private Asset asset;

    @Column(name="vendido")
    private int sold;
    @Column(name="restante")
    private int remains;

    @Column(name="utilidad_promedio")
    private BigDecimal avgUtility;
    @Column(name="precio_venta_promedio")
    private BigDecimal avgSalePrice;
    @Column(name="cargos")
    private BigDecimal charges;
}