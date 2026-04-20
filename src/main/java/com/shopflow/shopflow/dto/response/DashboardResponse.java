package com.shopflow.shopflow.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class DashboardResponse {

    // ADMIN
    private Double chiffreAffairesGlobal;
    private Long totalCommandes;
    private Long totalUtilisateurs;
    private Long totalProduits;
    private List<ProductResponse> topProduits;

    // SELLER
    private Double revenus;
    private Long commandesEnAttente;
    private Long produitsStockFaible;

    // CUSTOMER
    private Long mesCommandesEnCours;
    private Long mesAvis;
}