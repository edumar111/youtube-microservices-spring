package academy.digitallab.onlinestore.product.infrastructure.adapter.out.persistence;

import academy.digitallab.onlinestore.product.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByCategoryId(Long categoryId);
}
