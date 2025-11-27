package com.example.tickets.Controller;

import com.example.tickets.config.SecurityConfig;
import com.example.tickets.controller.TicketController;
import com.example.tickets.model.Motivo;
import com.example.tickets.model.Ticket;
import com.example.tickets.service.TicketService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TicketController.class)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void listarTodos_conAdmin_retornaOK() throws Exception {
        Mockito.when(ticketService.obtenerTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/tickets"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void obtenerPorId_conAdmin_yExistente_retornaOK() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setIdTicket(1L);
        ticket.setComentario("Test");

        Mockito.when(ticketService.obtenerPorId(1L)).thenReturn(ticket);

        mockMvc.perform(get("/api/v1/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTicket").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void obtenerPorId_conAdmin_yError_retorna400() throws Exception {
        Mockito.when(ticketService.obtenerPorId(1L)).thenThrow(new RuntimeException("No encontrado"));

        mockMvc.perform(get("/api/v1/tickets/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No encontrado"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void crear_conAdmin_retornaOK() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setComentario("Nuevo ticket");
        ticket.setFechaCreacion(LocalDateTime.now());
        ticket.setIdUsuario(1L);
        ticket.setMotivo(new Motivo(1L, "Motivo X"));

        Mockito.when(ticketService.crear(any())).thenReturn(ticket);

        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comentario\":\"Nuevo ticket\", \"idUsuario\":1, \"fechaCreacion\":\"2024-01-01T00:00:00\", \"motivo\":{\"idMotivo\":1, \"nombre\":\"Motivo X\"}}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void crear_conAdmin_yError_retorna400() throws Exception {
        Mockito.when(ticketService.crear(any())).thenThrow(new RuntimeException("Error al crear"));

        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comentario\":\"Error\", \"idUsuario\":1, \"fechaCreacion\":\"2024-01-01T00:00:00\", \"motivo\":{\"idMotivo\":1, \"nombre\":\"X\"}}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al crear"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void actualizar_conAdmin_retornaOK() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setComentario("Actualizado");
        ticket.setFechaCreacion(LocalDateTime.now());
        ticket.setIdUsuario(1L);
        ticket.setMotivo(new Motivo(1L, "Motivo X"));

        Mockito.when(ticketService.actualizar(eq(1L), any())).thenReturn(ticket);

        mockMvc.perform(put("/api/v1/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comentario\":\"Actualizado\", \"idUsuario\":1, \"fechaCreacion\":\"2024-01-01T00:00:00\", \"motivo\":{\"idMotivo\":1, \"nombre\":\"Motivo X\"}}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void actualizar_conAdmin_yError_retorna400() throws Exception {
        Mockito.when(ticketService.actualizar(eq(1L), any())).thenThrow(new RuntimeException("Error al actualizar"));

        mockMvc.perform(put("/api/v1/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comentario\":\"x\", \"idUsuario\":1, \"fechaCreacion\":\"2024-01-01T00:00:00\", \"motivo\":{\"idMotivo\":1, \"nombre\":\"x\"}}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al actualizar"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void eliminar_conAdmin_retornaOK() throws Exception {
        Mockito.doNothing().when(ticketService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/tickets/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void eliminar_conAdmin_yError_retorna400() throws Exception {
        Mockito.doThrow(new RuntimeException("Error al eliminar")).when(ticketService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/tickets/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al eliminar"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void listarTodos_conUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/v1/tickets"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void obtenerPorId_conUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/v1/tickets/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void crear_conUser_retorna403() throws Exception {
        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comentario\":\"Error\", \"idUsuario\":1, \"fechaCreacion\":\"2024-01-01T00:00:00\", \"motivo\":{\"idMotivo\":1, \"nombre\":\"X\"}}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void actualizar_conUser_retorna403() throws Exception {
        mockMvc.perform(put("/api/v1/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comentario\":\"x\", \"idUsuario\":1, \"fechaCreacion\":\"2024-01-01T00:00:00\", \"motivo\":{\"idMotivo\":1, \"nombre\":\"x\"}}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void eliminar_conUser_retorna403() throws Exception {
        mockMvc.perform(delete("/api/v1/tickets/1"))
                .andExpect(status().isForbidden());
    }
}

