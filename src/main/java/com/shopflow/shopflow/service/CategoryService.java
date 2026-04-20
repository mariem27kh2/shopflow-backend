package com.shopflow.shopflow.service;

import com.shopflow.shopflow.entity.Category;
import com.shopflow.shopflow.exception.ResourceNotFoundException;
import com.shopflow.shopflow.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findByParentIsNull();
    }

    @Transactional
    public Category createCategory(String nom, String description, Long parentId) {
        Category category = new Category();
        category.setNom(nom);
        category.setDescription(description);

        if (parentId != null) {
            Category parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Catégorie parente non trouvée"));
            category.setParent(parent);
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, String nom, String description) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Catégorie non trouvée"));
        category.setNom(nom);
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}