package com.shopflow.shopflow.service;

import com.shopflow.shopflow.dto.request.OrderRequest;
import com.shopflow.shopflow.dto.response.OrderResponse;
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
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));
    }

    // Convertir commande en DTO
    private OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setNumeroCommande(order.getNumeroCommande());
        response.setStatut(order.getStatut().name());
        response.setAdresseLivraison(order.getAdresseLivraison());
        response.setSousTotal(order.getSousTotal());
        response.setFraisLivraison(order.getFraisLivraison());
        response.setTotalTTC(order.getTotalTTC());
        response.setDateCommande(order.getDateCommande());

        if (order.getLignes() != null) {
            List<OrderResponse.OrderItemResponse> lignes = order.getLignes()
                    .stream().map(item -> {
                        OrderResponse.OrderItemResponse r =
                                new OrderResponse.OrderItemResponse();
                        r.setId(item.getId());
                        r.setProductNom(item.getProduct().getNom());
                        r.setQuantite(item.getQuantite());
                        r.setPrixUnitaire(item.getPrixUnitaire());
                        r.setSousTotal(item.getPrixUnitaire()
                                * item.getQuantite());
                        return r;
                    }).collect(Collectors.toList());
            response.setLignes(lignes);
        }

        return response;
    }

    // Générer numéro de commande unique
    private String generateOrderNumber() {
        int random = new Random().nextInt(99999);
        return String.format("ORD-2024-%05d", random);
    }

    // Passer une commande depuis le panier
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User customer = getCurrentUser();

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new BusinessException("Panier vide !"));

        if (cart.getLignes().isEmpty()) {
            throw new BusinessException("Votre panier est vide !");
        }

        // Vérifier stock et calculer total
        double sousTotal = 0.0;
        for (CartItem item : cart.getLignes()) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantite()) {
                throw new BusinessException(
                        "Stock insuffisant pour : " + product.getNom());
            }
            sousTotal += product.getPrix() * item.getQuantite();
        }

        double fraisLivraison = sousTotal > 100 ? 0.0 : 7.0;

        // Appliquer coupon si présent
        double remise = 0.0;
        if (cart.getCodeCoupon() != null) {
            remise = calculerRemise(cart.getCodeCoupon(), sousTotal);
        }

        double totalTTC = sousTotal + fraisLivraison - remise;

        // Créer la commande
        Order order = Order.builder()
                .customer(customer)
                .numeroCommande(generateOrderNumber())
                .adresseLivraison(request.getAdresseLivraison())
                .statut(OrderStatus.PENDING)
                .sousTotal(sousTotal)
                .fraisLivraison(fraisLivraison)
                .totalTTC(totalTTC)
                .build();

        // Créer les lignes de commande + décrémenter le stock
        List<OrderItem> lignes = cart.getLignes().stream().map(item -> {
            // Décrémenter stock
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantite());
            productRepository.save(product);

            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantite(item.getQuantite())
                    .prixUnitaire(product.getPrix())
                    .build();
        }).collect(Collectors.toList());

        order.setLignes(lignes);
        orderRepository.save(order);

        // Vider le panier
        cart.getLignes().clear();
        cart.setCodeCoupon(null);
        cartRepository.save(cart);

        return toResponse(order);
    }

    private double calculerRemise(String code, double sousTotal) {
        return couponRepository.findByCodeAndActifTrue(code)
                .map(coupon -> {
                    if (coupon.getType() == CouponType.PERCENT) {
                        return sousTotal * coupon.getValeur() / 100;
                    } else {
                        return coupon.getValeur();
                    }
                }).orElse(0.0);
    }

    // Mes commandes
    public Page<OrderResponse> getMyOrders(int page, int size) {
        User customer = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("dateCommande").descending());
        return orderRepository.findByCustomerId(customer.getId(), pageable)
                .map(this::toResponse);
    }

    // Détail commande
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande non trouvée"));
        return toResponse(order);
    }

    // Toutes les commandes (ADMIN)
    public Page<OrderResponse> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("dateCommande").descending());
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    // Commandes contenant les produits du seller
    public Page<OrderResponse> getSellerOrders(int page, int size) {
        User seller = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("dateCommande").descending());
        return orderRepository.findBySellerIdInItems(seller.getId(), pageable)
                .map(order -> toSellerResponse(order, seller.getId()));
    }

    // DTO filtré pour le seller — seulement ses lignes
    private OrderResponse toSellerResponse(Order order, Long sellerId) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setNumeroCommande(order.getNumeroCommande());
        response.setStatut(order.getStatut().name());
        response.setAdresseLivraison(order.getAdresseLivraison());
        response.setDateCommande(order.getDateCommande());

        // Filtrer seulement les lignes du seller
        List<OrderResponse.OrderItemResponse> lignes = order.getLignes()
                .stream()
                .filter(item -> item.getProduct().getSeller().getId().equals(sellerId))
                .map(item -> {
                    OrderResponse.OrderItemResponse r = new OrderResponse.OrderItemResponse();
                    r.setId(item.getId());
                    r.setProductNom(item.getProduct().getNom());
                    r.setQuantite(item.getQuantite());
                    r.setPrixUnitaire(item.getPrixUnitaire());
                    r.setSousTotal(item.getPrixUnitaire() * item.getQuantite());
                    return r;
                }).collect(Collectors.toList());

        response.setLignes(lignes);

        // Recalculer le total pour les lignes du seller uniquement
        double sousTotal = lignes.stream()
                .mapToDouble(OrderResponse.OrderItemResponse::getSousTotal).sum();
        response.setSousTotal(sousTotal);
        response.setFraisLivraison(0.0);
        response.setTotalTTC(sousTotal);

        return response;
    }

    // Mettre à jour le statut
    @Transactional
    public OrderResponse updateStatus(Long id, String statut) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande non trouvée"));
        order.setStatut(OrderStatus.valueOf(statut));
        return toResponse(orderRepository.save(order));
    }

    // Annuler une commande
    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande non trouvée"));

        if (order.getStatut() != OrderStatus.PENDING
                && order.getStatut() != OrderStatus.PAID) {
            throw new BusinessException(
                    "Impossible d'annuler une commande " +
                    order.getStatut().name());
        }

        // Remettre le stock
        order.getLignes().forEach(item -> {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantite());
            productRepository.save(product);
        });

        order.setStatut(OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }
}