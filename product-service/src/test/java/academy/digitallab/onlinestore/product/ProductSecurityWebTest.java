package academy.digitallab.onlinestore.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Ep. 11 — verifica el Resource Server: sin JWT devuelve 401; con un JWT válido, 200.
 * Usa H2 (perfil local) y un JWT simulado (spring-security-test), sin Authorization Server real.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ProductSecurityWebTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void productsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void productsAccessibleWithValidJwt() throws Exception {
        mockMvc.perform(get("/products").with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void actuatorHealthIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }
}
