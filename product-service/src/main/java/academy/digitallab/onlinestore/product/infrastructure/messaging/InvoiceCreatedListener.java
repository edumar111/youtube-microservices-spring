package academy.digitallab.onlinestore.product.infrastructure.messaging;

import academy.digitallab.onlinestore.product.domain.port.in.ProductUseCase;
import academy.digitallab.onlinestore.product.infrastructure.messaging.SagaEvents.InvoiceCreatedEvent;
import academy.digitallab.onlinestore.product.infrastructure.messaging.SagaEvents.StockProcessedEvent;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Ep. 12 — participante de la saga en product-service: al recibir InvoiceCreated descuenta
 * el stock de cada ítem y publica el resultado (StockProcessed) para que shopping confirme
 * o compense la factura.
 */
@Component
@ConditionalOnProperty(prefix = "app.saga", name = "enabled", havingValue = "true")
public class InvoiceCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(InvoiceCreatedListener.class);

    private final ProductUseCase productUseCase;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String stockProcessedTopic;

    public InvoiceCreatedListener(ProductUseCase productUseCase,
                                  KafkaTemplate<String, String> kafkaTemplate,
                                  ObjectMapper objectMapper,
                                  @Value("${app.saga.topics.stock-processed}") String stockProcessedTopic) {
        this.productUseCase = productUseCase;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.stockProcessedTopic = stockProcessedTopic;
    }

    @KafkaListener(topics = "${app.saga.topics.invoice-created}", groupId = "product-saga")
    public void onInvoiceCreated(String message) {
        InvoiceCreatedEvent event = objectMapper.readValue(message, InvoiceCreatedEvent.class);
        StockProcessedEvent result;
        try {
            for (InvoiceCreatedEvent.Item item : event.items()) {
                productUseCase.updateStock(item.productId(), item.quantity());
            }
            result = new StockProcessedEvent(event.invoiceId(), true, null);
            log.info("Saga: stock descontado para factura {}", event.invoiceId());
        } catch (Exception ex) {
            result = new StockProcessedEvent(event.invoiceId(), false, ex.getMessage());
            log.warn("Saga: fallo al descontar stock para factura {}: {}", event.invoiceId(), ex.toString());
        }
        kafkaTemplate.send(stockProcessedTopic, String.valueOf(event.invoiceId()),
                objectMapper.writeValueAsString(result));
    }
}
