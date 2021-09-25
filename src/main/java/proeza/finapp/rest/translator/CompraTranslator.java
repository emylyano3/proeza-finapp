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
public class CompraTranslator {

    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private AssetRepository assetRepository;

    public Buy toDomain(BuyDTO buyDTO) {
        Portfolio portfolio = portfolioRepository.findById(buyDTO.getIdCartera()).orElseThrow();
        Instrument instrument = instrumentRepository.findByTicker(buyDTO.getTicker()).orElseThrow();
        Asset asset = assetRepository.findByPortfolioAndInstrument(portfolio, instrument).orElse(new Asset(portfolio, instrument));
        Buy buy = new Buy();
        buy.setQuantity(buyDTO.getCantidad());
        buy.setPrice(buyDTO.getPrecio());
        buy.setPortfolio(portfolio);
        buy.setAsset(asset);
        buy.setDate(buyDTO.getFecha() == null ? LocalDateTime.now() : buyDTO.getFecha());
        return buy;
    }
}
