package proeza.finapp.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "fin_instrumento")
@Table(name = "fin_instrumento")
public class Instrumento extends IdEntity<Long>{
    @Column(nullable = false, length = 128, unique = true)
    private String nombre;

    @Column(nullable = false, length = 8, unique = true)
    private String ticker;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mercado_id", referencedColumnName = "id")
    private Mercado mercado;

    @Column
    private BigDecimal precio;

    @Column
    private LocalDateTime fechaPrecio;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Mercado getMercado() {
        return mercado;
    }

    public void setMercado(Mercado mercado) {
        this.mercado = mercado;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public LocalDateTime getFechaPrecio() {
        return fechaPrecio;
    }

    public void setFechaPrecio(LocalDateTime fechaPrecio) {
        this.fechaPrecio = fechaPrecio;
    }
}
