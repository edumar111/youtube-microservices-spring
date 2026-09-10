package academy.digitallab.onlinestore.product.infrastructure.adapter.out.persistence;

import academy.digitallab.onlinestore.product.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {
}
