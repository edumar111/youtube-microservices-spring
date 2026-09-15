package academy.digitallab.onlinestore.customer.infrastructure.adapter.out.persistence;

import academy.digitallab.onlinestore.customer.domain.model.Customer;
import academy.digitallab.onlinestore.customer.domain.port.out.CustomerRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CustomerPersistenceAdapter implements CustomerRepositoryPort {

    private final CustomerJpaRepository jpaRepository;
    private final CustomerPersistenceMapper mapper;

    public CustomerPersistenceAdapter(CustomerJpaRepository jpaRepository, CustomerPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Customer> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Customer> findByRegionId(Long regionId) {
        return jpaRepository.findByRegionId(regionId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Customer save(Customer customer) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(customer)));
    }
}
