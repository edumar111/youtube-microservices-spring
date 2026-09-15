package academy.digitallab.onlinestore.customer.infrastructure.adapter.out.persistence;

import academy.digitallab.onlinestore.customer.domain.model.Region;
import academy.digitallab.onlinestore.customer.domain.port.out.RegionRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RegionPersistenceAdapter implements RegionRepositoryPort {

    private final RegionJpaRepository jpaRepository;
    private final CustomerPersistenceMapper mapper;

    public RegionPersistenceAdapter(RegionJpaRepository jpaRepository, CustomerPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Region> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Region> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
