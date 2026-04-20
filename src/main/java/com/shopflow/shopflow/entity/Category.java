package com.shopflow.shopflow.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String description;

    // Sous-catégories : une catégorie peut avoir une catégorie parente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    // Les sous-catégories de cette catégorie
    @OneToMany(mappedBy = "parent")
    @ToString.Exclude
    private List<Category> sousCategories;
}