package com.example.tickets.controller;

import com.example.tickets.model.Mensaje;
import com.example.tickets.service.MensajeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

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


@RestController
@RequestMapping("/api/v1/mensajes")
@Tag(name = "Mensajes", description = "Operaciones CRUD para mensajes")
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    @Operation(
        summary = "Listar todos los mensajes",
        responses = {
            @ApiResponse(responseCode = "200", description = "Mensajes listados correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @GetMapping
    public ResponseEntity<?> listarTodos(Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();
        List<Mensaje> mensajes = mensajeService.obtenerTodos();
        List<EntityModel<Mensaje>> modelos = mensajes.stream()
                .map(m -> toModel(m, principal))
                .collect(Collectors.toList());
        return ResponseEntity.ok(modelos);
    }

    @Operation(
        summary = "Obtener un mensaje por ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Mensaje encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido o mensaje no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();
        try {
            Mensaje mensaje = mensajeService.obtenerPorId(id);
            return ResponseEntity.ok(toModel(mensaje, principal));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
        summary = "Crear un mensaje",
        responses = {
            @ApiResponse(responseCode = "200", description = "Mensaje creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Mensaje mensaje, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();
        try {
            Mensaje creado = mensajeService.crear(mensaje);
            return ResponseEntity.ok(toModel(creado, principal));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
        summary = "Actualizar un mensaje por ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Mensaje actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o ID inexistente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Mensaje mensaje, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();
        try {
            Mensaje actualizado = mensajeService.actualizar(id, mensaje);
            return ResponseEntity.ok(toModel(actualizado, principal));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
        summary = "Eliminar un mensaje por ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Mensaje eliminado correctamente"),
            @ApiResponse(responseCode = "400", description = "ID inválido o inexistente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();
        try {
            mensajeService.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Métodos HATEOAS
    private EntityModel<Mensaje> toModel(Mensaje mensaje, Principal principal) {
        return EntityModel.of(mensaje,
                linkTo(methodOn(MensajeController.class).obtenerPorId(mensaje.getIdMensaje(), principal)).withSelfRel(),
                linkTo(methodOn(MensajeController.class).listarTodos(principal)).withRel("mensajes"));
    }

    private boolean esAdmin(Principal principal) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private ResponseEntity<String> accesoDenegado() {
        return ResponseEntity.status(403).body("Acceso denegado: Solo el administrador puede usar este recurso.");
    }
}