package proeza.finapp.command;

import lombok.Builder;
import proeza.finapp.entities.Broker;
import proeza.finapp.entities.Cargo;
import proeza.finapp.entities.Cartera;
import proeza.finapp.entities.Venta;

@Builder
public class VentaCommand implements ICommand {
    private final Venta venta;

    public VentaCommand(Venta venta) {
        this.venta = venta;
    }

    @Override
    public void execute() {
        Cartera cartera = venta.getActivo().getCartera();
        Broker broker = cartera.getBroker();
        double operado = venta.getPrecio().doubleValue() * venta.getCantidad();
        double tasa = broker.getCargos().stream()
                .map(Cargo::getTasaAplicable)
                .reduce(1D, (subtotal, tasaParcial) -> subtotal * tasaParcial);
        double cargo = operado * (tasa - 1);
//        Movim ientoActivo mov = new MovimientoActivo();
//        mov.setMontoCargos(BigDecimal.valueOf(cargo));
//        mov.setActivo(activo);
//        mov.setPrecio(precio);
//        mov.setCantidad(cantidad);
//        mov.setTipo(TipoMovimiento.VENTA);
//        mov.setFecha(LocalDateTime.now());
//        activo.addVenta();
    }
}
