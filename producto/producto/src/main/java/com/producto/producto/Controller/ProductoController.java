package com.producto.producto.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.producto.producto.model.Categoria;
import com.producto.producto.model.EstadoProducto;
import com.producto.producto.model.Producto;
import com.producto.producto.service.ProductoService;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // GET /api/v1/productos?nombre=...&categoriaId=...
    @Operation(summary = "Obtiene productos con filtros opcionales de nombre y categoría")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos encontrada"),
        @ApiResponse(responseCode = "204", description = "No hay productos disponibles"),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<?> obtenerProductos(@RequestParam(required = false) String nombre,
                                              @RequestParam(required = false) Long categoriaId) {
        try {
            List<Producto> productos = productoService.getProductos(nombre, categoriaId);
            if (productos.isEmpty()) return ResponseEntity.noContent().build();

            List<EntityModel<Producto>> response = productos.stream()
                .map(producto -> EntityModel.of(producto,
                        linkTo(methodOn(ProductoController.class).obtenerProductoConDetalles(producto.getIdProducto())).withSelfRel()))
                .toList();

            return ResponseEntity.ok(CollectionModel.of(response,
                    linkTo(methodOn(ProductoController.class).obtenerProductos(nombre, categoriaId)).withSelfRel()));
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    // GET /api/v1/productos/{id}
    @Operation(summary = "Obtiene un producto por su ID con detalles")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProductoConDetalles(@PathVariable Long id) {
        try {
            Producto producto = productoService.getProductoConDetalles(id);
            return ResponseEntity.ok(EntityModel.of(producto,
                    linkTo(methodOn(ProductoController.class).obtenerProductoConDetalles(id)).withSelfRel(),
                    linkTo(methodOn(ProductoController.class).obtenerProductos(null, null)).withRel("productos")));
        } catch (ProductoService.ResourceNotFoundException e) {
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    // POST /api/v1/productos
    @Operation(summary = "Agrega un nuevo producto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<?> agregarProducto(@RequestBody Producto producto, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();
        try {
            Producto nuevoProducto = productoService.agregarProducto(producto);
            return ResponseEntity.status(201).body(nuevoProducto);
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    // PUT /api/v1/productos/{id}
    @Operation(summary = "Actualiza un producto existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();
        try {
            Producto actualizado = productoService.actualizarProducto(id, producto);
            return ResponseEntity.ok(actualizado);
        } catch (ProductoService.ResourceNotFoundException e) {
            return errorResponse(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    // DELETE /api/v1/productos/{id}
    @Operation(summary = "Elimina un producto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto eliminado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    // GET /api/v1/productos/categorias
    @GetMapping("/categorias")
    public ResponseEntity<?> obtenerCategorias() {
        try {
            List<Categoria> categorias = productoService.getCategorias();
            return categorias.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(categorias);
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    // POST /api/v1/productos/categorias
    @PostMapping("/categorias")
    public ResponseEntity<?> agregarCategoria(@RequestBody Categoria categoria, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();
        try {
            return ResponseEntity.status(201).body(productoService.agregarCategoria(categoria));
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    // PUT /api/v1/productos/categorias/{id}
    @PutMapping("/categorias/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable Long id, @RequestBody Categoria categoria, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();
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

    // DELETE /api/v1/productos/categorias/{id}
    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();
        try {
            productoService.eliminarCategoria(id);
            return ResponseEntity.ok().build();
        } catch (ProductoService.ResourceNotFoundException e) {
            return errorResponse(404, e.getMessage());
        } catch (IllegalStateException | IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    // GET /api/v1/productos/estado?estado=ACTIVO
    @GetMapping("/estado")
    public ResponseEntity<?> obtenerPorEstado(@RequestParam EstadoProducto estado) {
        try {
            List<Producto> productos = productoService.getProductosPorEstado(estado);
            return productos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(productos);
        } catch (IllegalArgumentException e) {
            return errorResponse(400, "Estado no válido: " + e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error interno del servidor: " + e.getMessage());
        }
    }

    // Validaciones reutilizables
    private boolean esAdmin(Principal principal) {
        return principal != null && principal.getName().equals("admin");
    }

    private ResponseEntity<Map<String, Object>> accesoDenegado() {
        return ResponseEntity.status(403).body(Map.of(
            "status", "error",
            "message", "Acceso denegado: no tienes permisos suficientes"
        ));
    }

    private ResponseEntity<Map<String, Object>> errorResponse(int status, String message) {
        return ResponseEntity.status(status).body(Map.of(
            "status", "error",
            "message", message
        ));
    }
}