package com.minimarket.service.impl;

import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    @DisplayName("Lista todos los usuarios")
    void listaTodosLosUsuarios() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin");

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> resultado = usuarioService.findAll();

        assertEquals(1, resultado.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Busca usuario por id")
    void buscaUsuarioPorId() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Busca usuario por username")
    void buscaUsuarioPorUsername() {
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.findByUsername("admin");

        assertTrue(resultado.isPresent());
        assertEquals("admin", resultado.get().getUsername());
        verify(usuarioRepository, times(1)).findByUsername("admin");
    }

    @Test
    @DisplayName("Guarda usuario con contraseña encriptada")
    void guardaUsuarioConContrasenaEncriptada() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin");
        usuario.setPassword("123456");

        when(passwordEncoder.encode("123456")).thenReturn("password_encriptada");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioService.save(usuario);

        assertNotNull(resultado);
        assertEquals("password_encriptada", resultado.getPassword());
        verify(passwordEncoder, times(1)).encode("123456");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Elimina usuario por id")
    void eliminaUsuarioPorId() {
        usuarioService.deleteById(1L);

        verify(usuarioRepository, times(1)).deleteById(1L);
    }
}