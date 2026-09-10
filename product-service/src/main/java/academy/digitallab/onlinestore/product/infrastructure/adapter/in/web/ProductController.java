package academy.digitallab.onlinestore.product.infrastructure.adapter.in.web;

import academy.digitallab.onlinestore.product.domain.model.Product;
import academy.digitallab.onlinestore.product.domain.port.in.ProductUseCase;
import academy.digitallab.onlinestore.product.infrastructure.adapter.in.web.dto.ProductDtos.ProductRequest;
import academy.digitallab.onlinestore.product.infrastructure.adapter.in.web.dto.ProductDtos.ProductResponse;
import academy.digitallab.onlinestore.product.infrastructure.adapter.in.web.dto.ProductDtos.ProductUpdateRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Adaptador de entrada REST para productos.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductUseCase productUseCase;
    private final WebMapper mapper;

    public ProductController(ProductUseCase productUseCase, WebMapper mapper) {
        this.productUseCase = productUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public List<ProductResponse> listAll() {
        return productUseCase.findAll().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/category")
    public List<ProductResponse> listByCategory(@RequestParam Long id) {
        return productUseCase.findByCategory(id).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        return mapper.toResponse(productUseCase.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        Product created = productUseCase.create(mapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @RequestBody ProductUpdateRequest request) {
        return mapper.toResponse(productUseCase.update(id, mapper.toDomain(request)));
    }

    @DeleteMapping("/{id}")
    public ProductResponse delete(@PathVariable Long id) {
        return mapper.toResponse(productUseCase.delete(id));
    }

    @GetMapping("/{id}/stock")
    public ProductResponse updateStock(@PathVariable Long id, @RequestParam Double quantity) {
        return mapper.toResponse(productUseCase.updateStock(id, quantity));
    }
}
