package academy.digitallab.onlinestore.shopping.domain.event;

/**
 * Resultado del procesamiento de stock en product-service (ep. 12). Cierra la saga:
 * éxito → factura CONFIRMED; fallo → compensación → factura CANCELLED.
 */
public record StockProcessedEvent(Long invoiceId, boolean success, String reason) {
}
