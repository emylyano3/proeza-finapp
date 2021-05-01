package proeza.finapp.repository;

import org.springframework.data.repository.CrudRepository;
import proeza.finapp.entities.Instrumento;

public interface InstrumentoRepository extends CrudRepository<Instrumento, Long> {
}
