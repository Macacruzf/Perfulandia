package com.example.tickets.controller;

import com.example.tickets.model.Motivo;
import com.example.tickets.service.MotivoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/motivos")
@Tag(name = "Motivos", description = "Operaciones CRUD para Motivos")
public class MotivoController {

    @Autowired
    private MotivoService motivoService;

    @Operation(
        summary = "Listar todos los motivos",
        responses = {
            @ApiResponse(responseCode = "200", description = "Motivos listados exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @GetMapping
    public ResponseEntity<?> listarTodos(Principal principal) {
        if (!esAdmin()) return accesoDenegado();

        List<Motivo> motivos = motivoService.obtenerTodos();
        var resultado = motivos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    @Operation(
        summary = "Obtener un motivo por su ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Motivo encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido o no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id, Principal principal) {
        if (!esAdmin()) return accesoDenegado();

        try {
            Motivo motivo = motivoService.obtenerPorId(id);
            return ResponseEntity.ok(toModel(motivo));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
        summary = "Crear un nuevo motivo",
        responses = {
            @ApiResponse(responseCode = "200", description = "Motivo creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Motivo motivo, Principal principal) {
        if (!esAdmin()) return accesoDenegado();

        try {
            Motivo creado = motivoService.crear(motivo);
            return ResponseEntity.ok(toModel(creado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
        summary = "Actualizar un motivo",
        responses = {
            @ApiResponse(responseCode = "200", description = "Motivo actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o ID inexistente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Motivo motivo, Principal principal) {
        if (!esAdmin()) return accesoDenegado();

        try {
            Motivo actualizado = motivoService.actualizar(id, motivo);
            return ResponseEntity.ok(toModel(actualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
        summary = "Eliminar un motivo",
        responses = {
            @ApiResponse(responseCode = "200", description = "Motivo eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "ID inválido o no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, Principal principal) {
        if (!esAdmin()) return accesoDenegado();

        try {
            motivoService.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Métodos auxiliares
    private boolean esAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private ResponseEntity<String> accesoDenegado() {
        return ResponseEntity.status(403).body("Acceso denegado: Solo el administrador puede usar este recurso.");
    }

    private EntityModel<Motivo> toModel(Motivo motivo) {
        return EntityModel.of(motivo,
            linkTo(methodOn(MotivoController.class).obtenerPorId(motivo.getIdMotivo(), null)).withSelfRel(),
            linkTo(methodOn(MotivoController.class).listarTodos(null)).withRel("todos"));
    }
}