package academy.digitallab.onlinestore.product.domain.service;

import academy.digitallab.onlinestore.product.domain.exception.InsufficientStockException;
import academy.digitallab.onlinestore.product.domain.exception.NotFoundException;
import academy.digitallab.onlinestore.product.domain.model.Product;
import academy.digitallab.onlinestore.product.domain.port.in.ProductUseCase;
import academy.digitallab.onlinestore.product.domain.port.out.CategoryRepositoryPort;
import academy.digitallab.onlinestore.product.domain.port.out.ProductRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementación de los casos de uso de producto. Contiene la lógica de negocio
 * y depende solo de puertos de salida (inyección por constructor).
 */
@Service
public class ProductService implements ProductUseCase {

    private final ProductRepositoryPort productRepository;
    private final CategoryRepositoryPort categoryRepository;

    public ProductService(ProductRepositoryPort productRepository,
                          CategoryRepositoryPort categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    @Transactional
    public Product create(Product product) {
        if (product.category() == null || product.category().id() == null) {
            throw new NotFoundException("Category is required to create a product");
        }
        categoryRepository.findById(product.category().id())
                .orElseThrow(() -> new NotFoundException("Category not found: " + product.category().id()));

        Product toCreate = new Product(
                null,
                product.name(),
                product.description(),
                product.stock(),
                product.price(),
                "CREATED",
                LocalDate.now(),
                product.category());
        return productRepository.save(toCreate);
    }

    @Override
    @Transactional
    public Product update(Long id, Product changes) {
        Product current = findById(id);
        Product updated = new Product(
                current.id(),
                changes.name() != null ? changes.name() : current.name(),
                changes.description() != null ? changes.description() : current.description(),
                current.stock(),
                changes.price() != null ? changes.price() : current.price(),
                current.status(),
                current.createdAt(),
                current.category());
        return productRepository.save(updated);
    }

    @Override
    @Transactional
    public Product delete(Long id) {
        Product current = findById(id);
        return productRepository.save(current.withStatus("DELETED"));
    }

    @Override
    @Transactional
    public Product updateStock(Long id, Double quantity) {
        Product current = findById(id);
        double newStock = current.stock() - quantity;
        if (newStock < 0) {
            throw new InsufficientStockException(
                    "Insufficient stock for product " + id + ": have " + current.stock() + ", requested " + quantity);
        }
        return productRepository.save(current.withStock(newStock));
    }
}
