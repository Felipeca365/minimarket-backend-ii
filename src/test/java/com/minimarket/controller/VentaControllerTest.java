package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.entity.Venta;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.service.VentaService;
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

@WebMvcTest(VentaController.class)
@Import(SecurityConfig.class)
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VentaService ventaService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Administrador puede listar ventas")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void administradorPuedeListarVentas() throws Exception {

        Venta venta = new Venta();
        venta.setId(1L);

        when(ventaService.findAll()).thenReturn(List.of(venta));

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.ventaList[0].id").value(1));

        verify(ventaService, times(1)).findAll();
    }

    @Test
    @DisplayName("Cajero puede listar ventas")
    @WithMockUser(username = "cajero", authorities = {"CAJERO"})
    void cajeroPuedeListarVentas() throws Exception {

        Venta venta = new Venta();
        venta.setId(1L);

        when(ventaService.findAll()).thenReturn(List.of(venta));

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.ventaList[0].id").value(1));

        verify(ventaService, times(1)).findAll();
    }

    @Test
    @DisplayName("Cliente no puede listar ventas")
    @WithMockUser(username = "cliente", authorities = {"CLIENTE"})
    void clienteNoPuedeListarVentas() throws Exception {

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isForbidden());

        verify(ventaService, never()).findAll();
    }

    @Test
    @DisplayName("Usuario sin autenticación no puede listar ventas")
    void usuarioSinAutenticacionNoPuedeListarVentas() throws Exception {

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isUnauthorized());

        verify(ventaService, never()).findAll();
    }

    @Test
    @DisplayName("Administrador puede obtener venta por id")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void administradorPuedeObtenerVentaPorId() throws Exception {

        Venta venta = new Venta();
        venta.setId(1L);

        when(ventaService.findById(1L)).thenReturn(venta);

        mockMvc.perform(get("/api/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.ventas.href").exists());

        verify(ventaService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Cajero recibe 404 al consultar venta inexistente")
    @WithMockUser(username = "cajero", authorities = {"CAJERO"})
    void cajeroRecibe404AlConsultarVentaInexistente() throws Exception {

        when(ventaService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/ventas/99"))
                .andExpect(status().isNotFound());

        verify(ventaService, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Cajero puede generar venta")
    @WithMockUser(username = "cajero", authorities = {"CAJERO"})
    void cajeroPuedeGenerarVenta() throws Exception {

        Venta venta = new Venta();
        venta.setId(1L);

        when(ventaService.save(any(Venta.class))).thenReturn(venta);

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.ventas.href").exists());

        verify(ventaService, times(1)).save(any(Venta.class));
    }

    @Test
    @DisplayName("Administrador no puede generar venta si la regla exige rol cajero")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void administradorNoPuedeGenerarVenta() throws Exception {

        Venta venta = new Venta();

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isForbidden());

        verify(ventaService, never()).save(any(Venta.class));
    }

    @Test
    @DisplayName("Cliente no puede generar venta")
    @WithMockUser(username = "cliente", authorities = {"CLIENTE"})
    void clienteNoPuedeGenerarVenta() throws Exception {

        Venta venta = new Venta();

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isForbidden());

        verify(ventaService, never()).save(any(Venta.class));
    }

    @Test
    @DisplayName("Usuario sin autenticación no puede generar venta")
    void usuarioSinAutenticacionNoPuedeGenerarVenta() throws Exception {

        Venta venta = new Venta();

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isUnauthorized());

        verify(ventaService, never()).save(any(Venta.class));
    }
}