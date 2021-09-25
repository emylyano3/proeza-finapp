package proeza.finapp.rest.translator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import proeza.finapp.entities.Asset;
import proeza.finapp.entities.Instrument;
import proeza.finapp.entities.Sale;
import proeza.finapp.repository.AssetRepository;
import proeza.finapp.repository.PortfolioRepository;
import proeza.finapp.repository.InstrumentRepository;
import proeza.finapp.rest.dto.SaleDTO;

import java.time.LocalDateTime;

@Component
public class VentaTranslator {

    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private AssetRepository assetRepository;

    public Sale toDomain(SaleDTO saleDTO) {
        Sale sale = new Sale();
        sale.setQuantity(saleDTO.getCantidad());
        sale.setPrice(saleDTO.getPrecio());
        sale.setDate(saleDTO.getFecha());
        sale.setPortfolio(portfolioRepository.findById(saleDTO.getIdCartera()).orElseThrow());
        Instrument instrument = instrumentRepository.findByTicker(saleDTO.getTicker()).orElseThrow();
        Asset asset = assetRepository.findByPortfolioAndInstrument(sale.getPortfolio(), instrument)
                                     .orElse(new Asset(sale.getPortfolio(), instrument));
        sale.setAsset(asset);
        sale.setDate(saleDTO.getFecha() == null ? LocalDateTime.now() : saleDTO.getFecha());
        return sale;
    }
}
