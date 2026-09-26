package academy.digitallab.onlinestore.assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

/**
 * Ep. 15 — traduce el interruptor {@code LLM_ENABLED} a la propiedad de Spring AI que activa o
 * desactiva la autoconfiguración del modelo de chat: {@code spring.ai.model.chat}.
 *
 * <p>Con {@code LLM_ENABLED=false} (por defecto) queda en {@code none}: el asistente arranca sin
 * necesidad de {@code ANTHROPIC_API_KEY}. Con {@code true} usa {@code anthropic} (Claude).
 */
public class LlmToggleEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        boolean enabled = environment.getProperty("llm.enabled", Boolean.class, false);
        String provider = enabled ? "anthropic" : "none";
        environment.getPropertySources().addFirst(
                new MapPropertySource("llm-toggle", Map.of("spring.ai.model.chat", provider)));
    }
}
