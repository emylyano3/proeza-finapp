package proeza.finapp.repository;

import org.springframework.data.repository.CrudRepository;
import proeza.finapp.entities.Cargo;

public interface CargoRepository extends CrudRepository<Cargo, Long> {
}
