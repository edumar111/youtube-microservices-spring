package academy.digitallab.onlinestore.shopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Shopping Service — facturación. Orquesta product-service y customer-service
 * mediante RestClient + HTTP Interfaces (descubrimiento por DNS de Docker).
 */
@SpringBootApplication
public class ShoppingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingServiceApplication.class, args);
    }
}
