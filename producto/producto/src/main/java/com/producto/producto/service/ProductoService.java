package com.producto.producto.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.producto.producto.model.Categoria;
import com.producto.producto.model.Producto;
import com.producto.producto.repository.CategoriaRepository;
import com.producto.producto.repository.ProductoRepository;
import com.producto.producto.webclient.PromocionClient;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final PromocionClient promocionClient;

    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository,
                           PromocionClient promocionClient) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.promocionClient = promocionClient;
    }

    // Obtener productos con filtros opcionales
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

    // Obtener un producto con detalles
    public Producto getProductoConDetalles(Long id) {
        return productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
    }

    public List<Producto> getProductosByIdDescuento(Long descuentoId) {
        Map<String, Object> descuento = promocionClient.getDescuentoById(descuentoId);

        if (descuento == null) {
            throw new IllegalArgumentException("El descuento no existe");
        }

        Object productosObj = descuento.get("idProducto");

        if (productosObj == null) {
            return List.of();
        }

        List<?> productosRaw = (List<?>) productosObj;

        List<Long> idProductos = productosRaw.stream()
            .map(obj -> {
                if (obj instanceof Number) {
                    return ((Number) obj).longValue();
                } else {
                    throw new IllegalArgumentException("idProducto contiene valores no numéricos");
                }
            })
            .collect(Collectors.toList());

        if (idProductos.isEmpty()) {
            return List.of();
        }

        return productoRepository.findByIdProductoIn(idProductos);
    }

    // Agregar un producto
    public Producto agregarProducto(Producto producto) {
        if (producto.getCategoria() == null || producto.getCategoria().getIdCategoria() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }

        categoriaRepository.findById(producto.getCategoria().getIdCategoria())
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        return productoRepository.save(producto);
    }

    // Actualizar un producto
    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        producto.setNombre(productoActualizado.getNombre());
        producto.setDescripcion(productoActualizado.getDescripcion());
        producto.setPrecioUnitario(productoActualizado.getPrecioUnitario());
        producto.setStock(productoActualizado.getStock());
        producto.setIdEstado(productoActualizado.getIdEstado());

        if (productoActualizado.getCategoria() != null) {
            categoriaRepository.findById(productoActualizado.getCategoria().getIdCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

            producto.setCategoria(productoActualizado.getCategoria());
        }

        return productoRepository.save(producto);
    }

    // Actualizar stock individual
    public Producto actualizarStock(Long id, Integer cantidad) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        producto.setStock(cantidad);

        return productoRepository.save(producto);
    }

    // Actualizar stock en lote
    public void actualizarStockBulk(List<Map<String, Object>> updates) {
        for (Map<String, Object> update : updates) {
            Long idProducto = Long.valueOf(update.get("idProducto").toString());
            Integer cantidad = Integer.valueOf(update.get("cantidad").toString());
            actualizarStock(idProducto, cantidad);
        }
    }

    // Eliminar un producto
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    // --- CRUD de CATEGORÍA ---

    // Obtener todas las categorías
    public List<Categoria> getCategorias() {
        return categoriaRepository.findAll();
    }

    // Agregar una categoría
    public Categoria agregarCategoria(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        return categoriaRepository.save(categoria);
    }

    // Actualizar una categoría
    public Categoria actualizarCategoria(Long id, Categoria categoriaActualizada) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        categoria.setNombre(categoriaActualizada.getNombre());
        return categoriaRepository.save(categoria);
    }

    public void eliminarCategoria(Long id) {
        // Validar que exista
        categoriaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        // Validar que no tenga productos asociados
        if (productoRepository.existsByCategoria_IdCategoria(id)) {
            throw new IllegalStateException("No se puede eliminar la categoría, ya que tiene productos asociados");
        }

        // Si pasa todo, eliminar
        categoriaRepository.deleteById(id);
    }

    // --- Excepción personalizada ---
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}