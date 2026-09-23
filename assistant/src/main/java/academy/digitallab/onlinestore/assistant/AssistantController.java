package academy.digitallab.onlinestore.assistant;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ep. 15 — endpoint del asistente de compras. El ChatClient usa las herramientas de catálogo
 * para responder con datos reales de los microservicios.
 */
@RestController
@RequestMapping("/assistant")
public class AssistantController {

    private final ChatClient chatClient;
    private final ProductTools productTools;

    public AssistantController(ChatClient.Builder builder, ProductTools productTools) {
        this.productTools = productTools;
        this.chatClient = builder
                .defaultSystem("""
                        Eres el asistente de compras de una tienda online. Responde en español,
                        de forma breve y útil. Usa las herramientas disponibles para consultar
                        el catálogo real en lugar de inventar productos, precios o stock.
                        """)
                .build();
    }

    public record ChatRequest(String message) {
    }

    public record ChatResponse(String answer) {
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String answer = chatClient.prompt()
                .user(request.message())
                .tools(productTools)
                .call()
                .content();
        return new ChatResponse(answer);
    }
}
