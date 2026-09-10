package academy.digitallab.onlinestore.product.domain.port.in;

import academy.digitallab.onlinestore.product.domain.model.Product;

import java.util.List;

/**
 * Puerto de entrada: casos de uso del catálogo de productos.
 * Es la API del dominio que consumen los adaptadores de entrada (REST).
 */
public interface ProductUseCase {

    List<Product> findAll();

    Product findById(Long id);

    List<Product> findByCategory(Long categoryId);

    Product create(Product product);

    Product update(Long id, Product product);

    Product delete(Long id);

    Product updateStock(Long id, Double quantity);
}
