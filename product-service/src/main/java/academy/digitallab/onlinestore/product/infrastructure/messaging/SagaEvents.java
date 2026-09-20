package academy.digitallab.onlinestore.product.infrastructure.messaging;

import java.util.List;

/**
 * Contratos de eventos de la saga (ep. 12) vistos desde product-service.
 * Se (de)serializan como JSON; no hay módulo compartido entre servicios a propósito.
 */
public final class SagaEvents {

    private SagaEvents() {
    }

    public record InvoiceCreatedEvent(Long invoiceId, Long customerId, List<Item> items) {
        public record Item(Long productId, Double quantity) {
        }
    }

    public record StockProcessedEvent(Long invoiceId, boolean success, String reason) {
    }
}
