package academy.digitallab.onlinestore.product;

import academy.digitallab.onlinestore.product.domain.port.in.ProductUseCase;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Ep. 12 — verifica el participante de la saga: al publicar InvoiceCreated en Kafka,
 * product-service descuenta el stock del producto. Usa PostgreSQL y Kafka reales (Testcontainers)
 * y activa la saga con app.saga.enabled=true.
 */
@SpringBootTest
@ActiveProfiles("postgres")
@Testcontainers
@TestPropertySource(properties = "app.saga.enabled=true")
class ProductSagaKafkaTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Container
    static ConfluentKafkaContainer kafka = new ConfluentKafkaContainer("confluentinc/cp-kafka:7.8.1");

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    ProductUseCase productUseCase;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void consumesInvoiceCreatedAndDecrementsStock() throws Exception {
        double stockBefore = productUseCase.findById(1L).stock();

        Map<String, Object> event = Map.of(
                "invoiceId", 1,
                "customerId", 1,
                "items", List.of(Map.of("productId", 1, "quantity", 2.0)));
        kafkaTemplate.send("invoice-created", "1", objectMapper.writeValueAsString(event));

        await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                assertThat(productUseCase.findById(1L).stock()).isEqualTo(stockBefore - 2.0));
    }
}
