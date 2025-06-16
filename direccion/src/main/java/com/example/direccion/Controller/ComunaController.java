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
import com.example.direccion.Service.ComunaService;
import com.example.direccion.model.Comuna;

@RestController
@RequestMapping("/api/v1/comunas")
public class ComunaController {

    @Autowired
    private ComunaService comunaService;

    //  Solo accesible por administrador
    @GetMapping
    public ResponseEntity<?> listarComunas(Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            return ResponseEntity.ok(comunaService.obtenerTodas());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //  Obtener comuna por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerComuna(@PathVariable Long id, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            return ResponseEntity.ok(comunaService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //  Crear nueva comuna
    @PostMapping
    public ResponseEntity<?> crearComuna(@Valid @RequestBody Comuna comuna, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            return ResponseEntity.ok(comunaService.crear(comuna));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Actualizar comuna existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarComuna(@PathVariable Long id, @Valid @RequestBody Comuna comuna, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            return ResponseEntity.ok(comunaService.actualizar(id, comuna));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //  Eliminar comuna
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarComuna(@PathVariable Long id, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            comunaService.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //  Verificación simple de rol
    private boolean esAdmin(Principal principal) {
        return principal != null && principal.getName().equals("admin");
    }

    //  Respuesta común si el usuario no es admin
    private ResponseEntity<String> accesoDenegado() {
        return ResponseEntity.status(403).body("Acceso denegado: Solo el administrador puede usar este recurso.");
    }
}