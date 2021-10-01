package proeza.finapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import proeza.finapp.domain.Broker;

public interface BrokerRepository extends CrudRepository<Broker, Long>, JpaRepository<Broker, Long> {
}
