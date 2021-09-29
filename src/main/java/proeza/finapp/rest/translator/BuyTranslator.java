package proeza.finapp.rest.translator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import proeza.finapp.domain.Asset;
import proeza.finapp.domain.Portfolio;
import proeza.finapp.domain.Buyout;
import proeza.finapp.domain.Instrument;
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

    public Buyout toDomain(BuyDTO buyDTO) {
        Portfolio portfolio = portfolioRepo.findById(buyDTO.getIdCartera()).orElseThrow();
        Instrument instrument = instrumentRepo.findByTicker(buyDTO.getTicker()).orElseThrow();
        Asset asset = assetRepo.findByPortfolioAndInstrumentAndHoldingGreaterThan(portfolio, instrument, 0)
                               .orElse(new Asset(portfolio, instrument));
        Buyout buyout = new Buyout();
        buyout.setQuantity(buyDTO.getCantidad());
        buyout.setPrice(buyDTO.getPrecio());
        buyout.setPortfolio(portfolio);
        buyout.setAsset(asset);
        buyout.setDate(buyDTO.getFecha() == null ? LocalDateTime.now() : buyDTO.getFecha());
        asset.addBuyout(buyout);
        return buyout;
    }
}
