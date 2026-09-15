package academy.digitallab.onlinestore.shopping.domain.model;

/**
 * Modelo de lectura de Cliente tal como lo expone customer-service.
 */
public record Customer(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}
