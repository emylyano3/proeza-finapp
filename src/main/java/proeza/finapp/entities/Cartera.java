package proeza.finapp.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString(exclude = "activos")
@Entity(name = "fin_Cartera")
@Table(name = "fin_cartera", indexes = {@Index(columnList = "broker_id")})
public class Cartera extends IdEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "broker_id", referencedColumnName = "id")
    private Broker broker;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cuenta_id", referencedColumnName = "id")
    private Account account;

    @JsonManagedReference
    @OneToMany(mappedBy = "cartera", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Activo> activos = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cartera cartera = (Cartera) o;
        return Objects.equals(broker, cartera.broker) && Objects.equals(activos, cartera.activos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(broker, activos);
    }

    public void update(Activo activo) {
        if (!getActivos().contains(activo) && activo.getTenencia() > 0)
            getActivos().add(activo);
        else if (getActivos().contains(activo) && activo.getTenencia() == 0) {
            getActivos().remove(activo);
            activo.setCartera(null);
        }
    }
}