package com.shopflow.shopflow.security;

import com.shopflow.shopflow.entity.User;
import com.shopflow.shopflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;
// Service de détails utilisateur personnalisé pour Spring Security, qui charge les informations de l'utilisateur à partir de la base de données en utilisant le UserRepository, et convertit les rôles de l'utilisateur en autorités Spring Security pour la gestion des accès et des autorisations dans l'application.
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));

        return new org.springframework.security.core.userdetails.User( // Spring Security UserDetails implementation
                user.getEmail(),
                user.getMotDePasse(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
