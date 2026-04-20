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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email déjà utilisé !");
        }

        // Déterminer le rôle
        Role role = "SELLER".equalsIgnoreCase(request.getRole())
                ? Role.SELLER : Role.CUSTOMER;

        // Créer l'utilisateur
        User user = User.builder()
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .prenom(request.getPrenom())
                .nom(request.getNom())
                .role(role)
                .actif(true)
                .build();

        userRepository.save(user);

        // Si vendeur, créer le profil boutique
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

        // Générer les tokens
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
        // Spring Security vérifie email + mot de passe
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