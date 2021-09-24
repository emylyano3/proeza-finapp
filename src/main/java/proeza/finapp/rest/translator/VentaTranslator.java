package proeza.finapp.rest.translator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import proeza.finapp.entities.Activo;
import proeza.finapp.entities.Venta;
import proeza.finapp.repository.ActivoRepository;
import proeza.finapp.repository.CarteraRepository;
import proeza.finapp.repository.InstrumentoRepository;
import proeza.finapp.rest.dto.VentaDTO;

import java.time.LocalDateTime;

@Component
public class VentaTranslator {

    @Autowired
    private CarteraRepository carteraRepository;
    @Autowired
    private InstrumentoRepository instrumentoRepository;
    @Autowired
    private ActivoRepository activoRepository;

    public Venta toDomain(VentaDTO ventaDTO) {
        Venta venta = new Venta();
        venta.setCantidad(ventaDTO.getCantidad());
        venta.setPrecio(ventaDTO.getPrecio());
        venta.setFecha(ventaDTO.getFecha());
        venta.setCartera(carteraRepository.findById(ventaDTO.getIdCartera()).orElseThrow());
        venta.setInstrumento(instrumentoRepository.findByTicker(ventaDTO.getTicker()).orElseThrow());
        Activo activo = activoRepository.findByCarteraAndInstrumento(venta.getCartera(), venta.getInstrumento())
                                        .orElse(new Activo(venta.getCartera(), venta.getInstrumento()));
        venta.setActivo(activo);
        venta.setFecha(ventaDTO.getFecha() == null ? LocalDateTime.now() : ventaDTO.getFecha());
        return venta;
    }
}
