package com.minimarket.security.service;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Autenticación válida carga usuario existente")
    void autenticacionValidaCargaUsuarioExistente() {
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ADMIN");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin");
        usuario.setPassword("123456");
        usuario.setRoles(Set.of(rol));

        when(usuarioRepository.findByUsername("admin"))
                .thenReturn(Optional.of(usuario));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("123456", userDetails.getPassword());
        assertFalse(userDetails.getAuthorities().isEmpty());

        verify(usuarioRepository, times(1)).findByUsername("admin");
    }

    @Test
    @DisplayName("Autenticación inválida lanza excepción si el usuario no existe")
    void autenticacionInvalidaLanzaExcepcionSiUsuarioNoExiste() {
        when(usuarioRepository.findByUsername("desconocido"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("desconocido")
        );

        verify(usuarioRepository, times(1)).findByUsername("desconocido");
    }
}