package academy.digitallab.onlinestore.product.domain.service;

import academy.digitallab.onlinestore.product.domain.exception.NotFoundException;
import academy.digitallab.onlinestore.product.domain.model.Category;
import academy.digitallab.onlinestore.product.domain.port.in.CategoryUseCase;
import academy.digitallab.onlinestore.product.domain.port.out.CategoryRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de los casos de uso de categoría.
 */
@Service
public class CategoryService implements CategoryUseCase {

    private final CategoryRepositoryPort categoryRepository;

    public CategoryService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
    }

    @Override
    @Transactional
    public Category create(Category category) {
        return categoryRepository.save(new Category(null, category.name()));
    }

    @Override
    @Transactional
    public Category update(Long id, Category changes) {
        Category current = findById(id);
        return categoryRepository.save(new Category(current.id(), changes.name()));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findById(id);
        categoryRepository.deleteById(id);
    }
}
