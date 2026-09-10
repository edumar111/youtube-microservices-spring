package academy.digitallab.onlinestore.product.infrastructure.adapter.out.persistence;

import academy.digitallab.onlinestore.product.domain.model.Category;
import academy.digitallab.onlinestore.product.domain.model.Product;
import academy.digitallab.onlinestore.product.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import academy.digitallab.onlinestore.product.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

/**
 * Traduce entre entidades JPA (infraestructura) y modelos de dominio.
 * Aísla el dominio de los detalles de persistencia.
 */
@Component
public class ProductPersistenceMapper {

    public Category toDomain(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Category(entity.getId(), entity.getName());
    }

    public CategoryEntity toEntity(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryEntity(category.id(), category.name());
    }

    public Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getStock(),
                entity.getPrice(),
                entity.getStatus(),
                entity.getCreateAt(),
                toDomain(entity.getCategory()));
    }

    public ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.id());
        entity.setName(product.name());
        entity.setDescription(product.description());
        entity.setStock(product.stock());
        entity.setPrice(product.price());
        entity.setStatus(product.status());
        entity.setCreateAt(product.createdAt());
        entity.setCategory(toEntity(product.category()));
        return entity;
    }
}
