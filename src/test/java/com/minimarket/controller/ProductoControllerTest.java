package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.entity.Producto;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.service.ProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
@Import(SecurityConfig.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductoService productoService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Usuario autenticado puede listar productos")
    @WithMockUser(username = "cliente", authorities = {"CLIENTE"})
    void listarProductosConUsuarioAutenticado() throws Exception {
        when(productoService.findAll()).thenReturn(List.of(new Producto()));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());

        verify(productoService, times(1)).findAll();
    }

    @Test
    @DisplayName("Usuario sin autenticación no puede listar productos")
    void listarProductosSinAutenticacionDebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isUnauthorized());

        verify(productoService, never()).findAll();
    }

    @Test
    @DisplayName("Administrador puede crear producto")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void administradorPuedeCrearProducto() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);

        when(productoService.save(any(Producto.class))).thenReturn(producto);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(productoService, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Cliente no puede crear producto")
    @WithMockUser(username = "cliente", authorities = {"CLIENTE"})
    void clienteNoPuedeCrearProducto() throws Exception {
        Producto producto = new Producto();

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isForbidden());

        verify(productoService, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Administrador puede actualizar producto existente")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void administradorPuedeActualizarProducto() throws Exception {
        Producto productoExistente = new Producto();
        productoExistente.setId(1L);

        Producto productoActualizado = new Producto();
        productoActualizado.setId(1L);

        when(productoService.findById(1L)).thenReturn(productoExistente);
        when(productoService.save(any(Producto.class))).thenReturn(productoActualizado);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(productoService, times(1)).findById(1L);
        verify(productoService, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Cliente no puede actualizar producto")
    @WithMockUser(username = "cliente", authorities = {"CLIENTE"})
    void clienteNoPuedeActualizarProducto() throws Exception {
        Producto producto = new Producto();

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isForbidden());

        verify(productoService, never()).findById(anyLong());
        verify(productoService, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Administrador puede eliminar producto existente")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void administradorPuedeEliminarProducto() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);

        when(productoService.findById(1L)).thenReturn(producto);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());

        verify(productoService, times(1)).findById(1L);
        verify(productoService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Cliente no puede eliminar producto")
    @WithMockUser(username = "cliente", authorities = {"CLIENTE"})
    void clienteNoPuedeEliminarProducto() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isForbidden());

        verify(productoService, never()).findById(anyLong());
        verify(productoService, never()).deleteById(anyLong());
    }
}