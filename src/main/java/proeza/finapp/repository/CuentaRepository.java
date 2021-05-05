package proeza.finapp.repository;

import org.springframework.data.repository.CrudRepository;
import proeza.finapp.entities.Cuenta;

public interface CuentaRepository extends CrudRepository<Cuenta, Long> {
}
