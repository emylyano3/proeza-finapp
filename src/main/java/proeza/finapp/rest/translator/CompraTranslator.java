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
import proeza.finapp.rest.dto.CompraDTO;

import java.time.LocalDateTime;

@Component
public class CompraTranslator {

    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private AssetRepository assetRepository;

    public Buy toDomain(CompraDTO compraDTO) {
        Portfolio portfolio = portfolioRepository.findById(compraDTO.getIdCartera()).orElseThrow();
        Instrument instrument = instrumentRepository.findByTicker(compraDTO.getTicker()).orElseThrow();
        Asset asset = assetRepository.findByPortfolioAndInstrument(portfolio, instrument).orElse(new Asset(portfolio, instrument));
        Buy buy = new Buy();
        buy.setQuantity(compraDTO.getCantidad());
        buy.setPrice(compraDTO.getPrecio());
        buy.setPortfolio(portfolio);
        buy.setInstrument(instrument);
        buy.setAsset(asset);
        buy.setDate(compraDTO.getFecha() == null ? LocalDateTime.now() : compraDTO.getFecha());
        return buy;
    }
}
