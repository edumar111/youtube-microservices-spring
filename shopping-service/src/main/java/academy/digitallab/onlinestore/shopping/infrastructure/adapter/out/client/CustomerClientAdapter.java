package academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.client;

import academy.digitallab.onlinestore.shopping.domain.model.Customer;
import academy.digitallab.onlinestore.shopping.domain.port.out.CustomerClientPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida hacia customer-service sobre el HTTP Interface, con Resilience4j (ep. 8).
 */
@Component
public class CustomerClientAdapter implements CustomerClientPort {

    private static final Logger log = LoggerFactory.getLogger(CustomerClientAdapter.class);

    private final CustomerHttpClient httpClient;

    public CustomerClientAdapter(CustomerHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    @CircuitBreaker(name = "customerClient")
    @Retry(name = "customerClient", fallbackMethod = "getCustomerFallback")
    public Customer getCustomer(Long id) {
        return httpClient.getCustomer(id);
    }

    @SuppressWarnings("unused")
    private Customer getCustomerFallback(Long id, Throwable t) {
        log.warn("Fallback getCustomer({}): customer-service no disponible ({})", id, t.toString());
        return new Customer(id, "unavailable", "unavailable", "unavailable");
    }
}
