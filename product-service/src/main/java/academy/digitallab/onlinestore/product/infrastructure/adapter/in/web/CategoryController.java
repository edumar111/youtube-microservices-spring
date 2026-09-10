package academy.digitallab.onlinestore.product.infrastructure.adapter.in.web;

import academy.digitallab.onlinestore.product.domain.model.Category;
import academy.digitallab.onlinestore.product.domain.port.in.CategoryUseCase;
import academy.digitallab.onlinestore.product.infrastructure.adapter.in.web.dto.CategoryDtos.CategoryRequest;
import academy.digitallab.onlinestore.product.infrastructure.adapter.in.web.dto.CategoryDtos.CategoryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Adaptador de entrada REST para categorías.
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryUseCase categoryUseCase;
    private final WebMapper mapper;

    public CategoryController(CategoryUseCase categoryUseCase, WebMapper mapper) {
        this.categoryUseCase = categoryUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public List<CategoryResponse> listAll() {
        return categoryUseCase.findAll().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public CategoryResponse getById(@PathVariable Long id) {
        return mapper.toResponse(categoryUseCase.findById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        Category created = categoryUseCase.create(new Category(null, request.name()));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return mapper.toResponse(categoryUseCase.update(id, new Category(id, request.name())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
