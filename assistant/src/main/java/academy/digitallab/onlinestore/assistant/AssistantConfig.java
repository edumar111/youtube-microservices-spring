package academy.digitallab.onlinestore.assistant;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ep. 15 — registra las herramientas (@Tool de {@link ProductTools}) como un
 * {@link ToolCallbackProvider}, que es lo que el <b>servidor MCP</b> publica para que
 * cualquier cliente MCP (otro LLM, un IDE, otro agente) las descubra e invoque.
 */
@Configuration
public class AssistantConfig {

    @Bean
    public ToolCallbackProvider productToolCallbackProvider(ProductTools productTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(productTools)
                .build();
    }
}
