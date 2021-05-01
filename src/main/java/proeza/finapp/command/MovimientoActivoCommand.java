package proeza.finapp.command;

import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import proeza.finapp.entities.Activo;
import proeza.finapp.entities.Cartera;
import proeza.finapp.entities.Instrumento;
import proeza.finapp.entities.MovimientoActivo;
import proeza.finapp.exception.EntityNotFoundException;
import proeza.finapp.repository.ActivoRepository;
import proeza.finapp.repository.CarteraRepository;
import proeza.finapp.repository.InstrumentoRepository;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Builder
public class MovimientoActivoCommand extends CarteraAbstractCommand {

    @Autowired
    private ActivoRepository activoRepository;

    @Autowired
    private InstrumentoRepository instrumentoRepository;

    @Autowired
    private CarteraRepository carteraRepository;

    @Autowired
    private CommandsFactory commandsFactory;

    protected final MovimientoActivo movimiento;
    protected Cartera cartera;
    protected Instrumento instrumento;
    protected Activo activo;

    @Override
    protected void businessLogic() {
        commandsFactory.cargosCommand(movimiento).execute();
        movimiento.setActivo(activo);
        movimiento.setCartera(cartera);
        movimiento.setInstrumento(instrumento);
        movimiento.setFecha(LocalDateTime.now());
        activo.addMovimiento(movimiento);
        cartera.update(activo);
    }

    protected void validate() {
        Objects.requireNonNull(movimiento);
        Objects.requireNonNull(movimiento.getCartera());
        Objects.requireNonNull(movimiento.getCartera().getId());
        Objects.requireNonNull(movimiento.getInstrumento());
        Objects.requireNonNull(movimiento.getInstrumento().getId());
        Objects.requireNonNull(movimiento.getCantidad());
        Objects.requireNonNull(movimiento.getPrecio());
    }

    protected void loadContext() {
        Optional<Cartera> opCartera = carteraRepository.findById(movimiento.getCartera().getId());
        cartera = opCartera.orElseThrow(() -> entityNotFoundException(Cartera.class.getSimpleName(), movimiento.getCartera().getId()));
        Optional<Instrumento> opInstrumento = instrumentoRepository.findById(movimiento.getInstrumento().getId());
        instrumento = opInstrumento.orElseThrow(() -> entityNotFoundException(Instrumento.class.getSimpleName(), movimiento.getInstrumento().getId()));
        Optional<Activo> opActivo = activoRepository.findByCarteraAndInstrumento(cartera, instrumento);
        activo = opActivo.orElse(new Activo(cartera, instrumento));
    }

    private EntityNotFoundException entityNotFoundException(String entityName, Long id) {
        return new EntityNotFoundException(String.format("No se encontro la entidad %s con id %s", entityName, id));
    }
}
