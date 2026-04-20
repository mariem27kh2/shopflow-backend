package com.shopflow.shopflow.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequest {

    @NotNull(message = "Produit obligatoire")
    private Long productId;

    private Long variantId;

    @Min(value = 1, message = "Quantité minimum 1")
    private Integer quantite = 1;
}