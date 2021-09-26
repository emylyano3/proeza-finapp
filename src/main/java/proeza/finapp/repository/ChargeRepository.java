package proeza.finapp.repository;

import org.springframework.data.repository.CrudRepository;
import proeza.finapp.entities.Charge;

public interface ChargeRepository extends CrudRepository<Charge, Long> {
}
