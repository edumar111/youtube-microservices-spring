package academy.digitallab.onlinestore.shopping.infrastructure.adapter.in.web.dto;

import academy.digitallab.onlinestore.shopping.domain.model.Customer;
import academy.digitallab.onlinestore.shopping.domain.model.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * DTOs de factura para la API REST.
 */
public final class InvoiceDtos {

    private InvoiceDtos() {
    }

    public record InvoiceItemRequest(
            @NotNull Long productId,
            @NotNull Double quantity,
            @NotNull Double price) {
    }

    public record InvoiceRequest(
            String numberInvoice,
            String description,
            @NotNull Long customerId,
            @NotEmpty @Valid List<InvoiceItemRequest> items) {
    }

    public record InvoiceItemResponse(
            Long id,
            Long productId,
            Double quantity,
            Double price,
            Product product) {
    }

    public record InvoiceResponse(
            Long id,
            String numberInvoice,
            String description,
            Long customerId,
            Customer customer,
            LocalDate createdAt,
            List<InvoiceItemResponse> items,
            String state) {
    }
}
