package com.shopflow.shopflow.controller;

import com.shopflow.shopflow.dto.request.LoginRequest;
import com.shopflow.shopflow.dto.request.RegisterRequest;
import com.shopflow.shopflow.dto.response.AuthResponse;
import com.shopflow.shopflow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // indique que cette classe est un contrôleur REST, capable de gérer les requêtes HTTP et de retourner des réponses JSON
@RequestMapping("/api/auth") 
@RequiredArgsConstructor // génère un constructeur avec tous les champs finaux, ce qui permet d'injecter AuthService via le constructeur
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register( 
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) { 
        return ResponseEntity.ok(authService.login(request));
    }
}