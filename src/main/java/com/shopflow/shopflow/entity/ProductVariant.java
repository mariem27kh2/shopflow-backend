package com.shopflow.shopflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // ex: "Taille", "Couleur"
    @Column(nullable = false)
    private String attribut;

    // ex: "M", "Rouge"
    @Column(nullable = false)
    private String valeur;

    private Integer stockSupplementaire = 0;

    // Prix supplémentaire par rapport au prix de base
    private Double prixDelta = 0.0;
}