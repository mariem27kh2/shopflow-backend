package com.shopflow.shopflow.config;

import com.shopflow.shopflow.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //authorization par role dans controlleur et service 
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter; // verifie token dans chaque requete

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { //coeur de la configration sécuritee
        http
            // ✅ CORS ajouté ici
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) // désactive CSRF pour les API REST
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(
                    "/api/auth/**",
                    "/api/products/**",
                    "/api/categories/**",
                    "/api/reviews/product/**",
                    "/h2-console/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .headers(headers ->
                headers.frameOptions(frame -> frame.disable()))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //  Bean CORS ajouté
    //cors autorise le frontend angular a acceder a l'api spring boot sans probleme de politique de meme origine
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://localhost:4200",
            "https://shopflow-81cdd.web.app",
            "https://shopflow-81cdd.firebaseapp.com"
        )); // autorise le frontend Angular
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager( // permet d'injecter AuthenticationManager dans les services
            AuthenticationConfiguration config) throws Exception { // permet d'obtenir l'AuthenticationManager à partir de la configuration d'authentification
        return config.getAuthenticationManager();
    }
}