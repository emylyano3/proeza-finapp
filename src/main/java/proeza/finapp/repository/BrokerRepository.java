package proeza.finapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import proeza.finapp.entities.Broker;

public interface BrokerRepository extends CrudRepository<Broker, Long>, JpaRepository<Broker, Long> {
}
