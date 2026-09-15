package academy.digitallab.onlinestore.shopping.infrastructure.config;

import academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.client.CustomerHttpClient;
import academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.client.ProductHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

/**
 * Construye los HTTP Interfaces con RestClient. Las URLs se resuelven por configuración
 * (perfil local → localhost; en Docker → DNS de los contenedores vía variables de entorno).
 *
 * <p>Ep. 8 — se aplican timeouts de conexión y lectura; junto con retry/circuit breaker
 * (Resilience4j en los adaptadores) completan las 4 técnicas: circuit breaker, retry,
 * timeout y fallback.
 */
@Configuration
public class HttpClientConfig {

    @Value("${clients.timeout.connect-ms:2000}")
    private long connectTimeoutMs;

    @Value("${clients.timeout.read-ms:2000}")
    private long readTimeoutMs;

    private <T> T createClient(String baseUrl, Class<T> clientType) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .requestInterceptor(new BearerTokenRelayInterceptor())
                .build();
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(clientType);
    }

    @Bean
    ProductHttpClient productHttpClient(@Value("${clients.product.url}") String baseUrl) {
        return createClient(baseUrl, ProductHttpClient.class);
    }

    @Bean
    CustomerHttpClient customerHttpClient(@Value("${clients.customer.url}") String baseUrl) {
        return createClient(baseUrl, CustomerHttpClient.class);
    }
}
