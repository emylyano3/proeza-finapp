package proeza.finapp.repository;

import org.springframework.data.repository.CrudRepository;
import proeza.finapp.entities.Asset;
import proeza.finapp.entities.Portfolio;
import proeza.finapp.entities.Instrument;

import java.util.Optional;

public interface AssetRepository extends CrudRepository<Asset, Long> {
    Optional<Asset> findByPortfolioAndInstrumentAndHoldingGreaterThan(Portfolio portfolio, Instrument instrument, Integer holding);
}
