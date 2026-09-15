package academy.digitallab.onlinestore.shopping.domain.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Factura. {@code customer} se enriquece bajo demanda desde customer-service.
 */
public record Invoice(
        Long id,
        String numberInvoice,
        String description,
        Long customerId,
        Customer customer,
        LocalDate createdAt,
        List<InvoiceItem> items,
        String state
) {

    public Invoice withId(Long newId) {
        return new Invoice(newId, numberInvoice, description, customerId, customer, createdAt, items, state);
    }

    public Invoice withState(String newState) {
        return new Invoice(id, numberInvoice, description, customerId, customer, createdAt, items, newState);
    }

    public Invoice withCustomer(Customer resolved) {
        return new Invoice(id, numberInvoice, description, customerId, resolved, createdAt, items, state);
    }

    public Invoice withItems(List<InvoiceItem> newItems) {
        return new Invoice(id, numberInvoice, description, customerId, customer, createdAt, newItems, state);
    }
}
