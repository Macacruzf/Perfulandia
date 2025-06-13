package com.producto.producto.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.producto.producto.model.Categoria;
import com.producto.producto.model.Producto;
import com.producto.producto.service.ProductoService;
import com.producto.producto.webclient.UsuarioClient;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioClient usuarioClient;

    @GetMapping("/productos")
    public ResponseEntity<?> obtenerProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId) {
        try {
            List<Producto> productos = productoService.getProductos(nombre, categoriaId);

            if (productos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<Map<String, Object>> response = new java.util.ArrayList<>();

            for (Producto producto : productos) {
                Map<String, Object> productoMap = new java.util.HashMap<>();
                productoMap.put("idProducto", producto.getIdProducto());
                productoMap.put("nombre", producto.getNombre());
                productoMap.put("descripcion", producto.getDescripcion());
                productoMap.put("precioUnitario", producto.getPrecioUnitario());
                productoMap.put("stock", producto.getStock());
                productoMap.put("idCategoria", producto.getCategoria().getIdCategoria());
                productoMap.put("idEstado", producto.getIdEstado());

                if (producto.getStock() != null && producto.getStock() > 0) {
                    productoMap.put("estado", "Disponible");
                } else {
                    productoMap.put("estado", "No disponible");
                }

                response.add(productoMap);
            }

            return ResponseEntity.ok(response);

        } catch (ProductoService.ResourceNotFoundException e) {
            return errorResponse(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<?> obtenerProductoConDetalles(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productoService.getProductoConDetalles(id));
        } catch (ProductoService.ResourceNotFoundException e) {
            return errorResponse(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    @GetMapping("/descuentos/{descuentoId}/productos")
    public ResponseEntity<?> obtenerProductosPorDescuento(@PathVariable Long descuentoId) {
        try {
            List<Producto> productos = productoService.getProductosByIdDescuento(descuentoId);
            return productos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(productos);
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    @PostMapping("/productos")
    public ResponseEntity<?> agregarProducto(@RequestBody Producto producto, @RequestParam Long idUsuario) {
        try {
            Map<String, Object> usuario = usuarioClient.getUsuarioById(idUsuario);
            String privilegio = (String) usuario.get("privilegio");

            if (!privilegio.equalsIgnoreCase("ADMIN")) {
                return errorResponse(403, "No tienes permisos para esta operación");
            }

            return ResponseEntity.status(201).body(productoService.agregarProducto(producto));
        } catch (ProductoService.ResourceNotFoundException e) {
            return errorResponse(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        try {
            return ResponseEntity.ok(productoService.actualizarProducto(id, producto));
        } catch (ProductoService.ResourceNotFoundException e) {
            return errorResponse(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    @PutMapping("/productos/{id}/stock")
    public ResponseEntity<?> actualizarStock(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            Integer cantidad = request.get("cantidad");
            if (cantidad == null) {
                return errorResponse(400, "La cantidad es obligatoria");
            }
            return ResponseEntity.ok(productoService.actualizarStock(id, cantidad));
        } catch (ProductoService.ResourceNotFoundException e) {
            return errorResponse(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    @PutMapping("/productos/stock/bulk")
    public ResponseEntity<?> actualizarStockBulk(@RequestBody List<Map<String, Object>> updates) {
        try {
            productoService.actualizarStockBulk(updates);
            return ResponseEntity.ok().build();
        } catch (ProductoService.ResourceNotFoundException e) {
            return errorResponse(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.ok().build();
        } catch (ProductoService.ResourceNotFoundException e) {
            return errorResponse(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    @GetMapping("/categorias")
    public ResponseEntity<?> obtenerCategorias() {
        try {
            List<Categoria> categorias = productoService.getCategorias();
            return categorias.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(categorias);
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    @PostMapping("/categorias")
    public ResponseEntity<?> agregarCategoria(@RequestBody Categoria categoria) {
        try {
            return ResponseEntity.status(201).body(productoService.agregarCategoria(categoria));
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable Long id, @RequestBody Categoria categoria) {
        try {
            return ResponseEntity.ok(productoService.actualizarCategoria(id, categoria));
        } catch (ProductoService.ResourceNotFoundException e) {
            return errorResponse(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id) {
        try {
            productoService.eliminarCategoria(id);
            return ResponseEntity.ok().build();
        } catch (ProductoService.ResourceNotFoundException e) {
            return errorResponse(404, e.getMessage());
        } catch (IllegalStateException e) {
            return errorResponse(400, e.getMessage());
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    // Método utilitario para construir error response
    private ResponseEntity<Map<String, Object>> errorResponse(int status, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", message);
        error.put("status", status);
        return ResponseEntity.status(status).body(error);
    }
}