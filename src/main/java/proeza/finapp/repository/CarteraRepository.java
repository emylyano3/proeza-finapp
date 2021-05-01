package proeza.finapp.repository;

import org.springframework.data.repository.CrudRepository;
import proeza.finapp.entities.Cartera;

public interface CarteraRepository extends CrudRepository<Cartera, Long> {
    Cartera findById(long id);
}
