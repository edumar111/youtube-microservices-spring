package academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.persistence;

import academy.digitallab.onlinestore.shopping.domain.model.Invoice;
import academy.digitallab.onlinestore.shopping.domain.port.out.InvoiceRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InvoicePersistenceAdapter implements InvoiceRepositoryPort {

    private final InvoiceJpaRepository jpaRepository;
    private final InvoicePersistenceMapper mapper;

    public InvoicePersistenceAdapter(InvoiceJpaRepository jpaRepository, InvoicePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Invoice> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Invoice save(Invoice invoice) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(invoice)));
    }
}
