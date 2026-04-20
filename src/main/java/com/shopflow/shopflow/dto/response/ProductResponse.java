package com.shopflow.shopflow.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private String nom;
    private String description;
    private Double prix;
    private Double prixPromo;
    private Integer stock;
    private boolean actif;
    private LocalDateTime dateCreation;
    private String sellerNom;
    private Long sellerId;
    private List<String> categories;
    private List<String> images;
    private Double noteMoyenne;
    private Integer nombreAvis;

    // Calcul du pourcentage de remise
    public Integer getPourcentageRemise() {
        if (prixPromo != null && prix > 0) {
            return (int) ((prix - prixPromo) / prix * 100);
        }
        return null;
    }
}