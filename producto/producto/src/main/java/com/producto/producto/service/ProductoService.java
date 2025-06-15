package com.producto.producto.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.producto.producto.model.Categoria;
import com.producto.producto.model.EstadoProducto;
import com.producto.producto.model.Producto;
import com.producto.producto.repository.CategoriaRepository;
import com.producto.producto.repository.ProductoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // Obtener productos con filtros opcionales de nombre o categoría
    public List<Producto> getProductos(String nombre, Long categoriaId) {
        if (nombre != null && categoriaId != null) {
            return productoRepository.findByNombreContainingIgnoreCaseAndCategoria_IdCategoria(nombre, categoriaId);
        } else if (nombre != null) {
            return productoRepository.findByNombreContainingIgnoreCase(nombre);
        } else if (categoriaId != null) {
            return productoRepository.findByCategoria_IdCategoria(categoriaId);
        } else {
            return productoRepository.findAll();
        }
    }

    // Obtener productos filtrando solo por estado 
    public List<Producto> getProductosPorEstado(EstadoProducto estado) {
        return productoRepository.findByEstado(estado);
    }

    // Obtener un producto con todos sus detalles por ID
    public Producto getProductoConDetalles(Long id) {
        return productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
    }

    // Agregar un nuevo producto (valida que la categoría exista)
    public Producto agregarProducto(Producto producto) {
        if (producto.getCategoria() == null || producto.getCategoria().getIdCategoria() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }

        categoriaRepository.findById(producto.getCategoria().getIdCategoria())
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        return productoRepository.save(producto);
    }

    // Actualizar datos de un producto existente
    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        producto.setNombre(productoActualizado.getNombre());
        producto.setDescripcion(productoActualizado.getDescripcion());
        producto.setPrecioUnitario(productoActualizado.getPrecioUnitario());
        producto.setStock(productoActualizado.getStock());
        producto.setEstado(productoActualizado.getEstado()); // ← Se reemplaza el uso de idEstado

        if (productoActualizado.getCategoria() != null) {
            categoriaRepository.findById(productoActualizado.getCategoria().getIdCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

            producto.setCategoria(productoActualizado.getCategoria());
        }

        return productoRepository.save(producto);
    }

    // Actualizar solo el stock de un producto
    public Producto actualizarStock(Long id, Integer cantidad) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        producto.setStock(cantidad);
        return productoRepository.save(producto);
    }

    // Actualizar stock en lote (varios productos al mismo tiempo)
    public void actualizarStockBulk(List<Map<String, Object>> updates) {
        for (Map<String, Object> update : updates) {
            Long idProducto = Long.valueOf(update.get("idProducto").toString());
            Integer cantidad = Integer.valueOf(update.get("cantidad").toString());
            actualizarStock(idProducto, cantidad);
        }
    }

    // Eliminar un producto por ID
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    // --- CRUD de CATEGORÍA ---

    public List<Categoria> getCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria agregarCategoria(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        return categoriaRepository.save(categoria);
    }

    public Categoria actualizarCategoria(Long id, Categoria categoriaActualizada) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        categoria.setNombre(categoriaActualizada.getNombre());
        return categoriaRepository.save(categoria);
    }

    public void eliminarCategoria(Long id) {
        categoriaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        if (productoRepository.existsByCategoria_IdCategoria(id)) {
            throw new IllegalStateException("No se puede eliminar la categoría, ya que tiene productos asociados");
        }

        categoriaRepository.deleteById(id);
    }

    // Excepción personalizada para recursos no encontrados
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}