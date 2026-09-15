package academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.client;

import academy.digitallab.onlinestore.shopping.domain.model.Product;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * HTTP Interface de product-service (Spring 7). Reemplaza a Feign del curso viejo.
 */
@HttpExchange("/products")
public interface ProductHttpClient {

    @GetExchange("/{id}")
    Product getProduct(@PathVariable Long id);

    @GetExchange("/{id}/stock")
    Product updateStock(@PathVariable Long id, @RequestParam Double quantity);
}
