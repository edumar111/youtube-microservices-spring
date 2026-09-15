package academy.digitallab.onlinestore.shopping.domain.model;

/**
 * Ítem de factura. {@code product} se enriquece bajo demanda desde product-service.
 */
public record InvoiceItem(
        Long id,
        Double quantity,
        Double price,
        Long productId,
        Product product
) {

    public InvoiceItem withProduct(Product resolved) {
        return new InvoiceItem(id, quantity, price, productId, resolved);
    }
}
