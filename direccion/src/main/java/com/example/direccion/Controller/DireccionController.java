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
import com.example.direccion.Service.DireccionService;
import com.example.direccion.model.Direccion;


@RestController
@RequestMapping("/api/v1/direcciones")
public class DireccionController {

    @Autowired
    private DireccionService direccionService;

    // Listar todas las direcciones (solo admin)
    @GetMapping
    public ResponseEntity<?> listarTodas(Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            return ResponseEntity.ok(direccionService.obtenerTodas());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener una dirección por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            return ResponseEntity.ok(direccionService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener direcciones por comuna
    @GetMapping("/comuna/{idComuna}")
    public ResponseEntity<?> listarPorComuna(@PathVariable Long idComuna, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            return ResponseEntity.ok(direccionService.obtenerPorComuna(idComuna));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Crear una nueva dirección
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Direccion direccion, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            return ResponseEntity.ok(direccionService.crearDireccion(direccion));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Actualizar dirección
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Direccion direccion, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            return ResponseEntity.ok(direccionService.actualizarDireccion(id, direccion));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Eliminar dirección
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            direccionService.eliminarDireccion(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Validación de rol simulada (admin)
    private boolean esAdmin(Principal principal) {
        return principal != null && principal.getName().equals("admin");
    }

    // Mensaje común para acceso denegado
    private ResponseEntity<String> accesoDenegado() {
        return ResponseEntity.status(403).body("Acceso denegado: Solo el administrador puede usar este recurso.");
    }
}