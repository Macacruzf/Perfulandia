package com.example.tickets.controller;

import java.util.List;

import com.example.direccion.model.Ticket;
import com.example.direccion.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<Ticket>> listarTickets(Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();
        return ResponseEntity.ok(ticketService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerTicket(@PathVariable Long id, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            return ResponseEntity.ok(ticketService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crearTicket(@Valid @RequestBody Ticket ticket, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            return ResponseEntity.ok(ticketService.crear(ticket));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTicket(@PathVariable Long id, @Valid @RequestBody Ticket ticket, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            return ResponseEntity.ok(ticketService.actualizar(id, ticket));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTicket(@PathVariable Long id, Principal principal) {
        if (!esAdmin(principal)) return accesoDenegado();

        try {
            ticketService.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private boolean esAdmin(Principal principal) {
        return principal != null && "admin".equals(principal.getName());
    }

    private ResponseEntity<String> accesoDenegado() {
        return ResponseEntity.status(403).body("Acceso denegado: Solo el administrador puede usar este recurso.");
    }
}