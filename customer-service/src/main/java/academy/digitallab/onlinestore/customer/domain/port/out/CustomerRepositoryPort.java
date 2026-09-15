package academy.digitallab.onlinestore.customer.domain.port.out;

import academy.digitallab.onlinestore.customer.domain.model.Customer;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: persistencia de clientes.
 */
public interface CustomerRepositoryPort {

    List<Customer> findAll();

    Optional<Customer> findById(Long id);

    List<Customer> findByRegionId(Long regionId);

    Customer save(Customer customer);
}
