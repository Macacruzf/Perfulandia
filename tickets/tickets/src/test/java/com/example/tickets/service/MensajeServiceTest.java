package com.example.tickets.service;

import com.example.tickets.model.Mensaje;
import com.example.tickets.model.Ticket;
import com.example.tickets.repository.MensajeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

public class MensajeServiceTest {

    @Mock
    private MensajeRepository mensajeRepository;

    @InjectMocks
    private MensajeService mensajeService;

    private Mensaje mensaje;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Ticket ticket = new Ticket();
        ticket.setIdTicket(1L);

        mensaje = new Mensaje();
        mensaje.setIdMensaje(1L);
        mensaje.setMensaje("Mensaje de prueba");
        mensaje.setFechaMensaje(LocalDateTime.now());
        mensaje.setTipoMensaje("info");
        mensaje.setIdUsuario(5L);
        mensaje.setTicket(ticket);
    }

    @Test
    void testObtenerTodos() {
        when(mensajeRepository.findAll()).thenReturn(Arrays.asList(mensaje));

        List<Mensaje> result = mensajeService.obtenerTodos();
        assertEquals(1, result.size());
        verify(mensajeRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPorIdExistente() {
        when(mensajeRepository.findById(1L)).thenReturn(Optional.of(mensaje));

        Mensaje result = mensajeService.obtenerPorId(1L);
        assertEquals("Mensaje de prueba", result.getMensaje());
    }

    @Test
    void testObtenerPorIdNoExistente() {
        when(mensajeRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mensajeService.obtenerPorId(99L));
        assertEquals("No se encontró el mensaje con ID: 99", exception.getMessage());
    }

    @Test
    void testCrear() {
        when(mensajeRepository.save(mensaje)).thenReturn(mensaje);

        Mensaje result = mensajeService.crear(mensaje);
        assertNotNull(result);
        assertEquals("Mensaje de prueba", result.getMensaje());
    }

    @Test
    void testActualizar() {
        Mensaje actualizado = new Mensaje();
        actualizado.setMensaje("Mensaje actualizado");
        actualizado.setFechaMensaje(LocalDateTime.now());
        actualizado.setTipoMensaje("alerta");
        actualizado.setIdUsuario(6L);
        actualizado.setTicket(mensaje.getTicket());

        when(mensajeRepository.findById(1L)).thenReturn(Optional.of(mensaje));
        when(mensajeRepository.save(any(Mensaje.class))).thenReturn(mensaje);

        Mensaje result = mensajeService.actualizar(1L, actualizado);
        assertEquals("Mensaje actualizado", result.getMensaje());
    }

    @Test
    void testEliminarExistente() {
        when(mensajeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(mensajeRepository).deleteById(1L);

        assertDoesNotThrow(() -> mensajeService.eliminar(1L));
        verify(mensajeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarNoExistente() {
        when(mensajeRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mensajeService.eliminar(99L));
        assertEquals("No existe el mensaje con ID: 99", exception.getMessage());
    }
}
