package com.shopflow.shopflow.service;

import com.shopflow.shopflow.dto.response.CategoryResponse;
import com.shopflow.shopflow.entity.Category;
import com.shopflow.shopflow.exception.ResourceNotFoundException;
import com.shopflow.shopflow.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
// Service de gestion des catégories de produits, permettant de récupérer toutes les catégories, de créer une nouvelle catégorie (avec une catégorie parente optionnelle), de mettre à jour une catégorie existante, et de supprimer une catégorie. Les méthodes utilisent des transactions pour garantir l'intégrité des données lors des opérations de création, mise à jour et suppression, et convertissent les entités Category en DTO CategoryResponse pour la communication avec les contrôleurs et les clients de l'API.
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse createCategory(String nom, String description, Long parentId) {
        Category category = new Category();
        category.setNom(nom);
        category.setDescription(description);

        if (parentId != null) {
            Category parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Catégorie parente non trouvée"));
            category.setParent(parent);
        }

        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, String nom, String description) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));

        category.setNom(nom);
        category.setDescription(description);

        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
// Méthode utilitaire pour convertir une entité Category en DTO CategoryResponse, en incluant l'ID de la catégorie parente si elle existe
    private CategoryResponse toResponse(Category category) {
        Long parentId = category.getParent() != null ? category.getParent().getId() : null;

        return new CategoryResponse(
                category.getId(),
                category.getNom(),
                category.getDescription(),
                parentId
        );
    }
}