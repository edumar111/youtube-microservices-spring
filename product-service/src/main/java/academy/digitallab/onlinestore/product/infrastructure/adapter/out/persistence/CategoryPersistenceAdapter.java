package academy.digitallab.onlinestore.product.infrastructure.adapter.out.persistence;

import academy.digitallab.onlinestore.product.domain.model.Category;
import academy.digitallab.onlinestore.product.domain.port.out.CategoryRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida que implementa {@link CategoryRepositoryPort} con Spring Data JPA.
 */
@Component
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {

    private final CategoryJpaRepository jpaRepository;
    private final ProductPersistenceMapper mapper;

    public CategoryPersistenceAdapter(CategoryJpaRepository jpaRepository, ProductPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Category save(Category category) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(category)));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
