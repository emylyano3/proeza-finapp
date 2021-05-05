package proeza.finapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proeza.finapp.command.CommandsFactory;
import proeza.finapp.entities.Cuenta;
import proeza.finapp.entities.Deposito;
import proeza.finapp.entities.Extraccion;
import proeza.finapp.exception.EntityNotFoundException;
import proeza.finapp.repository.CuentaRepository;

import java.util.Objects;

@Service
public class CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private CommandsFactory commandsFactory;

    public Cuenta findById(Long id) {
        return cuentaRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Cuenta con id %s no encontrada", id)));
    }

    public Cuenta deposito(Deposito deposito) {
        Objects.requireNonNull(deposito);
        commandsFactory.movimientoCuentaCommand(deposito).execute();
        return deposito.getCuenta();
    }

    public Cuenta extraccion(Extraccion extraccion) {
        Objects.requireNonNull(extraccion);
        commandsFactory.movimientoCuentaCommand(extraccion).execute();
        return extraccion.getCuenta();
    }
}
