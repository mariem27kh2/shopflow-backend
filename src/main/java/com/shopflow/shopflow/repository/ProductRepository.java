package com.shopflow.shopflow.repository;

import com.shopflow.shopflow.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Recherche par nom ou description
    Page<Product> findByNomContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String nom, String description, Pageable pageable);

    // Produits d'un vendeur
    Page<Product> findBySellerId(Long sellerId, Pageable pageable);

    // Produits en promo
    Page<Product> findByPrixPromoIsNotNullAndActifTrue(Pageable pageable);

    // Produits actifs
    Page<Product> findByActifTrue(Pageable pageable);

    // Top 10 produits les plus vendus
    @Query("SELECT oi.product FROM OrderItem oi GROUP BY oi.product ORDER BY SUM(oi.quantite) DESC")
    List<Product> findTopSellingProducts(Pageable pageable);
}