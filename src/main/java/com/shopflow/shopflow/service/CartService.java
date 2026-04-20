package com.shopflow.shopflow.service;

import com.shopflow.shopflow.dto.request.CartItemRequest;
import com.shopflow.shopflow.dto.response.CartResponse;
import com.shopflow.shopflow.entity.*;
import com.shopflow.shopflow.exception.BusinessException;
import com.shopflow.shopflow.exception.ResourceNotFoundException;
import com.shopflow.shopflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));
    }

    // Récupérer ou créer le panier
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByCustomerId(user.getId())
                .orElseGet(() -> {
                    Cart cart = Cart.builder()
                            .customer(user)
                            .lignes(new ArrayList<>())
                            .build();
                    return cartRepository.save(cart);
                });
    }

    // Convertir panier en DTO
    private CartResponse toResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setCodeCoupon(cart.getCodeCoupon());

        List<CartResponse.CartItemResponse> lignes = cart.getLignes()
                .stream().map(item -> {
                    CartResponse.CartItemResponse r =
                            new CartResponse.CartItemResponse();
                    r.setId(item.getId());
                    r.setProductId(item.getProduct().getId());
                    r.setProductNom(item.getProduct().getNom());
                    r.setProductPrix(item.getProduct().getPrix());
                    r.setQuantite(item.getQuantite());
                    r.setSousTotal(item.getProduct().getPrix()
                            * item.getQuantite());
                    if (item.getProduct().getImages() != null
                            && !item.getProduct().getImages().isEmpty()) {
                        r.setProductImage(
                                item.getProduct().getImages().get(0));
                    }
                    return r;
                }).collect(Collectors.toList());

        response.setLignes(lignes);

        // Calcul sous-total
        double sousTotal = lignes.stream()
                .mapToDouble(CartResponse.CartItemResponse::getSousTotal)
                .sum();
        response.setSousTotal(sousTotal);

        // Frais de livraison
        double fraisLivraison = sousTotal > 100 ? 0.0 : 7.0;
        response.setFraisLivraison(fraisLivraison);

        // Appliquer coupon
        double remise = 0.0;
        if (cart.getCodeCoupon() != null) {
            remise = calculerRemise(cart.getCodeCoupon(), sousTotal);
        }
        response.setRemise(remise);
        response.setTotalTTC(sousTotal + fraisLivraison - remise);

        return response;
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

    public CartResponse getCart() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(CartItemRequest request) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        if (product.getStock() < request.getQuantite()) {
            throw new BusinessException("Stock insuffisant !");
        }

        // Vérifier si le produit est déjà dans le panier
        cart.getLignes().stream()
                .filter(i -> i.getProduct().getId()
                        .equals(request.getProductId()))
                .findFirst()
                .ifPresentOrElse(
                        item -> {
                            item.setQuantite(item.getQuantite() + request.getQuantite());
                            cartItemRepository.save(item);
                        },
                        () -> {
                            CartItem item = CartItem.builder()
                                    .cart(cart)
                                    .product(product)
                                    .quantite(request.getQuantite())
                                    .build();
                            cart.getLignes().add(item);
                            cartItemRepository.save(item);
                        });

        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(Long itemId, Integer quantite) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Article non trouvé"));

        if (item.getProduct().getStock() < quantite) {
            throw new BusinessException("Stock insuffisant !");
        }

        item.setQuantite(quantite);
        cartItemRepository.save(item);

        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(Long itemId) {
        cartItemRepository.deleteById(itemId);
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse applyCoupon(String code) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        couponRepository.findByCodeAndActifTrue(code)
                .orElseThrow(() -> new BusinessException(
                        "Code promo invalide ou expiré !"));

        cart.setCodeCoupon(code);
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeCoupon() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        cart.setCodeCoupon(null);
        cartRepository.save(cart);
        return toResponse(cart);
    }
}