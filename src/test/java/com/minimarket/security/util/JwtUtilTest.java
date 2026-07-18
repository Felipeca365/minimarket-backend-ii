package com.minimarket.security.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Como es un test unitario puro (sin levantar el contexto de Spring),
        // los campos @Value se setean manualmente con ReflectionTestUtils.
        ReflectionTestUtils.setField(
                jwtUtil,
                "secret",
                "claveDePruebaParaJwtMinimarketPlus2026SuperSeguraTest"
        );
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 3600000L);
    }

    private UserDetails usuarioDePrueba() {
        return User.withUsername("admin")
                .password("encoded")
                .authorities(List.of(new SimpleGrantedAuthority("ADMIN")))
                .build();
    }

    @Test
    @DisplayName("Genera un token JWT no nulo y con contenido")
    void generaTokenNoNulo() {
        String token = jwtUtil.generateToken(usuarioDePrueba());

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("Extrae correctamente el username del token")
    void extraeUsernameDelToken() {
        String token = jwtUtil.generateToken(usuarioDePrueba());

        assertEquals("admin", jwtUtil.extractUsername(token));
    }

    @Test
    @DisplayName("Extrae correctamente los roles del token")
    void extraeRolesDelToken() {
        String token = jwtUtil.generateToken(usuarioDePrueba());

        List<String> roles = jwtUtil.extractRoles(token);

        assertTrue(roles.contains("ADMIN"));
    }

    @Test
    @DisplayName("El token generado es válido para el usuario correspondiente")
    void tokenEsValidoParaUsuarioCorrespondiente() {
        UserDetails userDetails = usuarioDePrueba();
        String token = jwtUtil.generateToken(userDetails);

        assertTrue(jwtUtil.isTokenValid(token, userDetails));
    }

    @Test
    @DisplayName("El token generado no está expirado inmediatamente después de crearlo")
    void tokenNoExpiradoAlGenerarlo() {
        String token = jwtUtil.generateToken(usuarioDePrueba());

        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    @DisplayName("Un token con firma manipulada lanza excepción al validarlo")
    void tokenConFirmaInvalidaLanzaExcepcion() {
        String tokenManipulado = jwtUtil.generateToken(usuarioDePrueba()) + "manipulado";

        assertThrows(Exception.class, () -> jwtUtil.extractUsername(tokenManipulado));
    }
}
