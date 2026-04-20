package com.shopflow.shopflow.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer note; // 1 à 5

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    private LocalDateTime dateCreation;

    private boolean approuve = false;

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
    }
}