package academy.digitallab.onlinestore.shopping.domain.model;

/**
 * Modelo de lectura de Producto tal como lo expone product-service.
 * Es un DTO del dominio de shopping, no la entidad de otro servicio.
 */
public record Product(
        Long id,
        String name,
        String description,
        Double price,
        Double stock
) {
}
