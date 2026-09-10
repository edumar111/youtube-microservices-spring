package academy.digitallab.onlinestore.product.infrastructure.adapter.in.web.dto;

import academy.digitallab.onlinestore.product.infrastructure.adapter.in.web.dto.CategoryDtos.CategoryResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

/**
 * DTOs de producto para la API REST.
 */
public final class ProductDtos {

    private ProductDtos() {
    }

    public record ProductRequest(
            @NotBlank String name,
            String description,
            @NotNull @PositiveOrZero Double stock,
            @NotNull @PositiveOrZero Double price,
            @NotNull Long categoryId) {
    }

    public record ProductUpdateRequest(
            String name,
            String description,
            Double price) {
    }

    public record ProductResponse(
            Long id,
            String name,
            String description,
            Double stock,
            Double price,
            String status,
            LocalDate createdAt,
            CategoryResponse category) {
    }
}
