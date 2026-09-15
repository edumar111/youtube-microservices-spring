package academy.digitallab.onlinestore.customer.infrastructure.adapter.out.persistence;

import academy.digitallab.onlinestore.customer.infrastructure.adapter.out.persistence.entity.RegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionJpaRepository extends JpaRepository<RegionEntity, Long> {
}
