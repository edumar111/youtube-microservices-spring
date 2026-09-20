package academy.digitallab.onlinestore.shopping.infrastructure.messaging;

import academy.digitallab.onlinestore.shopping.domain.event.StockProcessedEvent;
import academy.digitallab.onlinestore.shopping.domain.port.in.InvoiceUseCase;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Ep. 12 — cierra la saga: consume el resultado del descuento de stock y actualiza la factura
 * (CONFIRMED en éxito, CANCELLED como compensación en fallo).
 */
@Component
@ConditionalOnProperty(prefix = "app.saga", name = "enabled", havingValue = "true")
public class StockProcessedListener {

    private static final Logger log = LoggerFactory.getLogger(StockProcessedListener.class);

    private final InvoiceUseCase invoiceUseCase;
    private final ObjectMapper objectMapper;

    public StockProcessedListener(InvoiceUseCase invoiceUseCase, ObjectMapper objectMapper) {
        this.invoiceUseCase = invoiceUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.saga.topics.stock-processed}", groupId = "shopping-saga")
    public void onStockProcessed(String message) {
        StockProcessedEvent event = objectMapper.readValue(message, StockProcessedEvent.class);
        String newState = event.success() ? "CONFIRMED" : "CANCELLED";
        invoiceUseCase.updateState(event.invoiceId(), newState);
        log.info("Saga: factura {} -> {} ({})", event.invoiceId(), newState,
                event.success() ? "stock ok" : event.reason());
    }
}
