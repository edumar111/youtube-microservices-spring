package academy.digitallab.onlinestore.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Product Service — catálogo de productos y categorías.
 *
 * <p>Arquitectura hexagonal: el dominio (modelos, puertos y casos de uso) no conoce
 * Spring ni JPA. La infraestructura provee los adaptadores REST (entrada) y de
 * persistencia (salida). Virtual threads habilitados vía {@code spring.threads.virtual.enabled}.
 */
@SpringBootApplication
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
