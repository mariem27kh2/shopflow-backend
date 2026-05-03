package com.shopflow.shopflow.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double prix;

    private Double prixPromo;

    @Column(nullable = false)
    private Integer stock = 0;

    private boolean actif = true;

    private LocalDateTime dateCreation;

    // Un produit appartient à plusieurs catégories
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    // Images du produit
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_images",
        joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url", columnDefinition = "TEXT")
    private List<String> images;

    // Variantes (tailles, couleurs...)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variantes;

    // Avis clients
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Review> avis;

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
    }
}