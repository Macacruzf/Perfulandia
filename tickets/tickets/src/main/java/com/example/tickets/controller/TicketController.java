package com.example.tickets.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable ;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import com.example.tickets.model.Ticket;
import com.example.tickets.service.TicketService;

import org.springframework.security.core.Authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/tickets")
@Tag(name = "Tickets", description = "Operaciones CRUD sobre tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Operation(
        summary = "Listar todos los tickets",
        responses = {
            @ApiResponse(responseCode = "200", description = "Tickets listados correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @GetMapping
    public ResponseEntity<?> listarTodos() {
        if (!esAdmin()) return accesoDenegado();
        return ResponseEntity.ok(ticketService.obtenerTodos());
    }

    @Operation(
        summary = "Obtener un ticket por ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ticket encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido o ticket no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        if (!esAdmin()) return accesoDenegado();
        try {
            return ResponseEntity.ok(ticketService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
        summary = "Crear un nuevo ticket",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ticket creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Ticket ticket) {
        if (!esAdmin()) return accesoDenegado();
        try {
            return ResponseEntity.ok(ticketService.crear(ticket));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
        summary = "Actualizar un ticket existente",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ticket actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o ticket inexistente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Ticket ticket) {
        if (!esAdmin()) return accesoDenegado();
        try {
            return ResponseEntity.ok(ticketService.actualizar(id, ticket));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
        summary = "Eliminar un ticket",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ticket eliminado correctamente"),
            @ApiResponse(responseCode = "400", description = "ID inválido o ticket inexistente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!esAdmin()) return accesoDenegado();
        try {
            ticketService.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private boolean esAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private ResponseEntity<String> accesoDenegado() {
        return ResponseEntity.status(403).body("Acceso denegado: Solo el administrador puede usar este recurso.");
    }
}