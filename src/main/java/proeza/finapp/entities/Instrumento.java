package proeza.finapp.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity(name = "fin_Instrumento")
@Table(name = "fin_instrumento", indexes = {@Index(columnList = "mercado_id"),@Index(columnList = "ticker")})
public class Instrumento extends IdEntity<Long> {
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
}
