package com.minimarket.service.impl;

import com.minimarket.entity.Inventario;
import com.minimarket.repository.InventarioRepository;
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
class InventarioServiceImplTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    @Test
    @DisplayName("Lista movimientos de inventario")
    void listaMovimientosInventario() {
        Inventario inventario = new Inventario();
        inventario.setId(1L);

        when(inventarioRepository.findAll()).thenReturn(List.of(inventario));

        List<Inventario> resultado = inventarioService.findAll();

        assertEquals(1, resultado.size());
        verify(inventarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Busca movimiento existente por id")
    void buscaMovimientoExistentePorId() {
        Inventario inventario = new Inventario();
        inventario.setId(1L);

        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));

        Inventario resultado = inventarioService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(inventarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Retorna null si movimiento no existe")
    void retornaNullSiMovimientoNoExiste() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        Inventario resultado = inventarioService.findById(99L);

        assertNull(resultado);
        verify(inventarioRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Guarda movimiento de inventario")
    void guardaMovimientoInventario() {
        Inventario inventario = new Inventario();
        inventario.setId(1L);

        when(inventarioRepository.save(inventario)).thenReturn(inventario);

        Inventario resultado = inventarioService.save(inventario);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(inventarioRepository, times(1)).save(inventario);
    }

    @Test
    @DisplayName("Elimina movimiento por id")
    void eliminaMovimientoPorId() {
        inventarioService.deleteById(1L);

        verify(inventarioRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Busca movimientos por producto")
    void buscaMovimientosPorProducto() {
        Inventario inventario = new Inventario();
        inventario.setId(1L);

        when(inventarioRepository.findByProductoId(10L)).thenReturn(List.of(inventario));

        List<Inventario> resultado = inventarioService.findByProductoId(10L);

        assertEquals(1, resultado.size());
        verify(inventarioRepository, times(1)).findByProductoId(10L);
    }
}