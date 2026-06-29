package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.entity.Usuario;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@Import(SecurityConfig.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Administrador puede listar usuarios")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void administradorPuedeListarUsuarios() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin");

        when(usuarioService.findAll()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("admin"));

        verify(usuarioService, times(1)).findAll();
    }

    @Test
    @DisplayName("Cliente no puede listar usuarios")
    @WithMockUser(username = "cliente", authorities = {"CLIENTE"})
    void clienteNoPuedeListarUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isForbidden());

        verify(usuarioService, never()).findAll();
    }

    @Test
    @DisplayName("Usuario sin autenticación no puede listar usuarios")
    void usuarioSinAutenticacionNoPuedeListarUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());

        verify(usuarioService, never()).findAll();
    }

    @Test
    @DisplayName("Administrador puede obtener usuario por id")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void administradorPuedeObtenerUsuarioPorId() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin");

        when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("admin"));

        verify(usuarioService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Administrador recibe 404 al buscar usuario inexistente")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void administradorRecibe404AlBuscarUsuarioInexistente() throws Exception {
        when(usuarioService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound());

        verify(usuarioService, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Registro de usuario por POST es público")
    void registroUsuarioEsPublico() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("nuevo_usuario");
        usuario.setPassword("123456");

        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("nuevo_usuario"));

        verify(usuarioService, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Administrador puede actualizar usuario existente")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void administradorPuedeActualizarUsuarioExistente() throws Exception {
        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setUsername("usuario_antiguo");

        Usuario actualizado = new Usuario();
        actualizado.setId(1L);
        actualizado.setUsername("usuario_actualizado");
        actualizado.setPassword("123456");

        when(usuarioService.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioService.save(any(Usuario.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("usuario_actualizado"));

        verify(usuarioService, times(1)).findById(1L);
        verify(usuarioService, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Administrador recibe 404 al actualizar usuario inexistente")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void administradorRecibe404AlActualizarUsuarioInexistente() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("no_existe");
        usuario.setPassword("123456");

        when(usuarioService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/usuarios/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isNotFound());

        verify(usuarioService, times(1)).findById(99L);
        verify(usuarioService, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Cliente no puede actualizar usuarios")
    @WithMockUser(username = "cliente", authorities = {"CLIENTE"})
    void clienteNoPuedeActualizarUsuarios() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("cliente");
        usuario.setPassword("123456");

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isForbidden());

        verify(usuarioService, never()).findById(anyLong());
        verify(usuarioService, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Administrador puede eliminar usuario existente")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void administradorPuedeEliminarUsuarioExistente() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("usuario");

        when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).findById(1L);
        verify(usuarioService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Cliente no puede eliminar usuarios")
    @WithMockUser(username = "cliente", authorities = {"CLIENTE"})
    void clienteNoPuedeEliminarUsuarios() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isForbidden());

        verify(usuarioService, never()).findById(anyLong());
        verify(usuarioService, never()).deleteById(anyLong());
    }
}