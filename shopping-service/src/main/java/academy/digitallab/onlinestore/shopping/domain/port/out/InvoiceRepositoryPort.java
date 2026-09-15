package academy.digitallab.onlinestore.shopping.domain.port.out;

import academy.digitallab.onlinestore.shopping.domain.model.Invoice;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: persistencia de facturas.
 */
public interface InvoiceRepositoryPort {

    List<Invoice> findAll();

    Optional<Invoice> findById(Long id);

    Invoice save(Invoice invoice);
}
