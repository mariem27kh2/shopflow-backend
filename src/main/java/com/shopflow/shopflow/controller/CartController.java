package com.shopflow.shopflow.controller;

import com.shopflow.shopflow.dto.request.CartItemRequest;
import com.shopflow.shopflow.dto.response.CartResponse;
import com.shopflow.shopflow.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// Contrôleur REST pour gérer les opérations liées au panier d'achat, telles que l'ajout, la mise à jour et la suppression d'articles, ainsi que l'application de coupons de réduction
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(request));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable Long itemId, 
            @RequestParam Integer quantite) {
        return ResponseEntity.ok(cartService.updateItem(itemId, quantite));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(itemId));
    }

    @PostMapping("/coupon")
    public ResponseEntity<CartResponse> applyCoupon(
            @RequestParam String code) { 
        return ResponseEntity.ok(cartService.applyCoupon(code));
    }

    @DeleteMapping("/coupon")
    public ResponseEntity<CartResponse> removeCoupon() {
        return ResponseEntity.ok(cartService.removeCoupon());
    }
}