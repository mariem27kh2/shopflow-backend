package com.shopflow.shopflow.exception;
// Classe d'exception personnalisée pour gérer les erreurs métier spécifiques à l'application, permettant de fournir des messages d'erreur clairs et pertinents aux clients et aux développeurs
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}