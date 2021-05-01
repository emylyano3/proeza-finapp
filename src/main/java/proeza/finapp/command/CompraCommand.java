package proeza.finapp.command;

import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import proeza.finapp.entities.Activo;
import proeza.finapp.entities.Cartera;
import proeza.finapp.entities.Compra;
import proeza.finapp.entities.Instrumento;
import proeza.finapp.exception.EntityNotFoundException;
import proeza.finapp.repository.ActivoRepository;
import proeza.finapp.repository.CarteraRepository;
import proeza.finapp.repository.InstrumentoRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Builder
public class CompraCommand implements ICommand {

    @Autowired
    private ActivoRepository activoRepository;

    @Autowired
    private InstrumentoRepository instrumentoRepository;

    @Autowired
    private CarteraRepository carteraRepository;

    @Autowired
    private CommandsFactory commansFactory;

    private final Compra compra;
    private Cartera cartera;
    private Instrumento instrumento;
    private Activo activo;

    @Override
    @Transactional
    public void execute() {
        validate();
        loadContext();
        businessLogic();
    }

    private void businessLogic() {
        commansFactory.cargosCommand(compra).execute();
        compra.setActivo(activo);
        compra.setCartera(cartera);
        compra.setInstrumento(instrumento);
        compra.setFecha(LocalDateTime.now());
        activo.addCompra(compra);
        cartera.addActivo(activo);
    }

    private void validate() {
        Objects.requireNonNull(compra);
        Objects.requireNonNull(compra.getCartera());
        Objects.requireNonNull(compra.getCartera().getId());
        Objects.requireNonNull(compra.getInstrumento());
        Objects.requireNonNull(compra.getInstrumento().getId());
        Objects.requireNonNull(compra.getCantidad());
        Objects.requireNonNull(compra.getPrecio());
    }

    private void loadContext() {
        Optional<Cartera> opCartera = carteraRepository.findById(compra.getCartera().getId());
        cartera = opCartera.orElseThrow(() -> entityNotFoundException(Cartera.class.getSimpleName(), compra.getCartera().getId()));
        Optional<Instrumento> opInstrumento = instrumentoRepository.findById(compra.getInstrumento().getId());
        instrumento = opInstrumento.orElseThrow(() -> entityNotFoundException(Instrumento.class.getSimpleName(), compra.getInstrumento().getId()));
        Optional<Activo> opActivo = activoRepository.findByCarteraAndInstrumento(cartera, instrumento);
        activo = opActivo.orElse(new Activo(cartera, instrumento));
    }

    private EntityNotFoundException entityNotFoundException(String entityName, Long id) {
        return new EntityNotFoundException(String.format("No se encontro la entidad %s con id %s", entityName, id));
    }
}
