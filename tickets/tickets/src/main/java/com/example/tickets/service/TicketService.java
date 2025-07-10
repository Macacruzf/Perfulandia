package com.example.tickets.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tickets.model.Ticket;
import com.example.tickets.repository.TicketRepository;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public List<Ticket> obtenerTodos() {
        return ticketRepository.findAll();
    }

    public Ticket obtenerPorId(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el ticket con ID: " + id));
    }

    public Ticket crear(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public Ticket actualizar(Long id, Ticket actualizado) {
        Ticket existente = obtenerPorId(id);

        existente.setComentario(actualizado.getComentario());
        existente.setFechaCreacion(actualizado.getFechaCreacion());
        existente.setIdUsuario(actualizado.getIdUsuario());
        existente.setMotivo(actualizado.getMotivo());

        return ticketRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("No existe el ticket con ID: " + id);
        }
        ticketRepository.deleteById(id);
    }
}