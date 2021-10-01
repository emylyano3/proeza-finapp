package proeza.finapp.repository;

import org.springframework.data.repository.CrudRepository;
import proeza.finapp.domain.Portfolio;

public interface PortfolioRepository extends CrudRepository<Portfolio, Long> {
    Portfolio findById(long id);
}
