package academy.digitallab.onlinestore.shopping.domain.port.out;

import academy.digitallab.onlinestore.shopping.domain.model.Customer;

/**
 * Puerto de salida hacia customer-service.
 */
public interface CustomerClientPort {

    Customer getCustomer(Long id);
}
