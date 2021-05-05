package proeza.finapp.command;

import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import proeza.finapp.entities.Cartera;
import proeza.finapp.entities.MovimientoActivo;
import proeza.finapp.repository.CarteraRepository;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Builder
public class CargosCommand extends CarteraAbstractCommand {
    private final MovimientoActivo movimiento;

    @Autowired
    private CarteraRepository carteraRepository;

    private Cartera cartera;

    protected void businessLogic() {
        double operado = movimiento.getPrecio().doubleValue() * movimiento.getCantidad();
        //TODO Verificar si es un movimiento intradiario para aplicar o no los nuevos cargos.
        //TODO Agregar en el broker un flag para saber si aplica o no la exencion de cargo en movs intradiarios
        cartera.getBroker().getCargos().forEach(c -> {
            double totalCargo = (c.getTasaAplicable() - 1) * operado;
            movimiento.addCargo(c, BigDecimal.valueOf(totalCargo));
        });
    }

    protected void validate() {
        Objects.requireNonNull(movimiento);
        Objects.requireNonNull(movimiento.getCartera());
        Objects.requireNonNull(movimiento.getCartera().getId());
    }

    protected void loadContext() {
        Optional<Cartera> opCartera = carteraRepository.findById(movimiento.getCartera().getId());
        cartera = opCartera.orElseThrow(() -> entityNotFoundException(Cartera.class.getSimpleName(), movimiento.getCartera().getId()));
    }
}
