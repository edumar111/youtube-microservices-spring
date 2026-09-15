package academy.digitallab.onlinestore.customer.domain.port.in;

import academy.digitallab.onlinestore.customer.domain.model.Customer;

import java.util.List;

/**
 * Puerto de entrada: casos de uso de clientes.
 */
public interface CustomerUseCase {

    List<Customer> findAll();

    Customer findById(Long id);

    List<Customer> findByRegion(Long regionId);

    Customer create(Customer customer);

    Customer update(Long id, Customer customer);

    Customer delete(Long id);
}
