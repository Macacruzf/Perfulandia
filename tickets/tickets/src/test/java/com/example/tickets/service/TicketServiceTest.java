package com.example.tickets.service;

import com.example.tickets.model.Motivo;
import com.example.tickets.model.Ticket;
import com.example.tickets.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;


public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Motivo motivo = new Motivo();
        motivo.setIdMotivo(1L);
        motivo.setNombre("Soporte");

        ticket = new Ticket();
        ticket.setIdTicket(1L);
        ticket.setComentario("Problema con impresora");
        ticket.setFechaCreacion(LocalDateTime.now());
        ticket.setIdUsuario(2L);
        ticket.setMotivo(motivo);
    }

    @Test
    void testObtenerTodos() {
        when(ticketRepository.findAll()).thenReturn(Arrays.asList(ticket));

        List<Ticket> result = ticketService.obtenerTodos();
        assertEquals(1, result.size());
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPorIdExistente() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        Ticket result = ticketService.obtenerPorId(1L);
        assertEquals("Problema con impresora", result.getComentario());
    }

    @Test
    void testObtenerPorIdNoExistente() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> ticketService.obtenerPorId(99L));
        assertEquals("No se encontró el ticket con ID: 99", exception.getMessage());
    }

    @Test
    void testCrear() {
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        Ticket result = ticketService.crear(ticket);
        assertNotNull(result);
        assertEquals("Problema con impresora", result.getComentario());
    }

    @Test
    void testActualizar() {
        Ticket actualizado = new Ticket();
        actualizado.setComentario("Nuevo comentario");
        actualizado.setFechaCreacion(LocalDateTime.now());
        actualizado.setIdUsuario(3L);
        actualizado.setMotivo(ticket.getMotivo());

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket result = ticketService.actualizar(1L, actualizado);
        assertEquals("Nuevo comentario", result.getComentario());
    }

    @Test
    void testEliminarExistente() {
        when(ticketRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ticketRepository).deleteById(1L);

        assertDoesNotThrow(() -> ticketService.eliminar(1L));
        verify(ticketRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarNoExistente() {
        when(ticketRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> ticketService.eliminar(99L));
        assertEquals("No existe el ticket con ID: 99", exception.getMessage());
    }
}
