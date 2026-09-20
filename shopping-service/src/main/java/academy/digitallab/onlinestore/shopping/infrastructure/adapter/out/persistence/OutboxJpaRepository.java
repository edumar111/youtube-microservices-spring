package academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.persistence;

import academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.persistence.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<OutboxEventEntity, Long> {

    List<OutboxEventEntity> findTop100BySentFalseOrderByIdAsc();
}
