package com.minimarket.security.config;

import com.minimarket.security.filter.JwtAuthenticationFilter;
import com.minimarket.security.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // Deshabilita CSRF para facilitar pruebas con Postman y Swagger
            .csrf(csrf -> csrf.disable())

            // Usa el servicio personalizado para cargar usuarios y roles
            .userDetailsService(customUserDetailsService)

            // JWT es stateless: el servidor no guarda sesión, cada
            // petición se autentica de nuevo a partir del token.
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Reglas de autorización
            .authorizeHttpRequests(auth -> auth

                // Rutas públicas generales
                .requestMatchers(
                    "/",
                    "/error",
                    "/public/**"
                ).permitAll()

                // Login: genera el token, debe ser accesible sin autenticación
                .requestMatchers("/api/auth/**").permitAll()

                // Rutas públicas de OpenAPI y Swagger UI
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()

                // Usuarios: permitir registro o creación por POST
                .requestMatchers(HttpMethod.POST, "/api/usuarios/**")
                    .permitAll()

                // Gestión de usuarios: solo administrador
                .requestMatchers(HttpMethod.GET, "/api/usuarios/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/usuarios/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ADMIN")

                // Productos: cualquier usuario autenticado puede consultar
                .requestMatchers(HttpMethod.GET, "/api/productos/**")
                    .authenticated()

                // Productos: solo administrador puede crear, modificar o eliminar
                .requestMatchers(HttpMethod.POST, "/api/productos/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/productos/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/productos/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ADMIN")

                // Categorías: consulta autenticada
                .requestMatchers(HttpMethod.GET, "/api/categorias/**")
                    .authenticated()

                // Categorías: modificación solo administrador
                .requestMatchers(HttpMethod.POST, "/api/categorias/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/categorias/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/categorias/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ADMIN")

                // Inventario: administrador y cajero
                .requestMatchers("/api/inventario/**")
                    .hasAnyAuthority(
                        "ROLE_ADMIN", "ADMIN",
                        "ROLE_CAJERO", "CAJERO"
                    )

                // Ventas: solo cajero puede generar ventas
                .requestMatchers(HttpMethod.POST, "/api/ventas/**")
                    .hasAnyAuthority("ROLE_CAJERO", "CAJERO")

                // Ventas: administrador y cajero pueden consultar
                .requestMatchers(HttpMethod.GET, "/api/ventas/**")
                    .hasAnyAuthority(
                        "ROLE_ADMIN", "ADMIN",
                        "ROLE_CAJERO", "CAJERO"
                    )

                // Detalles de venta: administrador y cajero
                .requestMatchers(
                    "/api/detalle-ventas/**",
                    "/api/detalleventas/**"
                )
                    .hasAnyAuthority(
                        "ROLE_ADMIN", "ADMIN",
                        "ROLE_CAJERO", "CAJERO"
                    )

                // Carrito: cliente, cajero o administrador
                .requestMatchers("/api/carrito/**")
                    .hasAnyAuthority(
                        "ROLE_CLIENTE", "CLIENTE",
                        "ROLE_CAJERO", "CAJERO",
                        "ROLE_ADMIN", "ADMIN"
                    )

                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )

            // Inserta el filtro JWT antes del filtro estándar de
            // usuario/contraseña, para que el token se procese primero
            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class)

            // Respuestas claras:
            // 401 = usuario no autenticado
            // 403 = usuario autenticado sin permisos
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "No autenticado"
                    )
                )
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Acceso denegado"
                    )
                )
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {

        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
