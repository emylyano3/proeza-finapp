package proeza.finapp.rest.translator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import proeza.finapp.entities.Asset;
import proeza.finapp.entities.Portfolio;
import proeza.finapp.entities.Buy;
import proeza.finapp.entities.Instrument;
import proeza.finapp.repository.AssetRepository;
import proeza.finapp.repository.PortfolioRepository;
import proeza.finapp.repository.InstrumentRepository;
import proeza.finapp.rest.dto.BuyDTO;

import java.time.LocalDateTime;

@Component
public class BuyTranslator {

    @Autowired
    private PortfolioRepository portfolioRepo;
    @Autowired
    private InstrumentRepository instrumentRepo;
    @Autowired
    private AssetRepository assetRepo;

    public Buy toDomain(BuyDTO buyDTO) {
        Portfolio portfolio = portfolioRepo.findById(buyDTO.getIdCartera()).orElseThrow();
        Instrument instrument = instrumentRepo.findByTicker(buyDTO.getTicker()).orElseThrow();
        Asset asset = assetRepo.findByPortfolioAndInstrumentAndHoldingGreaterThan(portfolio, instrument, 0)
                               .orElse(new Asset(portfolio, instrument));
        Buy buy = new Buy();
        buy.setQuantity(buyDTO.getCantidad());
        buy.setPrice(buyDTO.getPrecio());
        buy.setPortfolio(portfolio);
        buy.setAsset(asset);
        buy.setDate(buyDTO.getFecha() == null ? LocalDateTime.now() : buyDTO.getFecha());
        return buy;
    }
}
