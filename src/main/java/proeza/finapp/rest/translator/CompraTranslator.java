package proeza.finapp.rest.translator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import proeza.finapp.entities.Activo;
import proeza.finapp.entities.Cartera;
import proeza.finapp.entities.Compra;
import proeza.finapp.entities.Instrumento;
import proeza.finapp.repository.ActivoRepository;
import proeza.finapp.repository.CarteraRepository;
import proeza.finapp.repository.InstrumentoRepository;
import proeza.finapp.rest.dto.CompraDTO;

import java.time.LocalDateTime;

@Component
public class CompraTranslator {

    @Autowired
    private CarteraRepository carteraRepository;
    @Autowired
    private InstrumentoRepository instrumentoRepository;
    @Autowired
    private ActivoRepository activoRepository;

    public Compra toDomain(CompraDTO compraDTO) {
        Cartera cartera = carteraRepository.findById(compraDTO.getIdCartera()).orElseThrow();
        Instrumento instrumento = instrumentoRepository.findByTicker(compraDTO.getTicker()).orElseThrow();
        Activo activo = activoRepository.findByCarteraAndInstrumento(cartera, instrumento).orElse(new Activo(cartera, instrumento));
        Compra compra = new Compra();
        compra.setCantidad(compraDTO.getCantidad());
        compra.setPrecio(compraDTO.getPrecio());
        compra.setCartera(cartera);
        compra.setInstrumento(instrumento);
        compra.setActivo(activo);
        compra.setFecha(compraDTO.getFecha() == null ? LocalDateTime.now() : compraDTO.getFecha());
        return compra;
    }
}
