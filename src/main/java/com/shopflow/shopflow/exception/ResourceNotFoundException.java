package com.shopflow.shopflow.exception;
// Classe d'exception personnalisée pour gérer les cas où une ressource demandée (comme un produit, une catégorie, un utilisateur, etc.) n'est pas trouvée dans la base de données, permettant de fournir des messages d'erreur clairs et pertinents aux clients et aux développeurs
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}