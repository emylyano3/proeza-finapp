package proeza.finapp.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@Entity(name = "fin_Mercado")
@Table(name = "fin_mercado", indexes = {@Index(columnList = "pais_id")})
public class Mercado extends IdEntity<Long> {
    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false, unique = true)
    private String nombre;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pais_id", referencedColumnName = "id")
    private Pais pais;
}
