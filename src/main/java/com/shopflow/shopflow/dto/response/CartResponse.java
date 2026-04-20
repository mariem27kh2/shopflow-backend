package com.shopflow.shopflow.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class CartResponse {
    private Long id;
    private List<CartItemResponse> lignes;
    private Double sousTotal;
    private Double fraisLivraison;
    private Double totalTTC;
    private String codeCoupon;
    private Double remise;

    @Data
    public static class CartItemResponse {
        private Long id;
        private Long productId;
        private String productNom;
        private Double productPrix;
        private String productImage;
        private Integer quantite;
        private Double sousTotal;
    }
}
