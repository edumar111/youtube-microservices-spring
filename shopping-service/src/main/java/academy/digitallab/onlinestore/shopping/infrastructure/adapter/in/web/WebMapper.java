package academy.digitallab.onlinestore.shopping.infrastructure.adapter.in.web;

import academy.digitallab.onlinestore.shopping.domain.model.Invoice;
import academy.digitallab.onlinestore.shopping.domain.model.InvoiceItem;
import academy.digitallab.onlinestore.shopping.infrastructure.adapter.in.web.dto.InvoiceDtos.InvoiceItemResponse;
import academy.digitallab.onlinestore.shopping.infrastructure.adapter.in.web.dto.InvoiceDtos.InvoiceRequest;
import academy.digitallab.onlinestore.shopping.infrastructure.adapter.in.web.dto.InvoiceDtos.InvoiceResponse;
import org.springframework.stereotype.Component;

@Component
public class WebMapper {

    public Invoice toDomain(InvoiceRequest request) {
        var items = request.items().stream()
                .map(i -> new InvoiceItem(null, i.quantity(), i.price(), i.productId(), null))
                .toList();
        return new Invoice(
                null,
                request.numberInvoice(),
                request.description(),
                request.customerId(),
                null,
                null,
                items,
                null);
    }

    public InvoiceResponse toResponse(Invoice invoice) {
        var items = invoice.items().stream()
                .map(this::toResponse)
                .toList();
        return new InvoiceResponse(
                invoice.id(),
                invoice.numberInvoice(),
                invoice.description(),
                invoice.customerId(),
                invoice.customer(),
                invoice.createdAt(),
                items,
                invoice.state());
    }

    private InvoiceItemResponse toResponse(InvoiceItem item) {
        return new InvoiceItemResponse(item.id(), item.productId(), item.quantity(), item.price(), item.product());
    }
}
