package com.example.gestionventas.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gestionventas.model.DetalleVenta;
import com.example.gestionventas.model.Venta;
import com.example.gestionventas.service.VentaService;
import com.example.gestionventas.webclient.DireccionClient;
import com.example.gestionventas.webclient.ProductoClient;
import com.example.gestionventas.webclient.UsuarioClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/v1/ventas")
@Tag(name = "Ventas", description = "Operaciones CRUD para la entidad Venta")
public class VentaController {

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private DireccionClient direccionClient;

    @Autowired
    private ProductoClient productoClient;

    @Autowired
    private VentaService ventaService;

    VentaController(UsuarioClient usuarioClient) {
        this.usuarioClient = usuarioClient;
    }

    private EntityModel<Venta> toModel(Venta venta) {
        return EntityModel.of(venta,
                linkTo(methodOn(VentaController.class).getVentaById(venta.getIdVenta())).withSelfRel(),
                linkTo(methodOn(VentaController.class).getAllVentas()).withRel("todas"));
    }

    @Operation(summary = "Obtener todas las ventas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ventas encontradas",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venta.class))),
        @ApiResponse(responseCode = "204", description = "No hay ventas registradas")
    })
    @GetMapping
    public ResponseEntity<List<EntityModel<Venta>>> getAllVentas() {
        List<Venta> ventas = ventaService.getVentas();
        if (ventas.isEmpty()) return ResponseEntity.noContent().build();

        List<EntityModel<Venta>> modelos = ventas.stream().map(this::toModel).toList();
        return ResponseEntity.ok(modelos);
    }

    @Operation(summary = "Obtener una venta por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Venta encontrada",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venta.class))),
        @ApiResponse(responseCode = "204", description = "Venta no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Venta>> getVentaById(@PathVariable Long id) {
        try {
            Venta venta = ventaService.getVentaById(id);
            return ResponseEntity.ok(toModel(venta));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Obtener los detalles de una venta por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalles encontrados",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalleVenta.class))),
        @ApiResponse(responseCode = "204", description = "La venta no tiene detalles o no existe")
    })
    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<DetalleVenta>> getDetallesByVentaId(@PathVariable Long id) {
        List<DetalleVenta> detalles = ventaService.getDetallesByVentaId(id);
        return detalles.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(detalles);
    }

    @PostMapping
    @Operation(summary = "Crear una nueva venta con validaciones, cálculo de total y actualización de stock")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                example = """
                {
                    "venta": {
                        "idUsuario": 1,
                        "idDireccion": 2
                    },
                    "detalles": [
                        {
                            "idProducto": 2,
                            "cantidad": 1
                        },
                        {
                            "idProducto": 3,
                            "cantidad": 2
                        }
                    ]
                }
                """
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Venta creada con éxito",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venta.class))),
        @ApiResponse(responseCode = "400", description = "Error de validación o datos inválidos")
    })
    public ResponseEntity<?> createVenta(@RequestBody Map<String, Object> payload) {
        try {
            Map<?, ?> ventaMap = (Map<?, ?>) payload.get("venta");
            if (ventaMap == null || !ventaMap.containsKey("idUsuario") || !ventaMap.containsKey("idDireccion")) {
                return ResponseEntity.badRequest().body("Faltan datos de venta.");
            }

            Long idUsuario = Long.valueOf(ventaMap.get("idUsuario").toString());
            Long idDireccion = Long.valueOf(ventaMap.get("idDireccion").toString());

            // Validar existencia del usuario y dirección
            usuarioClient.getUsuarioById(idUsuario);
            direccionClient.getDireccionById(idDireccion);

            Object detallesObj = payload.get("detalles");
            if (!(detallesObj instanceof List<?> detallesList) || detallesList.isEmpty()) {
                return ResponseEntity.badRequest().body("La lista de detalles es inválida o vacía.");
            }

            List<DetalleVenta> detalles = new ArrayList<>();
            int total = 0;

            for (Object o : detallesList) {
                if (o instanceof Map<?, ?> detalleMap) {
                    Long idProducto = Long.valueOf(detalleMap.get("idProducto").toString());
                    int cantidad = Integer.parseInt(detalleMap.get("cantidad").toString());

                    Map<String, Object> productoMap = productoClient.getProductoById(idProducto);
                    if (productoMap == null || !productoMap.containsKey("precioUnitario")) {
                        return ResponseEntity.badRequest().body("Producto con ID " + idProducto + " no encontrado o sin precio.");
                    }

                    int precio;
                    try {
                        Object precioObj = productoMap.get("precioUnitario");
                        if (precioObj instanceof Integer) {
                            precio = (Integer) precioObj;
                        } else if (precioObj instanceof Number) {
                            precio = ((Number) precioObj).intValue();
                        } else {
                            throw new RuntimeException("Tipo de precio no válido");
                        }
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().body("Error al convertir el precio del producto " + idProducto);
                    }

                    int subtotal = precio * cantidad;
                    total += subtotal;

                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setIdProducto(idProducto);
                    detalle.setCantidad(cantidad);
                    detalle.setPrecioUnitario(precio);
                    detalle.setSubtotal(subtotal);
                    detalles.add(detalle);
                }
            }

            Venta venta = new Venta();
            venta.setIdUsuario(idUsuario);
            venta.setIdDireccion(idDireccion);
            venta.setFechaVenta(LocalDateTime.now());
            venta.setTotal(total);

            Venta nuevaVenta = ventaService.createVenta(venta, detalles);

            // Actualizar el stock de los productos (descontando cantidad)
            List<Map<String, Object>> updates = detalles.stream()
                .map(d -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idProducto", d.getIdProducto());
                    map.put("cantidad", -d.getCantidad()); // descontar stock
                    return map;
                })
                .collect(Collectors.toList());

            productoClient.actualizarStockBulk(updates);

            return ResponseEntity.status(HttpStatus.CREATED).body(toModel(nuevaVenta));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error procesando la venta: " + e.getMessage());
        }
    }

    @Operation(summary = "Actualizar una venta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Venta actualizada",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venta.class))),
        @ApiResponse(responseCode = "204", description = "Venta no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Venta>> updateVenta(@PathVariable Long id, @RequestBody Venta venta) {
        try {
            Venta actualizada = ventaService.updateVenta(id, venta);
            return ResponseEntity.ok(toModel(actualizada));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Eliminar una venta por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Venta eliminada"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenta(@PathVariable Long id) {
        try {
            ventaService.deleteVenta(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

 