package com.shopflow.shopflow.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
// configuration de base pour OpenAPI/Swagger, avec ajout de la sécurité JWT dans la documentation
    @Bean // permet de personnaliser la documentation OpenAPI
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; 

        return new OpenAPI()
                .info(new Info()
                        .title("ShopFlow API")
                        .version("1.0")
                        .description("Documentation API de ShopFlow"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) // indique que toutes les opérations nécessitent ce schéma de sécurité
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}