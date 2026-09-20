package academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.persistence;

import academy.digitallab.onlinestore.shopping.domain.port.out.OutboxPort;
import academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.persistence.entity.OutboxEventEntity;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Adaptador del puerto Outbox: serializa el evento a JSON y lo persiste. Participa en la
 * transacción del caso de uso que lo invoca (no abre una nueva).
 */
@Component
public class OutboxPersistenceAdapter implements OutboxPort {

    private final OutboxJpaRepository repository;
    private final ObjectMapper objectMapper;

    public OutboxPersistenceAdapter(OutboxJpaRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void append(String aggregateType, Long aggregateId, String eventType, Object payload) {
        // Jackson 3: writeValueAsString ya no lanza excepción chequeada.
        String json = objectMapper.writeValueAsString(payload);
        repository.save(new OutboxEventEntity(aggregateType, aggregateId, eventType, json));
    }
}
