package academy.digitallab.onlinestore.product.domain.model;

/**
 * Modelo de dominio puro de Categoría. Inmutable (record), sin dependencias de JPA ni Spring.
 */
public record Category(Long id, String name) {

    public Category withId(Long newId) {
        return new Category(newId, name);
    }
}
