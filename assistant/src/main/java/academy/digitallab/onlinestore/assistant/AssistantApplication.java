package academy.digitallab.onlinestore.assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Asistente de compras (ep. 15) con Spring AI. Usa un modelo Claude (Anthropic) y expone
 * herramientas (@Tool) que consultan los microservicios; esas mismas herramientas se publican
 * como servidor MCP. Requiere ANTHROPIC_API_KEY para ejecutarse.
 */
@SpringBootApplication
public class AssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssistantApplication.class, args);
    }
}
