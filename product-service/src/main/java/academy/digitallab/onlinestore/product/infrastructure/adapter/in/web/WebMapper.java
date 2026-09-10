package academy.digitallab.onlinestore.product.infrastructure.adapter.in.web;

import academy.digitallab.onlinestore.product.domain.model.Category;
import academy.digitallab.onlinestore.product.domain.model.Product;
import academy.digitallab.onlinestore.product.infrastructure.adapter.in.web.dto.CategoryDtos.CategoryResponse;
import academy.digitallab.onlinestore.product.infrastructure.adapter.in.web.dto.ProductDtos.ProductRequest;
import academy.digitallab.onlinestore.product.infrastructure.adapter.in.web.dto.ProductDtos.ProductResponse;
import academy.digitallab.onlinestore.product.infrastructure.adapter.in.web.dto.ProductDtos.ProductUpdateRequest;
import org.springframework.stereotype.Component;

/**
 * Traduce entre DTOs de la API y modelos de dominio.
 */
@Component
public class WebMapper {

    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryResponse(category.id(), category.name());
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.id(),
                product.name(),
                product.description(),
                product.stock(),
                product.price(),
                product.status(),
                product.createdAt(),
                toResponse(product.category()));
    }

    public Product toDomain(ProductRequest request) {
        return new Product(
                null,
                request.name(),
                request.description(),
                request.stock(),
                request.price(),
                null,
                null,
                new Category(request.categoryId(), null));
    }

    public Product toDomain(ProductUpdateRequest request) {
        return new Product(null, request.name(), request.description(), null, request.price(), null, null, null);
    }
}
