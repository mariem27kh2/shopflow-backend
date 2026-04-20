package com.shopflow.shopflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.List;

@Data
public class ProductRequest {

    @NotBlank(message = "Nom obligatoire")
    private String nom;

    private String description;

    @NotNull(message = "Prix obligatoire")
    @Positive(message = "Prix doit être positif")
    private Double prix;

    private Double prixPromo;

    @NotNull
    private Integer stock;

    private List<Long> categorieIds;
    private List<String> images;
}