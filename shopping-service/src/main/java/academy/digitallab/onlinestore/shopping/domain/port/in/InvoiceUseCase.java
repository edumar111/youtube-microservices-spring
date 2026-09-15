package academy.digitallab.onlinestore.shopping.domain.port.in;

import academy.digitallab.onlinestore.shopping.domain.model.Invoice;

import java.util.List;

/**
 * Puerto de entrada: casos de uso de facturación.
 */
public interface InvoiceUseCase {

    List<Invoice> findAll();

    Invoice findById(Long id);

    Invoice create(Invoice invoice);

    Invoice update(Long id, Invoice invoice);

    Invoice delete(Long id);

    /** Ep. 12: actualiza el estado de la factura según el resultado de la saga. */
    Invoice updateState(Long id, String state);
}
