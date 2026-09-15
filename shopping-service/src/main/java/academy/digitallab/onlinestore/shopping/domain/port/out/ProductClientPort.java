package academy.digitallab.onlinestore.shopping.domain.port.out;

import academy.digitallab.onlinestore.shopping.domain.model.Product;

/**
 * Puerto de salida hacia product-service. El dominio no sabe si detrás hay HTTP,
 * mensajería o un stub; solo depende de esta abstracción.
 */
public interface ProductClientPort {

    Product getProduct(Long id);

    Product updateStock(Long id, Double quantity);
}
