package proeza.finapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proeza.finapp.command.CommandsFactory;
import proeza.finapp.entities.Cartera;
import proeza.finapp.entities.Compra;
import proeza.finapp.entities.Venta;

import java.util.Objects;

@Service
public class CarteraService {

    @Autowired
    private CommandsFactory commandsFactory;

    public Cartera venta(Venta venta) {
        Objects.requireNonNull(venta);
        commandsFactory.ventaCommand(venta).execute();
        return venta.getCartera();
    }

    public Cartera compra(Compra compra) {
        Objects.requireNonNull(compra);
        commandsFactory.compraCommand(compra).execute();
        return compra.getCartera();
    }
}
