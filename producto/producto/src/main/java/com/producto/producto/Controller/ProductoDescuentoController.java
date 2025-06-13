package com.producto.producto.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.producto.producto.service.ProductoDescuentoService;

@RestController
@RequestMapping("/api/v1/productodescuentos")
public class ProductoDescuentoController {

    private final ProductoDescuentoService productoDescuentoService;

    public ProductoDescuentoController(ProductoDescuentoService productoDescuentoService) {
        this.productoDescuentoService = productoDescuentoService;
    }

    @GetMapping("/{idProducto}")
    public ResponseEntity<?> obtenerDescuentosPorProducto(@PathVariable Long idProducto) {
        try {
            List<Map<String, Object>> descuentos = productoDescuentoService.obtenerDescuentosPorProducto(idProducto);

            if (descuentos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(descuentos);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener descuentos: " + e.getMessage());
        }
    }
}
