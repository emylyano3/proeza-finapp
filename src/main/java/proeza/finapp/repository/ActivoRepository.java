package proeza.finapp.repository;

import org.springframework.data.repository.CrudRepository;
import proeza.finapp.entities.Activo;
import proeza.finapp.entities.Cartera;
import proeza.finapp.entities.Instrumento;

import java.util.Optional;

public interface ActivoRepository extends CrudRepository<Activo, Long> {
    Optional<Activo> findByCarteraAndInstrumento(Cartera cartera, Instrumento instrumento);
}
