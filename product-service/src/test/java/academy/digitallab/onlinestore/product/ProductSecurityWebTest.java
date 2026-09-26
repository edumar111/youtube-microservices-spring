package academy.digitallab.onlinestore.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Ep. 11 — política de seguridad de product-service (como una tienda online):
 * el catálogo (GET) es público; las escrituras y el ajuste de stock exigen JWT.
 * Usa H2 (perfil local) y JWT simulado (spring-security-test).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ProductSecurityWebTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void catalogIsPublic() throws Exception {
        mockMvc.perform(get("/products")).andExpect(status().isOk());
        mockMvc.perform(get("/products/1")).andExpect(status().isOk());
    }

    @Test
    void catalogAlsoWorksWithJwt() throws Exception {
        mockMvc.perform(get("/products").with(jwt())).andExpect(status().isOk());
    }

    @Test
    void writeRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/products")).andExpect(status().isUnauthorized());
    }

    @Test
    void stockUpdateRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/products/1/stock").param("quantity", "1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void actuatorHealthIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }
}
