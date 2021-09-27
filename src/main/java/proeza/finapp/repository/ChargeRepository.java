package proeza.finapp.repository;

import org.springframework.data.repository.CrudRepository;
import proeza.finapp.domain.Charge;

public interface ChargeRepository extends CrudRepository<Charge, Long> {
}
