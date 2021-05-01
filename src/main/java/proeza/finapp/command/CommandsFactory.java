package proeza.finapp.command;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import proeza.finapp.entities.Compra;
import proeza.finapp.entities.MovimientoActivo;
import proeza.finapp.entities.Venta;

@Configuration
public class CommandsFactory {

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public CargosCommand  cargosCommand(MovimientoActivo movimiento) {
        return CargosCommand.builder().movimiento(movimiento).build();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public VentaCommand ventaCommand(Venta venta) {
        return VentaCommand.builder().venta(venta).build();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public CompraCommand compraCommand(Compra compra) {
        return CompraCommand.builder().compra(compra).build();
    }
}
