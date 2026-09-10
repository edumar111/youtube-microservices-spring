package academy.digitallab.onlinestore.product.domain.port.in;

import academy.digitallab.onlinestore.product.domain.model.Category;

import java.util.List;

/**
 * Puerto de entrada: casos de uso de categorías.
 */
public interface CategoryUseCase {

    List<Category> findAll();

    Category findById(Long id);

    Category create(Category category);

    Category update(Long id, Category category);

    void delete(Long id);
}
