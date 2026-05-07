package com.shopflow.shopflow.service;

import com.shopflow.shopflow.dto.request.LoginRequest;
import com.shopflow.shopflow.dto.request.RegisterRequest;
import com.shopflow.shopflow.dto.response.AuthResponse;
import com.shopflow.shopflow.entity.Role;
import com.shopflow.shopflow.entity.SellerProfile;
import com.shopflow.shopflow.entity.User;
import com.shopflow.shopflow.exception.BusinessException;
import com.shopflow.shopflow.repository.SellerProfileRepository;
import com.shopflow.shopflow.repository.UserRepository;
import com.shopflow.shopflow.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// Service d'authentification pour gérer les processus d'inscription et de connexion des utilisateurs, en vérifiant les informations d'identification, en générant des tokens JWT pour les sessions authentifiées, et en gérant les rôles et les profils des utilisateurs (notamment pour les vendeurs) dans la base de données.
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
// Méthode d'inscription pour créer un nouvel utilisateur, vérifier que l'email n'est pas déjà utilisé, encoder le mot de passe, attribuer un rôle (ADMIN, SELLER ou CUSTOMER), créer un profil de vendeur si nécessaire, et générer des tokens JWT pour la session authentifiée
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email déjà utilisé !");
        }

        //  ADMIN, SELLER, CUSTOMER tous acceptés
        Role role;
        if ("ADMIN".equalsIgnoreCase(request.getRole())) {
            role = Role.ADMIN;
        } else if ("SELLER".equalsIgnoreCase(request.getRole())) {
            role = Role.SELLER;
        } else {
            role = Role.CUSTOMER;
        }

        User user = User.builder()
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .prenom(request.getPrenom())
                .nom(request.getNom())
                .role(role)
                .actif(true)
                .build();

        userRepository.save(user);

        if (role == Role.SELLER) {
            SellerProfile profile = SellerProfile.builder()
                    .user(user)
                    .nomBoutique(request.getNomBoutique() != null
                            ? request.getNomBoutique() : "Ma Boutique")
                    .description(request.getDescriptionBoutique())
                    .note(0.0)
                    .build();
            sellerProfileRepository.save(profile);
        }

        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .prenom(user.getPrenom())
                .nom(user.getNom())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getMotDePasse()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));

        if (!user.isActif()) {
            throw new BusinessException("Compte désactivé !");
        }

        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .prenom(user.getPrenom())
                .nom(user.getNom())
                .role(user.getRole().name())
                .build();
    }
}