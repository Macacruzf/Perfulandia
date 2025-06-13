package com.example.gestionventas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gestionventas.model.DetalleVenta;

import com.example.gestionventas.model.Venta;
import com.example.gestionventas.service.VentaService;

@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {
@Autowired
    private VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<Venta>> obtenerVentas() {
        List<Venta> lista = ventaService.getVentas();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<?> registrarVenta(@RequestBody VentaRequest ventaRequest) {
        try {
            Venta venta = ventaService.registrarVenta(ventaRequest.getVenta(), ventaRequest.getDetalles());
            return ResponseEntity.status(201).body(venta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/factura")
    public ResponseEntity<String> generarFactura(@PathVariable Long id) {
        try {
            String factura = ventaService.generarFactura(id);
            return ResponseEntity.ok(factura);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}

// Clase auxiliar para manejar la solicitud de venta
class VentaRequest {
    private Venta venta;
    private List<DetalleVenta> detalles;

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }
}