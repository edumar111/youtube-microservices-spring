package academy.digitallab.onlinestore.assistant;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ep. 15 — respuesta cuando el LLM está desactivado ({@code LLM_ENABLED=false}, por defecto).
 * Permite que el servicio arranque sin {@code ANTHROPIC_API_KEY} y que el widget reciba un
 * mensaje claro en lugar de un error.
 */
@RestController
@RequestMapping("/assistant")
@ConditionalOnProperty(prefix = "llm", name = "enabled", havingValue = "false", matchIfMissing = true)
public class AssistantDisabledController {

    public record ChatRequest(String message) {
    }

    public record ChatResponse(String answer) {
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody(required = false) ChatRequest request) {
        return new ChatResponse(
                "El asistente con IA está desactivado. Actívalo con LLM_ENABLED=true y una ANTHROPIC_API_KEY válida.");
    }
}
