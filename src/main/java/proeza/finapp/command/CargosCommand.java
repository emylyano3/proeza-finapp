package proeza.finapp.command;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import proeza.finapp.entities.Cartera;
import proeza.finapp.entities.MovimientoActivo;
import proeza.finapp.exception.EntityNotFoundException;
import proeza.finapp.repository.CarteraRepository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Builder
public class CargosCommand implements ICommand {
    private final MovimientoActivo movimiento;

    @Autowired
    private CarteraRepository carteraRepository;

    private Cartera cartera;

    @Override
    @Transactional
    public void execute() {
        validate();
        loadContext();
        businessLogic();
    }

    private void businessLogic() {
        double operado = movimiento.getPrecio().doubleValue() * movimiento.getCantidad();
        cartera.getBroker().getCargos().forEach(c -> {
            double totalCargo = (c.getTasaAplicable() - 1) * operado;
            movimiento.addCargo(c, BigDecimal.valueOf(totalCargo));
        });
    }

    private void validate() {
        Objects.requireNonNull(movimiento);
        Objects.requireNonNull(movimiento.getCartera());
        Objects.requireNonNull(movimiento.getCartera().getId());
    }

    private void loadContext() {
        Optional<Cartera> opCartera = carteraRepository.findById(movimiento.getCartera().getId());
        cartera = opCartera.orElseThrow(() -> entityNotFoundException(Cartera.class.getSimpleName(), movimiento.getCartera().getId()));
    }

    @NotNull
    private EntityNotFoundException entityNotFoundException(String entityName, Long id) {
        return new EntityNotFoundException(String.format("No se encontro la entidad %s con id %s", entityName, id));
    }
}
