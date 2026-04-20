package com.shopflow.shopflow.service;

import com.shopflow.shopflow.dto.response.DashboardResponse;
import com.shopflow.shopflow.entity.*;
import com.shopflow.shopflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final ProductService productService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public DashboardResponse getAdminDashboard() {
        DashboardResponse response = new DashboardResponse();

        // Chiffre d'affaires global
        double ca = orderRepository.findAll().stream()
                .filter(o -> o.getStatut() != OrderStatus.CANCELLED)
                .mapToDouble(Order::getTotalTTC)
                .sum();
        response.setChiffreAffairesGlobal(ca);
        response.setTotalCommandes((long) orderRepository.findAll().size());
        response.setTotalUtilisateurs(userRepository.count());
        response.setTotalProduits(productRepository.count());

        // Top produits
        response.setTopProduits(productService.getTopSellingProducts());

        return response;
    }

    public DashboardResponse getSellerDashboard() {
        User seller = getCurrentUser();
        DashboardResponse response = new DashboardResponse();

        // Revenus du vendeur
        double revenus = orderRepository.findAll().stream()
                .flatMap(o -> o.getLignes().stream())
                .filter(item -> item.getProduct().getSeller()
                        .getId().equals(seller.getId()))
                .mapToDouble(item -> item.getPrixUnitaire()
                        * item.getQuantite())
                .sum();
        response.setRevenus(revenus);

        // Commandes en attente
        long commandesEnAttente = orderRepository
                .findByStatut(OrderStatus.PENDING).size();
        response.setCommandesEnAttente(commandesEnAttente);

        // Produits avec stock faible (< 5)
        long stockFaible = productRepository.findAll().stream()
                .filter(p -> p.getSeller().getId().equals(seller.getId())
                        && p.getStock() < 5)
                .count();
        response.setProduitsStockFaible(stockFaible);

        return response;
    }

    public DashboardResponse getCustomerDashboard() {
        User customer = getCurrentUser();
        DashboardResponse response = new DashboardResponse();

        long commandesEnCours = orderRepository
                .findByCustomerId(customer.getId(),
                        PageRequest.of(0, 1000))
                .stream()
                .filter(o -> o.getStatut() != OrderStatus.DELIVERED
                        && o.getStatut() != OrderStatus.CANCELLED)
                .count();
        response.setMesCommandesEnCours(commandesEnCours);

        long mesAvis = reviewRepository.findAll().stream()
                .filter(r -> r.getCustomer().getId()
                        .equals(customer.getId()))
                .count();
        response.setMesAvis(mesAvis);

        return response;
    }
}