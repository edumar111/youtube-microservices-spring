package academy.digitallab.onlinestore.customer.infrastructure.adapter.out.persistence;

import academy.digitallab.onlinestore.customer.infrastructure.adapter.out.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long> {

    List<CustomerEntity> findByRegionId(Long regionId);
}
