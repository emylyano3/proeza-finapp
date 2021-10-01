package proeza.finapp.repository;

import org.springframework.data.repository.CrudRepository;
import proeza.finapp.domain.AssetBreadcrumb;
import proeza.finapp.domain.Instrument;
import proeza.finapp.domain.Portfolio;

import java.util.List;

public interface AssetBreadcrumbRepository extends CrudRepository<AssetBreadcrumb, Long> {
    List<AssetBreadcrumb> findByAssetPortfolioAndAssetInstrumentAndRemainsGreaterThan(Portfolio portfolio, Instrument instrument, Integer remains);
}
