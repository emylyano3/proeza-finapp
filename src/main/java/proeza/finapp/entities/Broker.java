package proeza.finapp.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = "charges")
@Entity(name = "fin_Broker")
@Table(name = "fin_broker")
public class Broker extends IdEntity<Long> {
    @Column(name="codigo", nullable = false, length = 32, unique = true)
    private String code;

    @Column(name="nombre", nullable = false, length = 256, unique = true)
    private String name;

    @OneToMany(mappedBy = "broker")
    @Cascade(CascadeType.ALL)
    private Set<Charge> charges = new HashSet<>();
}