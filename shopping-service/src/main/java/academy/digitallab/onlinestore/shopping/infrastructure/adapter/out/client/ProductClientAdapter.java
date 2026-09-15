package academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.client;

import academy.digitallab.onlinestore.shopping.domain.model.Product;
import academy.digitallab.onlinestore.shopping.domain.port.out.ProductClientPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida hacia product-service sobre el HTTP Interface.
 *
 * <p>Ep. 8 — Resiliencia con Resilience4j: cada llamada se protege con retry y circuit
 * breaker; ante fallo definitivo se ejecuta un fallback que degrada la respuesta en lugar
 * de propagar el error. El timeout se configura en el RestClient (ver {@code HttpClientConfig}).
 */
@Component
public class ProductClientAdapter implements ProductClientPort {

    private static final Logger log = LoggerFactory.getLogger(ProductClientAdapter.class);

    private final ProductHttpClient httpClient;

    public ProductClientAdapter(ProductHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    // Orden de aspectos Resilience4j: Retry envuelve a CircuitBreaker. El fallback va en
    // @Retry para que se agoten los reintentos (registrando fallos en el CB) antes de degradar.
    @Override
    @CircuitBreaker(name = "productClient")
    @Retry(name = "productClient", fallbackMethod = "getProductFallback")
    public Product getProduct(Long id) {
        return httpClient.getProduct(id);
    }

    @Override
    @CircuitBreaker(name = "productClient")
    @Retry(name = "productClient", fallbackMethod = "updateStockFallback")
    public Product updateStock(Long id, Double quantity) {
        return httpClient.updateStock(id, quantity);
    }

    /** Fallback de lectura: devuelve un producto degradado para no romper la factura. */
    @SuppressWarnings("unused")
    private Product getProductFallback(Long id, Throwable t) {
        log.warn("Fallback getProduct({}): product-service no disponible ({})", id, t.toString());
        return new Product(id, "unavailable", "product-service no disponible", 0.0, 0.0);
    }

    /**
     * Fallback de escritura del stock. Se registra el fallo y se acepta la inconsistencia
     * conocida (la factura ya está creada). Ep. 12 lo resuelve con Saga + Outbox sobre Kafka.
     */
    @SuppressWarnings("unused")
    private Product updateStockFallback(Long id, Double quantity, Throwable t) {
        log.warn("Fallback updateStock({}, {}): no se pudo descontar stock ({})", id, quantity, t.toString());
        return null;
    }
}
