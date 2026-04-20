package com.shopflow.shopflow.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus statut = OrderStatus.PENDING;

    @Column(unique = true, nullable = false)
    private String numeroCommande;

    // Adresse de livraison (on sauvegarde les infos au moment de la commande)
    private String adresseLivraison;

    @Column(nullable = false)
    private Double sousTotal;

    @Column(nullable = false)
    private Double fraisLivraison;

    @Column(nullable = false)
    private Double totalTTC;

    private LocalDateTime dateCommande;

    private boolean isNew = true;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> lignes;

    @PrePersist
    public void prePersist() {
        this.dateCommande = LocalDateTime.now();
    }
}