package proeza.finapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity(name = "fin_Broker")
@Table(name = "fin_broker")
public class Broker extends IdEntity<Long> {
    @Column(nullable = false, length = 32, unique = true)
    private String codigo;

    @Column(nullable = false, length = 256, unique = true)
    private String nombre;

    @JsonIgnore
    @OneToMany(mappedBy = "broker")
    private Set<Cargo> cargos = new HashSet<>();
}