package proeza.finapp.repository;

import org.springframework.data.repository.CrudRepository;
import proeza.finapp.entities.Instrumento;

import java.util.Optional;

public interface InstrumentoRepository extends CrudRepository<Instrumento, Long> {
    Optional<Instrumento> findByTicker(String ticker);
}
