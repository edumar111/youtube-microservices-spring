package academy.digitallab.onlinestore.shopping;

import academy.digitallab.onlinestore.shopping.domain.model.Product;
import academy.digitallab.onlinestore.shopping.domain.port.out.ProductClientPort;
import academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.client.ProductHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Ep. 8 — verifica que, cuando product-service falla, el retry reintenta y el circuit
 * breaker deriva al fallback, que degrada la respuesta en lugar de propagar el error.
 * Usa H2 (perfil local) para no depender de Docker.
 */
@SpringBootTest
@ActiveProfiles("local")
class ResilienceFallbackTest {

    @Autowired
    ProductClientPort productClient;

    @MockitoBean
    ProductHttpClient productHttpClient;

    @Test
    void whenProductServiceFailsThenFallbackDegradesResponse() {
        when(productHttpClient.getProduct(any())).thenThrow(new RuntimeException("connection refused"));

        Product result = productClient.getProduct(99L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(99L);
        assertThat(result.name()).isEqualTo("unavailable");
        // El retry (max-attempts=3) reintenta antes de caer al fallback.
        verify(productHttpClient, atLeast(2)).getProduct(99L);
    }
}
