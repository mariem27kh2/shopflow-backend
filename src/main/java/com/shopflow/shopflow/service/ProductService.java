package com.shopflow.shopflow.service;

import com.shopflow.shopflow.dto.request.ProductRequest;
import com.shopflow.shopflow.dto.response.ProductResponse;
import com.shopflow.shopflow.entity.*;
import com.shopflow.shopflow.exception.BusinessException;
import com.shopflow.shopflow.exception.ResourceNotFoundException;
import com.shopflow.shopflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;

    // Convertir entité → DTO
    private ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setNom(product.getNom());
        response.setDescription(product.getDescription());
        response.setPrix(product.getPrix());
        response.setPrixPromo(product.getPrixPromo());
        response.setStock(product.getStock());
        response.setActif(product.isActif());
        response.setDateCreation(product.getDateCreation());
        response.setSellerId(product.getSeller().getId());
        response.setSellerNom(product.getSeller().getPrenom()
                + " " + product.getSeller().getNom());

        if (product.getCategories() != null) {
            response.setCategories(product.getCategories()
                    .stream().map(Category::getNom)
                    .collect(Collectors.toList()));
        }

        if (product.getImages() != null) {
            response.setImages(product.getImages());
        }

        // Note moyenne
        Double note = reviewRepository
                .findAverageNoteByProductId(product.getId());
        response.setNoteMoyenne(note != null ? note : 0.0);

        return response;
    }

    // Récupérer l'utilisateur connecté
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));
    }

    // Liste paginée des produits
    public Page<ProductResponse> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("dateCreation").descending());
        return productRepository.findByActifTrue(pageable)
                .map(this::toResponse);
    }

    // Détail d'un produit
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit non trouvé avec l'id : " + id));
        return toResponse(product);
    }

    // Recherche
    public Page<ProductResponse> searchProducts(String q, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository
                .findByNomContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        q, q, pageable)
                .map(this::toResponse);
    }

    // Créer un produit
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        User seller = getCurrentUser();

        if (seller.getRole() != Role.SELLER && seller.getRole() != Role.ADMIN) {
            throw new BusinessException("Seuls les vendeurs peuvent créer des produits");
        }

        List<Category> categories = request.getCategorieIds() != null
                ? categoryRepository.findAllById(request.getCategorieIds())
                : List.of();

        Product product = Product.builder()
                .seller(seller)
                .nom(request.getNom())
                .description(request.getDescription())
                .prix(request.getPrix())
                .prixPromo(request.getPrixPromo())
                .stock(request.getStock())
                .actif(true)
                .categories(categories)
                .images(request.getImages())
                .build();

        return toResponse(productRepository.save(product));
    }

    // Modifier un produit
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit non trouvé : " + id));

        User currentUser = getCurrentUser();
        if (!product.getSeller().getId().equals(currentUser.getId())
                && currentUser.getRole() != Role.ADMIN) {
            throw new BusinessException("Vous n'êtes pas autorisé à modifier ce produit");
        }

        product.setNom(request.getNom());
        product.setDescription(request.getDescription());
        product.setPrix(request.getPrix());
        product.setPrixPromo(request.getPrixPromo());
        product.setStock(request.getStock());

        if (request.getCategorieIds() != null) {
            product.setCategories(
                    categoryRepository.findAllById(request.getCategorieIds()));
        }
        if (request.getImages() != null) {
            product.setImages(request.getImages());
        }

        return toResponse(productRepository.save(product));
    }

    // Désactiver un produit (soft delete)
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit non trouvé : " + id));

        User currentUser = getCurrentUser();
        if (!product.getSeller().getId().equals(currentUser.getId())
                && currentUser.getRole() != Role.ADMIN) {
            throw new BusinessException("Non autorisé");
        }

        product.setActif(false);
        productRepository.save(product);
    }

    // Top produits
    public List<ProductResponse> getTopSellingProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        return productRepository.findTopSellingProducts(pageable)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Produits en promo
    public Page<ProductResponse> getPromoProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByPrixPromoIsNotNullAndActifTrue(pageable)
                .map(this::toResponse);
    }
}