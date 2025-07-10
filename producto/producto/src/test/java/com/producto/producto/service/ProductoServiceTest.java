package com.producto.producto.service;

import com.producto.producto.model.Categoria;
import com.producto.producto.model.EstadoProducto;
import com.producto.producto.model.Producto;
import com.producto.producto.repository.CategoriaRepository;
import com.producto.producto.repository.ProductoRepository;
import com.producto.producto.service.ProductoService.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria(1L, "Dulces", "Aromas Dulces y Aromaticos");
        producto = new Producto(1L, "Yara", "Con toques de petalos de rosas", 1000, 10, EstadoProducto.ACTIVO, categoria);
    }

    // Verifica que getProductos() devuelva una lista completa si no se usan filtros
    @Test
    void getProductosDebeRetornarLista() {
        when(productoRepository.findAll()).thenReturn(List.of(producto));
        List<Producto> productos = productoService.getProductos(null, null);
        assertFalse(productos.isEmpty());
    }

    // Verifica que se puedan filtrar productos por nombre
    @Test
    void getProductosFiltrandoPorNombre() {
        when(productoRepository.findByNombreContainingIgnoreCase("Yara")).thenReturn(List.of(producto));
        List<Producto> productos = productoService.getProductos("Yara", null);
        assertEquals(1, productos.size());
    }

    // Verifica que se puedan filtrar productos por ID de categoría
    @Test
    void getProductosFiltrandoPorCategoria() {
        when(productoRepository.findByCategoria_IdCategoria(1L)).thenReturn(List.of(producto));
        List<Producto> productos = productoService.getProductos(null, 1L);
        assertEquals(1, productos.size());
    }

    // Verifica que se puedan obtener productos filtrados por estado
    @Test
    void getProductosPorEstadoDebeRetornarLista() {
        when(productoRepository.findByEstado(EstadoProducto.ACTIVO)).thenReturn(List.of(producto));
        List<Producto> productos = productoService.getProductosPorEstado(EstadoProducto.ACTIVO);
        assertEquals(1, productos.size());
    }

    // Verifica que se pueda obtener un producto existente por ID
    @Test
    void getProductoConDetallesExistente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        Producto encontrado = productoService.getProductoConDetalles(1L);
        assertNotNull(encontrado);
    }

    // Verifica que se lanza excepción si el producto no existe al buscar por ID
    @Test
    void getProductoConDetallesNoExistenteDebeLanzarExcepcion() {
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());
        org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productoService.getProductoConDetalles(1L);
        });
    }

    // Verifica que se pueda agregar un producto si la categoría existe
    @Test
    void agregarProductoConCategoriaExistente() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any())).thenReturn(producto);
        Producto creado = productoService.agregarProducto(producto);
        assertNotNull(creado);
    }

    // Verifica que no se pueda agregar un producto sin categoría
    @Test
    void agregarProductoSinCategoriaDebeLanzarExcepcion() {
        producto.setCategoria(null);
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            productoService.agregarProducto(producto);
        });
    }

    // Verifica que se pueda actualizar un producto existente correctamente
    @Test
    void actualizarProductoExistente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any())).thenReturn(producto);
        Producto actualizado = productoService.actualizarProducto(1L, producto);
        assertEquals("Yara", actualizado.getNombre());
    }

    // Verifica que lanzar excepción si se actualiza un producto inexistente
    @Test
    void actualizarProductoNoExistenteDebeLanzarExcepcion() {
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());
        org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productoService.actualizarProducto(1L, producto);
        });
    }

    // Verifica que se pueda actualizar el stock de un producto existente
    @Test
    void actualizarStockCorrectamente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any())).thenReturn(producto);
        Producto actualizado = productoService.actualizarStock(1L, 20);
        assertEquals(20, actualizado.getStock());
    }

    // Verifica que lanzar excepción si el producto no existe al actualizar stock
    @Test
    void actualizarStockProductoNoExiste() {
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());
        org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productoService.actualizarStock(1L, 10);
        });
    }

    // Verifica que el método para actualizar stock masivamente no lance excepciones
    @Test
    void actualizarStockBulk() {
        Map<String, Object> stockUpdate = new HashMap<>();
        stockUpdate.put("idProducto", 1L);
        stockUpdate.put("cantidad", 5);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any())).thenReturn(producto);
        assertDoesNotThrow(() -> productoService.actualizarStockBulk(List.of(stockUpdate)));
    }

    // Verifica que se pueda eliminar un producto existente
    @Test
    void eliminarProductoExistente() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> productoService.eliminarProducto(1L));
    }

    // Verifica que lanzar excepción si el producto a eliminar no existe
    @Test
    void eliminarProductoNoExistenteDebeLanzarExcepcion() {
        when(productoRepository.existsById(1L)).thenReturn(false);
        org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productoService.eliminarProducto(1L);
        });
    }

    // Verifica que se puedan listar todas las categorías
    @Test
    void getCategoriasDebeRetornarLista() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));
        assertEquals(1, productoService.getCategorias().size());
    }

    // Verifica que se pueda agregar una categoría correctamente
    @Test
    void agregarCategoriaCorrectamente() {
        when(categoriaRepository.save(any())).thenReturn(categoria);
        Categoria creada = productoService.agregarCategoria(categoria);
        assertEquals("Dulces", creada.getNombre());
    }

    // Verifica que no se puede agregar categoría sin nombre
    @Test
    void agregarCategoriaSinNombreDebeLanzarExcepcion() {
        categoria.setNombre(null);
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            productoService.agregarCategoria(categoria);
        });
    }

    // Verifica que se pueda actualizar una categoría existente
    @Test
    void actualizarCategoriaExistente() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any())).thenReturn(categoria);
        Categoria actualizada = productoService.actualizarCategoria(1L, categoria);
        assertEquals("Dulces", actualizada.getNombre());
    }

    // Verifica que lanzar excepción si la categoría a actualizar no existe
    @Test
    void actualizarCategoriaNoExistenteDebeLanzarExcepcion() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());
        org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productoService.actualizarCategoria(1L, categoria);
        });
    }

    // Verifica que se pueda eliminar una categoría sin productos asociados
    @Test
    void eliminarCategoriaCorrectamente() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.existsByCategoria_IdCategoria(1L)).thenReturn(false);
        assertDoesNotThrow(() -> productoService.eliminarCategoria(1L));
    }

    // Verifica que lanzar excepción si la categoría tiene productos asociados
    @Test
    void eliminarCategoriaConProductosAsociadosDebeLanzarExcepcion() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.existsByCategoria_IdCategoria(1L)).thenReturn(true);
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () -> {
            productoService.eliminarCategoria(1L);
        });
    }

    // Verifica que lanzar excepción si se intenta eliminar una categoría inexistente
    @Test
    void eliminarCategoriaNoExistenteDebeLanzarExcepcion() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productoService.eliminarCategoria(1L));
    }
}


