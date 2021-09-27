package proeza.finapp.repository;

import org.springframework.data.repository.CrudRepository;
import proeza.finapp.domain.Instrument;

import java.util.Optional;

public interface InstrumentRepository extends CrudRepository<Instrument, Long> {
    Optional<Instrument> findByTicker(String ticker);
}
