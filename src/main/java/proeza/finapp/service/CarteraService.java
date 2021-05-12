package proeza.finapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proeza.finapp.entities.Activo;
import proeza.finapp.entities.Cartera;
import proeza.finapp.entities.Compra;
import proeza.finapp.entities.Cuenta;
import proeza.finapp.entities.Deposito;
import proeza.finapp.entities.Extraccion;
import proeza.finapp.entities.Instrumento;
import proeza.finapp.entities.Venta;
import proeza.finapp.exception.EntityNotFoundException;
import proeza.finapp.repository.ActivoRepository;
import proeza.finapp.repository.CarteraRepository;
import proeza.finapp.repository.InstrumentoRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class CarteraService {

    @Autowired
    private CarteraRepository carteraRepository;

    @Autowired
    private ActivoRepository activoRepository;

    @Autowired
    private InstrumentoRepository instrumentoRepository;

    public Cartera venta(Venta venta) {
        Objects.requireNonNull(venta);
        Objects.requireNonNull(venta.getCartera());
        Objects.requireNonNull(venta.getCartera().getId());
        Objects.requireNonNull(venta.getInstrumento());
        Objects.requireNonNull(venta.getInstrumento().getId());
        Objects.requireNonNull(venta.getCantidad());
        Objects.requireNonNull(venta.getPrecio());
        Optional<Cartera> opCartera = carteraRepository.findById(venta.getCartera().getId());
        Cartera cartera = opCartera.orElseThrow(() -> entityNotFoundException(Cartera.class.getSimpleName(), venta.getCartera().getId()));
        Optional<Instrumento> opInstrumento = instrumentoRepository.findById(venta.getInstrumento().getId());
        Instrumento instrumento = opInstrumento.orElseThrow(() -> entityNotFoundException(Instrumento.class.getSimpleName(), venta.getInstrumento().getId()));
        Optional<Activo> opActivo = activoRepository.findByCarteraAndInstrumento(cartera, instrumento);
        Activo activo = opActivo.orElse(new Activo(cartera, instrumento));
        //TODO Verificar si es un movimiento intradiario para aplicar o no los nuevos cargos.
        //TODO Agregar en el broker un flag para saber si aplica o no la exencion de cargo en movs intradiarios
        cartera.getBroker().getCargos().forEach(c -> {
            double totalCargo = (c.getTasaAplicable() - 1) * venta.getOperado().doubleValue();
            venta.addCargo(c, totalCargo);
        });
        venta.setActivo(activo);
        venta.setCartera(cartera);
        venta.setInstrumento(instrumento);
        venta.setFecha(LocalDateTime.now());
        activo.addVenta(venta);
        cartera.update(activo);
        Cuenta cuenta = cartera.getCuenta();
        Deposito deposito = new Deposito();
        deposito.setCuenta(cuenta);
        deposito.setFecha(LocalDateTime.now());
        deposito.setMonto(venta.getMontoNeto());
        cuenta.addDeposito(deposito);
        return venta.getCartera();
    }

    public Cartera compra(Compra compra) {
        Objects.requireNonNull(compra);
        Objects.requireNonNull(compra.getCartera());
        Objects.requireNonNull(compra.getCartera().getId());
        Objects.requireNonNull(compra.getInstrumento());
        Objects.requireNonNull(compra.getInstrumento().getId());
        Objects.requireNonNull(compra.getCantidad());
        Objects.requireNonNull(compra.getPrecio());
        Optional<Cartera> opCartera = carteraRepository.findById(compra.getCartera().getId());
        Cartera cartera = opCartera.orElseThrow(() -> entityNotFoundException(Cartera.class.getSimpleName(), compra.getCartera().getId()));
        Optional<Instrumento> opInstrumento = instrumentoRepository.findById(compra.getInstrumento().getId());
        Instrumento instrumento = opInstrumento.orElseThrow(() -> entityNotFoundException(Instrumento.class.getSimpleName(), compra.getInstrumento().getId()));
        Optional<Activo> opActivo = activoRepository.findByCarteraAndInstrumento(cartera, instrumento);
        Activo activo = opActivo.orElse(new Activo(cartera, instrumento));
        cartera.getBroker().getCargos().forEach(c -> {
            double totalCargo = (c.getTasaAplicable() - 1) * compra.getOperado().doubleValue();
            compra.addCargo(c, totalCargo);
        });
        compra.setActivo(activo);
        compra.setCartera(cartera);
        compra.setInstrumento(instrumento);
        compra.setFecha(LocalDateTime.now());
        activo.addCompra(compra);
        cartera.update(activo);
        Cuenta cuenta = cartera.getCuenta();
        Extraccion extraccion = new Extraccion();
        extraccion.setCuenta(cuenta);
        extraccion.setFecha(LocalDateTime.now());
        extraccion.setMonto(compra.getTotalMovimiento());
        cuenta.addExtraccion(extraccion);
        return compra.getCartera();
    }

    protected EntityNotFoundException entityNotFoundException(String entityName, Long id) {
        return new EntityNotFoundException(String.format("No se encontro la entidad %s con id %s", entityName, id));
    }
}
