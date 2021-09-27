package proeza.finapp.repository;

import org.springframework.data.repository.CrudRepository;
import proeza.finapp.domain.Asset;
import proeza.finapp.domain.Portfolio;
import proeza.finapp.domain.Instrument;

import java.util.Optional;

public interface AssetRepository extends CrudRepository<Asset, Long> {
    Optional<Asset> findByPortfolioAndInstrumentAndHoldingGreaterThan(Portfolio portfolio, Instrument instrument, Integer holding);
}
