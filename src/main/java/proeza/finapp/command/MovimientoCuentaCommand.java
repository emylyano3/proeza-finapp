package proeza.finapp.command;

import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import proeza.finapp.entities.Cuenta;
import proeza.finapp.entities.MovimientoCuenta;
import proeza.finapp.repository.CuentaRepository;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Builder
public class MovimientoCuentaCommand extends CarteraAbstractCommand {
    private final MovimientoCuenta movimiento;

    @Autowired
    private CuentaRepository cuentaRepository;

    private Cuenta cuenta;

    @Override
    protected void businessLogic() {
        movimiento.setCuenta(cuenta);
        movimiento.setFecha(movimiento.getFecha() == null
                ? LocalDateTime.now()
                : movimiento.getFecha());
        cuenta.addMovimiento(movimiento);
    }

    @Override
    protected void validate() {
        Objects.requireNonNull(movimiento);
        Objects.requireNonNull(movimiento.getMonto());
        Objects.requireNonNull(movimiento.getCuenta());
        Objects.requireNonNull(movimiento.getCuenta().getId());
    }

    @Override
    protected void loadContext() {
        Optional<Cuenta> opCuenta = cuentaRepository.findById(movimiento.getCuenta().getId());
        cuenta = opCuenta.orElseThrow(() -> entityNotFoundException(Cuenta.class.getSimpleName(), movimiento.getCuenta().getId()));
    }
}
