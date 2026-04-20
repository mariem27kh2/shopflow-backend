package com.shopflow.shopflow.repository;

import com.shopflow.shopflow.entity.Order;
import com.shopflow.shopflow.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Commandes d'un client
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    // Commandes par statut
    List<Order> findByStatut(OrderStatus statut);

    // Trouver par numéro de commande
    Optional<Order> findByNumeroCommande(String numeroCommande);

    // Commandes d'un vendeur
    @org.springframework.data.jpa.repository.Query(
        "SELECT DISTINCT o FROM Order o JOIN o.lignes li WHERE li.product.seller.id = :sellerId")
    Page<Order> findBySellerIdInItems(Long sellerId, Pageable pageable);
}