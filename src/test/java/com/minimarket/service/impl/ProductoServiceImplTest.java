package com.minimarket.service.impl;

import com.minimarket.entity.Producto;
import com.minimarket.repository.ProductoRepository;
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
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    @DisplayName("Lista todos los productos")
    void listaTodosLosProductos() {
        Producto producto = new Producto();
        producto.setId(1L);

        when(productoRepository.findAll()).thenReturn(List.of(producto));

        List<Producto> resultado = productoService.findAll();

        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Busca producto existente por id")
    void buscaProductoExistentePorId() {
        Producto producto = new Producto();
        producto.setId(1L);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Retorna null si producto no existe")
    void retornaNullSiProductoNoExiste() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Producto resultado = productoService.findById(99L);

        assertNull(resultado);
        verify(productoRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Guarda producto correctamente")
    void guardaProductoCorrectamente() {
        Producto producto = new Producto();
        producto.setId(1L);

        when(productoRepository.save(producto)).thenReturn(producto);

        Producto resultado = productoService.save(producto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    @DisplayName("Elimina producto por id")
    void eliminaProductoPorId() {
        productoService.deleteById(1L);

        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Busca productos por categoria")
    void buscaProductosPorCategoria() {
        Producto producto = new Producto();
        producto.setId(1L);

        when(productoRepository.findByCategoriaId(5L)).thenReturn(List.of(producto));

        List<Producto> resultado = productoService.findByCategoriaId(5L);

        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).findByCategoriaId(5L);
    }
}