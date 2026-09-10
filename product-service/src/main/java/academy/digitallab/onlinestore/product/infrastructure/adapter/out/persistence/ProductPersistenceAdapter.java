package academy.digitallab.onlinestore.product.infrastructure.adapter.out.persistence;

import academy.digitallab.onlinestore.product.domain.model.Product;
import academy.digitallab.onlinestore.product.domain.port.out.ProductRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida que implementa el puerto {@link ProductRepositoryPort}
 * usando Spring Data JPA. Convierte entidades a dominio con el mapper.
 */
@Component
public class ProductPersistenceAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository jpaRepository;
    private final ProductPersistenceMapper mapper;

    public ProductPersistenceAdapter(ProductJpaRepository jpaRepository, ProductPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return jpaRepository.findByCategoryId(categoryId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Product save(Product product) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(product)));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
