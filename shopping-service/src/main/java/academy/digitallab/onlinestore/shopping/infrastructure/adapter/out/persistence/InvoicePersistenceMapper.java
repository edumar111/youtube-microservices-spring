package academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.persistence;

import academy.digitallab.onlinestore.shopping.domain.model.Invoice;
import academy.digitallab.onlinestore.shopping.domain.model.InvoiceItem;
import academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.persistence.entity.InvoiceEntity;
import academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.persistence.entity.InvoiceItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Traduce entre entidades JPA de factura y modelos de dominio.
 */
@Component
public class InvoicePersistenceMapper {

    public InvoiceItem toDomain(InvoiceItemEntity entity) {
        return new InvoiceItem(entity.getId(), entity.getQuantity(), entity.getPrice(), entity.getProductId(), null);
    }

    public InvoiceItemEntity toEntity(InvoiceItem item) {
        return new InvoiceItemEntity(item.id(), item.quantity(), item.price(), item.productId());
    }

    public Invoice toDomain(InvoiceEntity entity) {
        List<InvoiceItem> items = entity.getItems().stream().map(this::toDomain).toList();
        return new Invoice(
                entity.getId(),
                entity.getNumberInvoice(),
                entity.getDescription(),
                entity.getCustomerId(),
                null,
                entity.getCreateAt(),
                items,
                entity.getState());
    }

    public InvoiceEntity toEntity(Invoice invoice) {
        InvoiceEntity entity = new InvoiceEntity();
        entity.setId(invoice.id());
        entity.setNumberInvoice(invoice.numberInvoice());
        entity.setDescription(invoice.description());
        entity.setCustomerId(invoice.customerId());
        entity.setCreateAt(invoice.createdAt());
        entity.setState(invoice.state());
        if (invoice.items() != null) {
            entity.setItems(new java.util.ArrayList<>(invoice.items().stream().map(this::toEntity).toList()));
        }
        return entity;
    }
}
