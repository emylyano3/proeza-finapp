package proeza.finapp.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity(name = "fin_activo")
@Table(name = "fin_activo")
public class Activo extends IdEntity<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "cartera_id", referencedColumnName = "id")
    private Cartera cartera;

    @ManyToOne(optional = false)
    @JoinColumn(name = "instrumento_id", referencedColumnName = "id")
    private Instrumento instrumento;

    @OneToMany(mappedBy = "activo", fetch = FetchType.LAZY)
    private List<MovimientoActivo> ventas;

    @OneToMany(mappedBy = "activo", fetch = FetchType.LAZY)
    private List<MovimientoActivo> compras;

    public Cartera getCartera() {
        return cartera;
    }

    public void setCartera(Cartera cartera) {
        this.cartera = cartera;
    }

    public Instrumento getInstrumento() {
        return instrumento;
    }

    public void setInstrumento(Instrumento instrumento) {
        this.instrumento = instrumento;
    }

    public List<MovimientoActivo> getVentas() {
        return ventas;
    }

    public void setVentas(List<MovimientoActivo> ventas) {
        this.ventas = ventas;
    }

    public List<MovimientoActivo> getCompras() {
        return compras;
    }

    public void setCompras(List<MovimientoActivo> compras) {
        this.compras = compras;
    }
}
