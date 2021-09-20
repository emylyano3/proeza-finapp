package proeza.finapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proeza.finapp.entities.*;
import proeza.finapp.repository.CarteraRepository;
import proeza.finapp.rest.dto.CompraDTO;
import proeza.finapp.rest.dto.VentaDTO;
import proeza.finapp.rest.translator.CompraTranslator;
import proeza.finapp.rest.translator.VentaTranslator;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class CarteraService {

    @Autowired
    private VentaTranslator translator;

    @Autowired
    private CompraTranslator compraTranslator;

    @Autowired
    private CarteraRepository carteraRepository;

    public Cartera crear(Cartera cartera) {
        Objects.requireNonNull(cartera);
        boolean exists = Optional.ofNullable(cartera.getId())
                .map(carteraRepository::findById)
                .isPresent();
        return exists ? cartera : carteraRepository.save(cartera);
    }

    public Cartera venta(VentaDTO ventaDTO) {
        //TODO Validar con annotations
        Objects.requireNonNull(ventaDTO);
        Objects.requireNonNull(ventaDTO.getIdCartera());
        Objects.requireNonNull(ventaDTO.getTicker());
        Objects.requireNonNull(ventaDTO.getCantidad());
        Objects.requireNonNull(ventaDTO.getPrecio());
        //TODO Verificar si es un movimiento intradiario para aplicar o no los nuevos cargos.
        //TODO Agregar en el broker un flag para saber si aplica o no la exencion de cargo en movs intradiarios
        Venta venta = translator.toDomain(ventaDTO);
        venta.getCartera().getBroker().getCargos().forEach(c -> {
            double totalCargo = (c.getTasaAplicable() - 1) * venta.getOperado().doubleValue();
            venta.addCargo(c, totalCargo);
        });
        venta.getCartera().update(venta.getActivo());
        Cuenta cuenta = venta.getCartera().getCuenta();
        Deposito deposito = new Deposito();
        deposito.setCuenta(cuenta);
        deposito.setFecha(LocalDateTime.now());
        deposito.setMonto(venta.getMontoNeto());
        cuenta.addDeposito(deposito);
        return venta.getCartera();
    }

    public Cartera compra(CompraDTO compraDTO) {
        Objects.requireNonNull(compraDTO);
        Objects.requireNonNull(compraDTO.getIdCartera());
        Objects.requireNonNull(compraDTO.getTicker());
        Objects.requireNonNull(compraDTO.getCantidad());
        Objects.requireNonNull(compraDTO.getPrecio());
        Compra compra = compraTranslator.toDomain(compraDTO);
        compra.getCartera().getBroker().getCargos().forEach(c -> {
            double totalCargo = (c.getTasaAplicable() - 1) * compra.getOperado().doubleValue();
            compra.addCargo(c, totalCargo);
        });
        compra.getCartera().update(compra.getActivo());
        Cuenta cuenta = compra.getCartera().getCuenta();
        Extraccion extraccion = new Extraccion();
        extraccion.setCuenta(cuenta);
        extraccion.setFecha(LocalDateTime.now());
        extraccion.setMonto(compra.getTotalMovimiento());
        cuenta.addExtraccion(extraccion);
        return compra.getCartera();
    }
}
