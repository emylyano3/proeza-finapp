package proeza.finapp.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "fin_movimiento_activo")
@Table(name = "fin_movimiento_activo")
public class MovimientoActivo extends IdEntity<Long>{
    @ManyToOne(optional = false)
    @JoinColumn(name = "activo_id", referencedColumnName = "id")
    private Activo activo;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private BigDecimal precio;

    @Column(nullable = false)
    private String tipo;

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public TipoMovimiento getTipo() {
        return tipo == null ? null : TipoMovimiento.fromId(tipo);
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo == null ? null : tipo.getId();
    }
}
