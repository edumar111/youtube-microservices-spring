package academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.persistence;

import academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.persistence.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceJpaRepository extends JpaRepository<InvoiceEntity, Long> {
}
