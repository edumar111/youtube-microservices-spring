package academy.digitallab.onlinestore.customer.domain.service;

import academy.digitallab.onlinestore.customer.domain.exception.NotFoundException;
import academy.digitallab.onlinestore.customer.domain.model.Customer;
import academy.digitallab.onlinestore.customer.domain.port.in.CustomerUseCase;
import academy.digitallab.onlinestore.customer.domain.port.out.CustomerRepositoryPort;
import academy.digitallab.onlinestore.customer.domain.port.out.RegionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Casos de uso de cliente. Inyección por constructor; depende solo de puertos.
 */
@Service
public class CustomerService implements CustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final RegionRepositoryPort regionRepository;

    public CustomerService(CustomerRepositoryPort customerRepository, RegionRepositoryPort regionRepository) {
        this.customerRepository = customerRepository;
        this.regionRepository = regionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findByRegion(Long regionId) {
        return customerRepository.findByRegionId(regionId);
    }

    @Override
    @Transactional
    public Customer create(Customer customer) {
        if (customer.region() == null || customer.region().id() == null) {
            throw new NotFoundException("Region is required to create a customer");
        }
        regionRepository.findById(customer.region().id())
                .orElseThrow(() -> new NotFoundException("Region not found: " + customer.region().id()));

        Customer toCreate = new Customer(
                null,
                customer.firstName(),
                customer.lastName(),
                customer.email(),
                customer.photoUrl(),
                customer.region(),
                "CREATED");
        return customerRepository.save(toCreate);
    }

    @Override
    @Transactional
    public Customer update(Long id, Customer changes) {
        Customer current = findById(id);
        Customer updated = new Customer(
                current.id(),
                changes.firstName() != null ? changes.firstName() : current.firstName(),
                changes.lastName() != null ? changes.lastName() : current.lastName(),
                changes.email() != null ? changes.email() : current.email(),
                changes.photoUrl() != null ? changes.photoUrl() : current.photoUrl(),
                current.region(),
                current.state());
        return customerRepository.save(updated);
    }

    @Override
    @Transactional
    public Customer delete(Long id) {
        Customer current = findById(id);
        return customerRepository.save(current.withState("DELETED"));
    }
}
