package academy.digitallab.onlinestore.customer;

import academy.digitallab.onlinestore.customer.domain.model.Customer;
import academy.digitallab.onlinestore.customer.domain.port.in.CustomerUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integración con PostgreSQL real (Testcontainers) sobre el caso de uso de cliente.
 */
@SpringBootTest
@ActiveProfiles("postgres")
@Testcontainers
class CustomerServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    CustomerUseCase customerUseCase;

    @Test
    void seedCustomerIsLoaded() {
        Customer customer = customerUseCase.findById(1L);
        assertThat(customer.firstName()).isEqualTo("Andrés");
        assertThat(customer.lastName()).isEqualTo("Guzmán");
        assertThat(customer.region().name()).isEqualTo("Sudamérica");
    }

    @Test
    void createCustomerAssignsCreatedState() {
        Customer created = customerUseCase.create(new Customer(
                null, "Ada", "Lovelace", "ada@example.com", "",
                new academy.digitallab.onlinestore.customer.domain.model.Region(4L, null), null));
        assertThat(created.id()).isNotNull();
        assertThat(created.state()).isEqualTo("CREATED");
    }
}
