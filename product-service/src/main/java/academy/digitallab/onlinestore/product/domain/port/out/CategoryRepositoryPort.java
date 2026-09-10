package academy.digitallab.onlinestore.product.domain.port.out;

import academy.digitallab.onlinestore.product.domain.model.Category;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: persistencia de categorías.
 */
public interface CategoryRepositoryPort {

    List<Category> findAll();

    Optional<Category> findById(Long id);

    Category save(Category category);

    void deleteById(Long id);
}
