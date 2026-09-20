package academy.digitallab.onlinestore.shopping.domain.event;

import java.util.List;

/**
 * Evento de dominio publicado cuando se crea una factura (ep. 12). Dispara la saga que
 * descuenta stock en product-service.
 */
public record InvoiceCreatedEvent(Long invoiceId, Long customerId, List<Item> items) {

    public record Item(Long productId, Double quantity) {
    }
}
