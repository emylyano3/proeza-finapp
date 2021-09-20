package proeza.finapp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import proeza.finapp.entities.Cuenta;
import proeza.finapp.entities.Deposito;
import proeza.finapp.entities.Extraccion;
import proeza.finapp.rest.dto.CompraDTO;
import proeza.finapp.rest.dto.VentaDTO;

import javax.transaction.Transactional;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration_test")
@Sql(scripts = {"/test-data.sql"})
public class CarteraIT {

    @Autowired
    protected MockMvc mockMvc;

    @Test
    public void cuandoBuscoCarteraConIdUno_entoncesElCodigoDelBrokerEsIOL() throws Exception {
        this.mockMvc
                .perform(get("/api/cartera/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.broker.codigo", is("IOL")))
                .andExpect(status().isOk());
    }

    @Test
    public void cuandoHagoUnDeposito_entoncesElSaldoDeLaCuentaSeIncrementa() throws Exception {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        Deposito deposito = new Deposito(cuenta, BigDecimal.valueOf(1000));
        this.mockMvc
                .perform(post("/api/cuenta/deposito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(deposito)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.saldo", is(4852.16)))
                .andExpect(status().isOk());
    }

    @Test
    public void cuandoHagoUnaExtraccion_entoncesElSaldoDeLaCuentaDisminuye() throws Exception {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        Extraccion extraccion = new Extraccion(cuenta, BigDecimal.valueOf(1000));
        this.mockMvc
                .perform(post("/api/cuenta/extraccion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(extraccion)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.saldo", is(2852.16)))
                .andExpect(status().isOk());
    }

    @Test
    public void cuandoHagoUnaCompra_entoncesSeActualizaLaCarteraElActivoYSeExtraeElMontoOperadoDeLaCuenta() throws Exception {
        CompraDTO compra = new CompraDTO();
        compra.setIdCartera(1L);
        compra.setTicker("YPFD");
        compra.setPrecio(BigDecimal.valueOf(600));
        compra.setCantidad(5);
        this.mockMvc
                .perform(post("/api/cartera/1/compra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(compra)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.cuenta", notNullValue()))
                .andExpect(jsonPath("$.cuenta.id", is(1)))
                .andExpect(jsonPath("$.cuenta.saldo", is(831.108)))
                .andExpect(jsonPath("$.activos", notNullValue()))
                .andExpect(jsonPath("$.activos", hasSize(1)))
                .andExpect(jsonPath("$.activos[0].ppc", is(607.0)))
                .andExpect(status().isOk());
    }


    @Test
    public void cuandoHagoUnaVenta_entoncesSeActualizaLaCarteraElActivoYSeDepositaElMontoOperadoEnLaCuenta() throws Exception {
        VentaDTO venta = new VentaDTO();
        venta.setIdCartera(1L);
        venta.setTicker("YPFD");
        venta.setPrecio(BigDecimal.valueOf(700));
        venta.setCantidad(5);
        this.mockMvc
                .perform(post("/api/cartera/1/venta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(venta)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.cuenta", notNullValue()))
                .andExpect(jsonPath("$.cuenta.id", is(1)))
                .andExpect(jsonPath("$.cuenta.saldo", is(7327.6)))
                .andExpect(jsonPath("$.activos", notNullValue()))
                .andExpect(jsonPath("$.activos", hasSize(1)))
                .andExpect(jsonPath("$.activos[0].ppc", is(610.5)))
                .andExpect(status().isOk());
    }

    protected ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }
}
