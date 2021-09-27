package proeza.finapp.repository;

import org.springframework.data.repository.CrudRepository;
import proeza.finapp.entities.AssetBreadcrumb;
import proeza.finapp.entities.Instrument;
import proeza.finapp.entities.Portfolio;

import java.util.List;

public interface AssetBreadcrumbRepository extends CrudRepository<AssetBreadcrumb, Long> {
    List<AssetBreadcrumb> findByAssetPortfolioAndAssetInstrumentAndRemainsGreaterThan(Portfolio portfolio, Instrument instrument, Integer remains);
}
