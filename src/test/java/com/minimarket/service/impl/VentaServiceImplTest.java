package com.minimarket.service.impl;

import com.minimarket.entity.Venta;
import com.minimarket.repository.VentaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;

    @Test
    @DisplayName("Lista todas las ventas")
    void listaTodasLasVentas() {
        Venta venta = new Venta();
        venta.setId(1L);

        when(ventaRepository.findAll()).thenReturn(List.of(venta));

        List<Venta> resultado = ventaService.findAll();

        assertEquals(1, resultado.size());
        verify(ventaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Busca venta existente por id")
    void buscaVentaExistentePorId() {
        Venta venta = new Venta();
        venta.setId(1L);

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        Venta resultado = ventaService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(ventaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Retorna null si venta no existe")
    void retornaNullSiVentaNoExiste() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        Venta resultado = ventaService.findById(99L);

        assertNull(resultado);
        verify(ventaRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Guarda venta correctamente")
    void guardaVentaCorrectamente() {
        Venta venta = new Venta();
        venta.setId(1L);

        when(ventaRepository.save(venta)).thenReturn(venta);

        Venta resultado = ventaService.save(venta);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(ventaRepository, times(1)).save(venta);
    }

    @Test
    @DisplayName("Busca ventas por usuario")
    void buscaVentasPorUsuario() {
        Venta venta = new Venta();
        venta.setId(1L);

        when(ventaRepository.findByUsuarioId(7L)).thenReturn(List.of(venta));

        List<Venta> resultado = ventaService.findByUsuarioId(7L);

        assertEquals(1, resultado.size());
        verify(ventaRepository, times(1)).findByUsuarioId(7L);
    }
}