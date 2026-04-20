package com.shopflow.shopflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderRequest {

    @NotBlank(message = "Adresse de livraison obligatoire")
    private String adresseLivraison;
}