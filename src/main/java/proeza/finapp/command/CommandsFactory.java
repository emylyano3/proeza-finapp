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
    public CargosCommand cargosCommand(MovimientoActivo movimiento) {
        return CargosCommand.builder().movimiento(movimiento).build();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public MovimientoActivoCommand ventaCommand(Venta venta) {
        return MovimientoActivoCommand.builder().movimiento(venta).build();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public MovimientoActivoCommand compraCommand(Compra compra) {
        return MovimientoActivoCommand.builder().movimiento(compra).build();
    }
}
