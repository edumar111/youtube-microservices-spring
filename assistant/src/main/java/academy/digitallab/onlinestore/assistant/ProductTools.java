package academy.digitallab.onlinestore.assistant;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Ep. 15 — herramientas del asistente. El LLM las invoca (function calling) para consultar
 * datos reales del catálogo. Se exponen además como herramientas MCP por el starter mcp-server.
 */
@Component
public class ProductTools {

    private final RestClient restClient;

    public ProductTools(@Value("${clients.product.url:http://localhost:8091}") String productUrl) {
        this.restClient = RestClient.builder().baseUrl(productUrl).build();
    }

    /** Vista de producto que el modelo recibe como resultado de la herramienta. */
    public record ProductInfo(Long id, String name, String description, Double price, Double stock) {
    }

    @Tool(description = "Lista todos los productos disponibles en el catálogo de la tienda online")
    public List<ProductInfo> listProducts() {
        return restClient.get().uri("/products").retrieve()
                .body(new ParameterizedTypeReference<List<ProductInfo>>() {});
    }

    @Tool(description = "Obtiene el detalle de un producto por su identificador")
    public ProductInfo getProduct(@ToolParam(description = "Identificador numérico del producto") Long id) {
        return restClient.get().uri("/products/{id}", id).retrieve().body(ProductInfo.class);
    }
}
