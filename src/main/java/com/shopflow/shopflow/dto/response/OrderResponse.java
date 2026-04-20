package com.shopflow.shopflow.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String numeroCommande;
    private String statut;
    private String adresseLivraison;
    private Double sousTotal;
    private Double fraisLivraison;
    private Double totalTTC;
    private LocalDateTime dateCommande;
    private List<OrderItemResponse> lignes;

    @Data
    public static class OrderItemResponse {
        private Long id;
        private String productNom;
        private Integer quantite;
        private Double prixUnitaire;
        private Double sousTotal;
    }
}