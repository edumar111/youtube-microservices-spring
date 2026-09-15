package academy.digitallab.onlinestore.shopping.domain.service;

import academy.digitallab.onlinestore.shopping.domain.event.InvoiceCreatedEvent;
import academy.digitallab.onlinestore.shopping.domain.exception.NotFoundException;
import academy.digitallab.onlinestore.shopping.domain.model.Customer;
import academy.digitallab.onlinestore.shopping.domain.model.Invoice;
import academy.digitallab.onlinestore.shopping.domain.model.InvoiceItem;
import academy.digitallab.onlinestore.shopping.domain.port.in.InvoiceUseCase;
import academy.digitallab.onlinestore.shopping.domain.port.out.CustomerClientPort;
import academy.digitallab.onlinestore.shopping.domain.port.out.InvoiceRepositoryPort;
import academy.digitallab.onlinestore.shopping.domain.port.out.OutboxPort;
import academy.digitallab.onlinestore.shopping.domain.port.out.ProductClientPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Casos de uso de facturación. Orquesta product-service y customer-service a través
 * de puertos de salida.
 */
@Service
public class InvoiceService implements InvoiceUseCase {

    private final InvoiceRepositoryPort invoiceRepository;
    private final ProductClientPort productClient;
    private final CustomerClientPort customerClient;
    private final OutboxPort outboxPort;

    public InvoiceService(InvoiceRepositoryPort invoiceRepository,
                          ProductClientPort productClient,
                          CustomerClientPort customerClient,
                          OutboxPort outboxPort) {
        this.invoiceRepository = invoiceRepository;
        this.productClient = productClient;
        this.customerClient = customerClient;
        this.outboxPort = outboxPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Override
    @Transactional
    public Invoice create(Invoice invoice) {
        // Ep. 12 — Saga + Outbox: la factura nace en estado PENDING y, en la MISMA transacción,
        // se registra el evento en la outbox. Ya no se descuenta stock por HTTP síncrono; un
        // relay publica el evento a Kafka y product-service descuenta el stock de forma asíncrona.
        // El resultado (éxito/fallo) vuelve por Kafka y actualiza el estado (CONFIRMED/CANCELLED).
        Invoice saved = invoiceRepository.save(invoice.withState("PENDING"));

        InvoiceCreatedEvent event = new InvoiceCreatedEvent(
                saved.id(),
                saved.customerId(),
                saved.items().stream()
                        .map(i -> new InvoiceCreatedEvent.Item(i.productId(), i.quantity()))
                        .toList());
        outboxPort.append("INVOICE", saved.id(), "InvoiceCreated", event);

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Invoice not found: " + id));

        // Enriquecer con el cliente
        Customer customer = customerClient.getCustomer(invoice.customerId());
        Invoice enriched = invoice.withCustomer(customer);

        // Enriquecer cada ítem con su producto.
        // FIX (bug del curso viejo): antes usaba item.getId() en vez de item.getProductId().
        List<InvoiceItem> items = enriched.items().stream()
                .map(item -> item.withProduct(productClient.getProduct(item.productId())))
                .toList();

        return enriched.withItems(items);
    }

    @Override
    @Transactional
    public Invoice update(Long id, Invoice changes) {
        Invoice current = invoiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Invoice not found: " + id));
        Invoice updated = new Invoice(
                current.id(),
                changes.numberInvoice() != null ? changes.numberInvoice() : current.numberInvoice(),
                changes.description() != null ? changes.description() : current.description(),
                changes.customerId() != null ? changes.customerId() : current.customerId(),
                null,
                current.createdAt(),
                changes.items() != null ? changes.items() : current.items(),
                current.state());
        return invoiceRepository.save(updated);
    }

    @Override
    @Transactional
    public Invoice delete(Long id) {
        Invoice current = invoiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Invoice not found: " + id));
        return invoiceRepository.save(current.withState("DELETED"));
    }

    @Override
    @Transactional
    public Invoice updateState(Long id, String state) {
        Invoice current = invoiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Invoice not found: " + id));
        return invoiceRepository.save(current.withState(state));
    }
}
