package proeza.finapp.rest.translator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import proeza.finapp.domain.Asset;
import proeza.finapp.domain.Instrument;
import proeza.finapp.domain.Sale;
import proeza.finapp.repository.AssetRepository;
import proeza.finapp.repository.PortfolioRepository;
import proeza.finapp.repository.InstrumentRepository;
import proeza.finapp.rest.dto.SellDTO;

import java.time.LocalDateTime;

@Component
public class SaleTranslator {

    @Autowired
    private PortfolioRepository portfolioRepo;
    @Autowired
    private InstrumentRepository instrumentRepo;
    @Autowired
    private AssetRepository assetRepo;

    public Sale toDomain(SellDTO sellDTO) {
        Sale sale = new Sale();
        sale.setQuantity(sellDTO.getCantidad());
        sale.setPrice(sellDTO.getPrecio());
        sale.setDate(sellDTO.getFecha());
        sale.setPortfolio(portfolioRepo.findById(sellDTO.getIdCartera()).orElseThrow());
        Instrument instrument = instrumentRepo.findByTicker(sellDTO.getTicker()).orElseThrow();
        Asset asset = assetRepo.findByPortfolioAndInstrumentAndHoldingGreaterThan(sale.getPortfolio(), instrument, 0)
                               .orElse(new Asset(sale.getPortfolio(), instrument));
        sale.setAsset(asset);
        sale.setDate(sellDTO.getFecha() == null ? LocalDateTime.now() : sellDTO.getFecha());
        asset.addSale(sale);
        return sale;
    }
}
