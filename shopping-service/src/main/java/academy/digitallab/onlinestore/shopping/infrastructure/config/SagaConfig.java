package academy.digitallab.onlinestore.shopping.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ep. 12 — habilita la mensajería de la saga (relay de outbox y listeners) solo cuando
 * {@code app.saga.enabled=true}. Así los tests que no necesitan Kafka arrancan sin él.
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "app.saga", name = "enabled", havingValue = "true")
public class SagaConfig {
}
