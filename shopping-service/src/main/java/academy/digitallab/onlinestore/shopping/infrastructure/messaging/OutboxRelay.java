package academy.digitallab.onlinestore.shopping.infrastructure.messaging;

import academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.persistence.OutboxJpaRepository;
import academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.persistence.entity.OutboxEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Ep. 12 — Relay del Outbox: sondea periódicamente los eventos no enviados y los publica en
 * Kafka, marcándolos como enviados. Desacopla la escritura transaccional de la publicación.
 */
@Component
@ConditionalOnProperty(prefix = "app.saga", name = "enabled", havingValue = "true")
public class OutboxRelay {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelay.class);

    private final OutboxJpaRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String invoiceCreatedTopic;

    public OutboxRelay(OutboxJpaRepository repository,
                       KafkaTemplate<String, String> kafkaTemplate,
                       @Value("${app.saga.topics.invoice-created}") String invoiceCreatedTopic) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.invoiceCreatedTopic = invoiceCreatedTopic;
    }

    @Scheduled(fixedDelayString = "${app.saga.relay-delay-ms:1000}")
    @Transactional
    public void publishPending() {
        List<OutboxEventEntity> pending = repository.findTop100BySentFalseOrderByIdAsc();
        for (OutboxEventEntity event : pending) {
            try {
                kafkaTemplate.send(invoiceCreatedTopic, String.valueOf(event.getAggregateId()), event.getPayload())
                        .get();
                event.setSent(true);
                log.debug("Outbox event {} publicado en {}", event.getId(), invoiceCreatedTopic);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.warn("No se pudo publicar el evento outbox {}: {}", event.getId(), e.toString());
                break; // se reintenta en el próximo ciclo
            }
        }
        repository.saveAll(pending);
    }
}
