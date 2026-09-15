package academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.client;

import academy.digitallab.onlinestore.shopping.domain.model.Customer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * HTTP Interface de customer-service (Spring 7).
 */
@HttpExchange("/customers")
public interface CustomerHttpClient {

    @GetExchange("/{id}")
    Customer getCustomer(@PathVariable Long id);
}
