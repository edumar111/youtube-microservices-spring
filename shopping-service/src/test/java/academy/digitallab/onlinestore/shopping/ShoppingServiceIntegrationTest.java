package academy.digitallab.onlinestore.shopping;

import academy.digitallab.onlinestore.shopping.domain.model.Customer;
import academy.digitallab.onlinestore.shopping.domain.model.Invoice;
import academy.digitallab.onlinestore.shopping.domain.model.InvoiceItem;
import academy.digitallab.onlinestore.shopping.domain.model.Product;
import academy.digitallab.onlinestore.shopping.domain.port.in.InvoiceUseCase;
import academy.digitallab.onlinestore.shopping.domain.port.out.CustomerClientPort;
import academy.digitallab.onlinestore.shopping.domain.port.out.ProductClientPort;
import academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.persistence.OutboxJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test de integración de shopping con PostgreSQL real (Testcontainers). Los puertos hacia
 * product-service y customer-service se mockean para no depender de otros servicios.
 */
@SpringBootTest
@ActiveProfiles("postgres")
@Testcontainers
class ShoppingServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    InvoiceUseCase invoiceUseCase;

    @MockitoBean
    ProductClientPort productClient;

    @MockitoBean
    CustomerClientPort customerClient;

    @Autowired
    OutboxJpaRepository outboxRepository;

    @Test
    void createInvoiceIsPendingAndWritesOutboxEvent() {
        Invoice invoice = new Invoice(null, "0002", "compra", 1L, null, null,
                List.of(new InvoiceItem(null, 3.0, 10.0, 1L, null)), null);

        long outboxBefore = outboxRepository.count();
        Invoice created = invoiceUseCase.create(invoice);

        // Ep. 12: la factura nace PENDING; el descuento de stock es asíncrono (saga).
        assertThat(created.id()).isNotNull();
        assertThat(created.state()).isEqualTo("PENDING");
        // Se escribió un evento en la outbox en la misma transacción.
        assertThat(outboxRepository.count()).isEqualTo(outboxBefore + 1);
        assertThat(outboxRepository.findTop100BySentFalseOrderByIdAsc())
                .anyMatch(e -> "InvoiceCreated".equals(e.getEventType())
                        && e.getAggregateId().equals(created.id()));
    }

    @Test
    void findByIdEnrichesUsingProductIdNotItemId() {
        // La factura demo (data.sql) tiene id=1 con ítems cuyos product_id son 1, 2 y 3.
        when(customerClient.getCustomer(1L)).thenReturn(new Customer(1L, "Andrés", "Guzmán", "a@b.com"));
        when(productClient.getProduct(any())).thenReturn(new Product(0L, "p", "d", 1.0, 1.0));

        Invoice invoice = invoiceUseCase.findById(1L);

        assertThat(invoice.customer()).isNotNull();
        assertThat(invoice.items()).allSatisfy(item -> assertThat(item.product()).isNotNull());

        // FIX del bug: se consulta por product_id (1,2,3), nunca por el id del ítem.
        verify(productClient).getProduct(1L);
        verify(productClient).getProduct(2L);
        verify(productClient).getProduct(3L);
    }
}
