package com.example.direccion.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import jakarta.validation.Valid;
import com.example.direccion.Service.RegionService;
import com.example.direccion.model.Region;

@RestController
@RequestMapping("/api/v1/regiones")
public class RegionController {

    @Autowired
    private RegionService regionService;

    //  Listar todas las regiones (solo admin)
    @GetMapping
    public ResponseEntity<?> listarRegiones(Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            return ResponseEntity.ok(regionService.obtenerTodas());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener región por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerRegion(@PathVariable Long id, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            return ResponseEntity.ok(regionService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Crear nueva región
    @PostMapping
    public ResponseEntity<?> crearRegion(@Valid @RequestBody Region region, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            return ResponseEntity.ok(regionService.crear(region));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Actualizar región
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarRegion(@PathVariable Long id, @Valid @RequestBody Region region, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            return ResponseEntity.ok(regionService.actualizar(id, region));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Eliminar región
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRegion(@PathVariable Long id, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            regionService.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //  Simulación de verificación de rol admin
    private boolean esAdmin(Principal principal) {
        return principal != null && principal.getName().equals("admin");
    }

    private ResponseEntity<String> accesoDenegado() {
        return ResponseEntity.status(403).body("Acceso denegado: Solo el administrador puede usar este recurso.");
    }
}