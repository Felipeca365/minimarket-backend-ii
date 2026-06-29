package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.entity.Inventario;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.service.InventarioService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventarioController.class)
@Import(SecurityConfig.class)
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InventarioService inventarioService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Administrador puede listar movimientos de inventario")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void administradorPuedeListarInventario() throws Exception {
        when(inventarioService.findAll()).thenReturn(List.of(new Inventario()));

        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk());

        verify(inventarioService, times(1)).findAll();
    }

    @Test
    @DisplayName("Cajero puede listar movimientos de inventario")
    @WithMockUser(username = "cajero", authorities = {"CAJERO"})
    void cajeroPuedeListarInventario() throws Exception {
        when(inventarioService.findAll()).thenReturn(List.of(new Inventario()));

        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk());

        verify(inventarioService, times(1)).findAll();
    }

    @Test
    @DisplayName("Cliente no puede listar movimientos de inventario")
    @WithMockUser(username = "cliente", authorities = {"CLIENTE"})
    void clienteNoPuedeListarInventario() throws Exception {
        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isForbidden());

        verify(inventarioService, never()).findAll();
    }

    @Test
    @DisplayName("Usuario sin autenticación no puede acceder a inventario")
    void usuarioSinAutenticacionNoPuedeAccederInventario() throws Exception {
        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isUnauthorized());

        verify(inventarioService, never()).findAll();
    }

    @Test
    @DisplayName("Cajero puede registrar movimiento de inventario")
    @WithMockUser(username = "cajero", authorities = {"CAJERO"})
    void cajeroPuedeRegistrarMovimientoInventario() throws Exception {
        Inventario inventario = new Inventario();
        inventario.setId(1L);

        when(inventarioService.save(any(Inventario.class))).thenReturn(inventario);

        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(inventarioService, times(1)).save(any(Inventario.class));
    }

    @Test
    @DisplayName("Cliente no puede registrar movimiento de inventario")
    @WithMockUser(username = "cliente", authorities = {"CLIENTE"})
    void clienteNoPuedeRegistrarMovimientoInventario() throws Exception {
        Inventario inventario = new Inventario();

        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isForbidden());

        verify(inventarioService, never()).save(any(Inventario.class));
    }

    @Test
    @DisplayName("Administrador puede actualizar movimiento existente")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void administradorPuedeActualizarMovimientoExistente() throws Exception {
        Inventario existente = new Inventario();
        existente.setId(1L);

        Inventario actualizado = new Inventario();
        actualizado.setId(1L);

        when(inventarioService.findById(1L)).thenReturn(existente);
        when(inventarioService.save(any(Inventario.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/inventario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(inventarioService, times(1)).findById(1L);
        verify(inventarioService, times(1)).save(any(Inventario.class));
    }

    @Test
    @DisplayName("Administrador recibe 404 al actualizar movimiento inexistente")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void administradorRecibe404AlActualizarMovimientoInexistente() throws Exception {
        Inventario inventario = new Inventario();

        when(inventarioService.findById(99L)).thenReturn(null);

        mockMvc.perform(put("/api/inventario/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isNotFound());

        verify(inventarioService, times(1)).findById(99L);
        verify(inventarioService, never()).save(any(Inventario.class));
    }

    @Test
    @DisplayName("Administrador puede eliminar movimiento existente")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void administradorPuedeEliminarMovimientoExistente() throws Exception {
        Inventario inventario = new Inventario();
        inventario.setId(1L);

        when(inventarioService.findById(1L)).thenReturn(inventario);

        mockMvc.perform(delete("/api/inventario/1"))
                .andExpect(status().isNoContent());

        verify(inventarioService, times(1)).findById(1L);
        verify(inventarioService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Cliente no puede eliminar movimiento de inventario")
    @WithMockUser(username = "cliente", authorities = {"CLIENTE"})
    void clienteNoPuedeEliminarMovimientoInventario() throws Exception {
        mockMvc.perform(delete("/api/inventario/1"))
                .andExpect(status().isForbidden());

        verify(inventarioService, never()).findById(anyLong());
        verify(inventarioService, never()).deleteById(anyLong());
    }
}