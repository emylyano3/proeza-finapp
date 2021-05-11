package proeza.finapp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import proeza.finapp.entities.Cuenta;
import proeza.finapp.entities.Deposito;

import javax.transaction.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-h2_integration_test.properties")
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
                .andExpect(jsonPath("$.broker.codigo", is("IOL")));
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
                .andExpect(jsonPath("$.saldo", is(4852.16)));
    }

    protected ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }
}
