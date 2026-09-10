package academy.digitallab.onlinestore.product.domain.port.out;

import academy.digitallab.onlinestore.product.domain.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: persistencia de productos. El dominio depende de esta
 * abstracción; la implementación concreta (JPA/PostgreSQL) vive en infraestructura.
 */
public interface ProductRepositoryPort {

    List<Product> findAll();

    Optional<Product> findById(Long id);

    List<Product> findByCategoryId(Long categoryId);

    Product save(Product product);

    void deleteById(Long id);
}
