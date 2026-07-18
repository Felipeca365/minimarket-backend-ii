package com.minimarket.security.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI minimarketOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Minimarket Plus API")
                        .description(
                                "API REST para la gestión de productos, carritos de compra, "
                                + "inventario, ventas, usuarios y roles del sistema Minimarket Plus. "
                                + "Autenticación mediante JWT: primero llama a POST /api/auth/login "
                                + "con username y password, y usa el token retornado en el botón "
                                + "'Authorize' de esta página (Bearer <token>)."
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Felipe")
                                .email("felipe@minimarket.cl")
                        )
                )
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(SECURITY_SCHEME_NAME)
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME_NAME,
                                        new SecurityScheme()
                                                .name(SECURITY_SCHEME_NAME)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description(
                                                        "Autenticación JWT. Pegue aquí el token "
                                                        + "obtenido en POST /api/auth/login "
                                                        + "(sin la palabra 'Bearer', Swagger la agrega sola)."
                                                )
                                )
                );
    }
}
