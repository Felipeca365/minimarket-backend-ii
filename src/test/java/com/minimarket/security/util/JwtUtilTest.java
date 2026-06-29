package com.minimarket.security.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtUtilTest {

    @Test
    @DisplayName("Se puede crear instancia de JwtUtil")
    void sePuedeCrearInstanciaDeJwtUtil() {
        JwtUtil jwtUtil = new JwtUtil();

        assertNotNull(jwtUtil);
    }
}