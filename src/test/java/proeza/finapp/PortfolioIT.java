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
import proeza.finapp.domain.Account;
import proeza.finapp.domain.Deposit;
import proeza.finapp.domain.Withdrawal;
import proeza.finapp.rest.dto.BuyDTO;
import proeza.finapp.rest.dto.SellDTO;

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
public class PortfolioIT {

    @Autowired
    protected MockMvc mockMvc;

    @Test
    public void cuandoBuscoCarteraConIdUno_entoncesElCodigoDelBrokerEsIOL() throws Exception {
        this.mockMvc
                .perform(get("/api/cartera/1"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.broker.code", is("IOL")))
                .andExpect(status().isOk());
    }

    @Test
    public void cuandoHagoUnDeposito_entoncesElSaldoDeLaCuentaSeIncrementa() throws Exception {
        Account account = new Account();
        account.setNumber("ABC00001");
        Deposit deposit = new Deposit(account, BigDecimal.valueOf(1000));
        this.mockMvc
                .perform(post("/api/cuenta/ABC00001/deposito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(deposit)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.balance", is(4852.16)))
                .andExpect(jsonPath("$.number", is("ABC00001")))
                .andExpect(status().isOk());
    }

    @Test
    public void cuandoHagoUnaExtraccion_entoncesElSaldoDeLaCuentaDisminuye() throws Exception {
        Account account = new Account();
        account.setNumber("ABC00001");
        Withdrawal withdrawal = new Withdrawal(account, BigDecimal.valueOf(1000));
        this.mockMvc
                .perform(post("/api/cuenta/ABC00001/extraccion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(withdrawal)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.balance", is(2852.16)))
                .andExpect(jsonPath("$.number", is("ABC00001")))
                .andExpect(status().isOk());
    }

    @Test
    public void cuandoHagoUnaCompra_entoncesSeActualizaLaCarteraElActivoYSeExtraeElMontoOperadoDeLaCuenta() throws Exception {
        BuyDTO compra = new BuyDTO();
        compra.setIdCartera(1L);
        compra.setTicker("YPFD");
        compra.setPrecio(BigDecimal.valueOf(600));
        compra.setCantidad(5);
        this.mockMvc
                .perform(post("/api/cartera/1/compra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(compra)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.account", notNullValue()))
                .andExpect(jsonPath("$.account.id", is(1)))
                .andExpect(jsonPath("$.account.balance", is(831.108)))
                .andExpect(jsonPath("$.assets", notNullValue()))
                .andExpect(jsonPath("$.assets", hasSize(1)))
                .andExpect(jsonPath("$.assets[0].avgPrice", is(607.0)))
                .andExpect(status().isOk());
    }

    @Test
    public void cuandoHagoUnaCompraConFondosInsufucientes_entoncesLanzaUnaExcepcion() throws Exception {
        BuyDTO compra = new BuyDTO();
        compra.setIdCartera(1L);
        compra.setTicker("YPFD");
        compra.setPrecio(BigDecimal.valueOf(600));
        compra.setCantidad(500);
        this.mockMvc
                .perform(post("/api/cartera/1/compra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(compra)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void cuandoHagoUnaCompraSinTicker_entoncesLanzaUnaException() throws Exception {
        BuyDTO compra = new BuyDTO();
        compra.setIdCartera(1L);
        compra.setPrecio(BigDecimal.valueOf(600));
        compra.setCantidad(5);
        this.mockMvc
                .perform(post("/api/cartera/1/compra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(compra)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void cuandoHagoUnaVenta_entoncesSeActualizaLaCarteraElActivoYSeDepositaElMontoOperadoEnLaCuenta() throws Exception {
        SellDTO venta = new SellDTO();
        venta.setIdCartera(1L);
        venta.setTicker("YPFD");
        venta.setPrecio(BigDecimal.valueOf(700));
        venta.setCantidad(5);
        this.mockMvc
                .perform(post("/api/cartera/1/venta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(venta)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.account", notNullValue()))
                .andExpect(jsonPath("$.account.id", is(1)))
                .andExpect(jsonPath("$.account.balance", is(7327.6)))
                .andExpect(jsonPath("$.assets", notNullValue()))
                .andExpect(jsonPath("$.assets", hasSize(1)))
                .andExpect(jsonPath("$.assets[0].avgPrice", is(610.5)))
                .andExpect(status().isOk());
    }

    @Test
    public void cuandoHagoUnaVentaDeMasActivosDeLosQueTengoEnCartera_entoncesLanzaExcepcion() throws Exception {
        SellDTO venta = new SellDTO();
        venta.setIdCartera(1L);
        venta.setTicker("YPFD");
        venta.setPrecio(BigDecimal.valueOf(700));
        venta.setCantidad(50);
        this.mockMvc
                .perform(post("/api/cartera/1/venta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(venta)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void cuandoHagoUnaVentaSinTicker_entoncesLanzaUnaException() throws Exception {
        SellDTO sale = new SellDTO();
        sale.setIdCartera(1L);
        sale.setPrecio(BigDecimal.valueOf(700));
        sale.setCantidad(5);
        this.mockMvc
                .perform(post("/api/cartera/1/venta")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(getObjectMapper().writeValueAsBytes(sale)))
                .andExpect(status().is4xxClientError());
    }

    protected ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }
}
