package academy.digitallab.onlinestore.product.domain.model;

import java.time.LocalDate;

/**
 * Modelo de dominio puro de Producto. Inmutable (record), sin JPA ni Spring.
 * Los cambios de estado producen una nueva instancia (métodos {@code with...}).
 */
public record Product(
        Long id,
        String name,
        String description,
        Double stock,
        Double price,
        String status,
        LocalDate createdAt,
        Category category
) {

    public Product withId(Long newId) {
        return new Product(newId, name, description, stock, price, status, createdAt, category);
    }

    public Product withStatus(String newStatus) {
        return new Product(id, name, description, stock, price, newStatus, createdAt, category);
    }

    public Product withStock(Double newStock) {
        return new Product(id, name, description, newStock, price, status, createdAt, category);
    }

    public Product withCreatedAt(LocalDate date) {
        return new Product(id, name, description, stock, price, status, date, category);
    }
}
