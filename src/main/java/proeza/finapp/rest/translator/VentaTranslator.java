package proeza.finapp.rest.translator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import proeza.finapp.entities.Asset;
import proeza.finapp.entities.Sale;
import proeza.finapp.repository.AssetRepository;
import proeza.finapp.repository.PortfolioRepository;
import proeza.finapp.repository.InstrumentRepository;
import proeza.finapp.rest.dto.VentaDTO;

import java.time.LocalDateTime;

@Component
public class VentaTranslator {

    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private AssetRepository assetRepository;

    public Sale toDomain(VentaDTO ventaDTO) {
        Sale sale = new Sale();
        sale.setQuantity(ventaDTO.getCantidad());
        sale.setPrice(ventaDTO.getPrecio());
        sale.setDate(ventaDTO.getFecha());
        sale.setPortfolio(portfolioRepository.findById(ventaDTO.getIdCartera()).orElseThrow());
        sale.setInstrument(instrumentRepository.findByTicker(ventaDTO.getTicker()).orElseThrow());
        Asset asset = assetRepository.findByPortfolioAndInstrument(sale.getPortfolio(), sale.getInstrument())
                                     .orElse(new Asset(sale.getPortfolio(), sale.getInstrument()));
        sale.setAsset(asset);
        sale.setDate(ventaDTO.getFecha() == null ? LocalDateTime.now() : ventaDTO.getFecha());
        return sale;
    }
}
