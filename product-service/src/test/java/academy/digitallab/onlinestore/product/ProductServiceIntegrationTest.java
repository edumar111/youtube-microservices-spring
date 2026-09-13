package academy.digitallab.onlinestore.product;

import academy.digitallab.onlinestore.product.domain.exception.InsufficientStockException;
import academy.digitallab.onlinestore.product.domain.model.Product;
import academy.digitallab.onlinestore.product.domain.port.in.ProductUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test de integración con PostgreSQL real (Testcontainers), ejercitando el caso de uso
 * de producto sobre el adaptador de persistencia JPA y las semillas de data.sql.
 */
@SpringBootTest
@ActiveProfiles("postgres")
@Testcontainers
class ProductServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    ProductUseCase productUseCase;

    @Test
    void seedsAreLoaded() {
        List<Product> products = productUseCase.findAll();
        assertThat(products).hasSize(3);
        assertThat(products).extracting(Product::name)
                .contains("adidas Cloudfoam Ultimate", "Spring Boot in Action");
    }

    @Test
    void updateStockDecrementsQuantity() {
        Product before = productUseCase.findById(1L);
        Product after = productUseCase.updateStock(1L, 2.0);
        assertThat(after.stock()).isEqualTo(before.stock() - 2.0);
    }

    @Test
    void updateStockFailsWhenInsufficient() {
        assertThatThrownBy(() -> productUseCase.updateStock(2L, 9999.0))
                .isInstanceOf(InsufficientStockException.class);
    }
}
