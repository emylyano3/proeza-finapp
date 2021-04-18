package proeza.finapp.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Table(name = "fin_cartera")
@Entity(name = "fin_Cartera")
public class Cartera extends IdEntity<Long> {
    @OneToMany(mappedBy = "cartera", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Activo> activos;

    public List<Activo> getActivos() {
        return activos;
    }

    public void setActivos(List<Activo> activos) {
        this.activos = activos;
    }
}


