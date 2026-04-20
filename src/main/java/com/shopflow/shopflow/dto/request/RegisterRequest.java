package com.shopflow.shopflow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @Email(message = "Email invalide")
    @NotBlank(message = "Email obligatoire")
    private String email;

    @NotBlank(message = "Mot de passe obligatoire")
    @Size(min = 6, message = "Mot de passe minimum 6 caractères")
    private String motDePasse;

    @NotBlank(message = "Prénom obligatoire")
    private String prenom;

    @NotBlank(message = "Nom obligatoire")
    private String nom;

    // CUSTOMER ou SELLER
    private String role = "CUSTOMER";

    // Uniquement si role = SELLER
    private String nomBoutique;
    private String descriptionBoutique;
}