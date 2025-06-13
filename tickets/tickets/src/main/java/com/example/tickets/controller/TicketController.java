package com.example.tickets.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tickets.model.Mensaje;
import com.example.tickets.model.Ticket;
import com.example.tickets.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping
    public Ticket crearTicket(@RequestBody Ticket ticket) {
        return ticketService.crearTicket(ticket);
    }

    @GetMapping("/{id}")
    public Ticket obtenerTicket(@PathVariable Long id) {
        return ticketService.obtenerTicketPorId(id);
    }

    @GetMapping
    public List<Ticket> listarTickets() {
        return ticketService.listarTickets();
    }

    @PutMapping("/{id}/estado")
    public Ticket actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        return ticketService.actualizarEstado(id, estado);
    }

    @DeleteMapping("/{id}")
    public void eliminarTicket(@PathVariable Long id) {
        ticketService.eliminarTicket(id);
    }

    @PostMapping("/{id}/mensajes")
    public Mensaje agregarMensaje(@PathVariable Long id, @RequestBody Mensaje mensaje) {
        return ticketService.agregarMensaje(id, mensaje);
    }

    @GetMapping("/{id}/mensajes")
    public List<Mensaje> listarMensajesPorTicket(@PathVariable Long id) {
        return ticketService.listarMensajesPorTicket(id);
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<Ticket> listarTicketsPorUsuario(@PathVariable Long idUsuario) {
        return ticketService.listarTicketsPorUsuario(idUsuario);
    }
}