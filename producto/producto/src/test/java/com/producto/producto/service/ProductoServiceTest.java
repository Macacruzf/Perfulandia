package com.producto.producto.service;

import com.producto.producto.model.Categoria;
import com.producto.producto.model.EstadoProducto;
import com.producto.producto.model.Producto;
import com.producto.producto.repository.CategoriaRepository;
import com.producto.producto.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ProductoServiceTest {

      @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetProductosSinFiltros() {
        // Creo una lista con un producto para simular la respuesta de la base de datos
        List<Producto> lista = new ArrayList<>();
        lista.add(new Producto());

        when(productoRepository.findAll()).thenReturn(lista);

        List<Producto> resultado = productoService.getProductos(null, null);

        // Verifico que me devuelva lo que espero
        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    public void testGetProductoConDetallesExistente() {
        // Simulo un producto con ID 1
        Producto producto = new Producto();
        producto.setIdProducto(1L);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.getProductoConDetalles(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProducto());
    }

    @Test
    public void testAgregarProductoConCategoriaExistente() {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1L);

        Producto producto = new Producto();
        producto.setCategoria(categoria);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(producto)).thenReturn(producto);

        Producto resultado = productoService.agregarProducto(producto);

        assertNotNull(resultado);
        verify(productoRepository).save(producto);
    }

    @Test
    public void testActualizarStock() {
        Producto producto = new Producto();
        producto.setIdProducto(1L);
        producto.setStock(10);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        Producto actualizado = productoService.actualizarStock(1L, 50);

        assertEquals(50, actualizado.getStock());
    }

    @Test
    public void testEliminarProductoExistente() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        productoService.eliminarProducto(1L);

        verify(productoRepository).deleteById(1L);
    }

    @Test
    public void testAgregarCategoriaValida() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Tecnología");

        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        Categoria nuevaCategoria = productoService.agregarCategoria(categoria);

        assertNotNull(nuevaCategoria);
    }

    @Test
    public void testGetProductosPorEstadoDisponible() {
        Producto producto = new Producto();
        producto.setEstado(EstadoProducto.ACTIVO);

        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findByEstado(EstadoProducto.ACTIVO)).thenReturn(productos);

        List<Producto> resultado = productoService.getProductosPorEstado(EstadoProducto.ACTIVO);

        assertEquals(1, resultado.size());
        assertEquals(EstadoProducto.ACTIVO, resultado.get(0).getEstado());
    }
}