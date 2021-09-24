package proeza.finapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import proeza.finapp.entities.Cuenta;
import proeza.finapp.entities.Deposito;
import proeza.finapp.entities.Extraccion;
import proeza.finapp.repository.CuentaRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Transactional
public class CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    public Cuenta findById(Long id) {
        return cuentaRepository.findById(id)
                               .orElseThrow(() -> new ResourceNotFoundException(String.format("Cuenta con id %s no encontrada", id)));
    }

    public Cuenta deposito(Deposito deposito) {
        Objects.requireNonNull(deposito);
        Objects.requireNonNull(deposito.getCuenta());
        Objects.requireNonNull(deposito.getCuenta().getId());
        Objects.requireNonNull(deposito.getMonto());
        Cuenta cuenta = findById(deposito.getCuenta().getId());
        deposito.setFecha(deposito.getFecha() == null
                ? LocalDateTime.now()
                : deposito.getFecha());
        deposito.setCuenta(cuenta);
        cuenta.addDeposito(deposito);
        return cuenta;
    }

    public Cuenta extraccion(Extraccion extraccion) {
        Objects.requireNonNull(extraccion);
        Objects.requireNonNull(extraccion.getCuenta());
        Objects.requireNonNull(extraccion.getCuenta().getId());
        Objects.requireNonNull(extraccion.getMonto());
        Cuenta cuenta = findById(extraccion.getCuenta().getId());
        extraccion.setFecha(extraccion.getFecha() == null
                ? LocalDateTime.now()
                : extraccion.getFecha());
        extraccion.setCuenta(cuenta);
        cuenta.addExtraccion(extraccion);
        return cuenta;
    }
}
