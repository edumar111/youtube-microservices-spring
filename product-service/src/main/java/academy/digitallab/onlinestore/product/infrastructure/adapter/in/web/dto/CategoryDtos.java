package academy.digitallab.onlinestore.product.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTOs de categoría para la API REST. Desacoplan el contrato HTTP del modelo de dominio.
 */
public final class CategoryDtos {

    private CategoryDtos() {
    }

    public record CategoryRequest(@NotBlank String name) {
    }

    public record CategoryResponse(Long id, String name) {
    }
}
