package com.example.tickets.Controller;

import com.example.tickets.config.SecurityConfig;
import com.example.tickets.controller.MensajeController;
import com.example.tickets.model.Mensaje;
import com.example.tickets.service.MensajeService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(MensajeController.class)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
public class MensajeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MensajeService mensajeService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void listarTodos_conAdmin_retornaOK() throws Exception {
        Mensaje m1 = new Mensaje();
        m1.setIdMensaje(1L);
        m1.setMensaje("Mensaje A");
        Mensaje m2 = new Mensaje();
        m2.setIdMensaje(2L);
        m2.setMensaje("Mensaje B");
        Mockito.when(mensajeService.obtenerTodos()).thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/api/v1/mensajes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idMensaje").value(1))
                .andExpect(jsonPath("$[1].mensaje").value("Mensaje B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void listarTodos_conUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/v1/mensajes"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void obtenerPorId_conAdmin_yExistente_retornaOK() throws Exception {
        Mensaje mensaje = new Mensaje();
        mensaje.setIdMensaje(1L);
        mensaje.setMensaje("Mensaje X");
        Mockito.when(mensajeService.obtenerPorId(1L)).thenReturn(mensaje);

        mockMvc.perform(get("/api/v1/mensajes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMensaje").value(1))
                .andExpect(jsonPath("$.mensaje").value("Mensaje X"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void obtenerPorId_conAdmin_yError_retorna400() throws Exception {
        Mockito.when(mensajeService.obtenerPorId(1L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/v1/mensajes/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void obtenerPorId_conUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/v1/mensajes/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void crear_conAdmin_retornaOK() throws Exception {
        Mensaje mensaje = new Mensaje();
        mensaje.setIdMensaje(1L);
        mensaje.setMensaje("Nuevo");
        Mockito.when(mensajeService.crear(any())).thenReturn(mensaje);

        mockMvc.perform(post("/api/v1/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idMensaje\":1, \"mensaje\":\"Nuevo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Nuevo"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void crear_conAdmin_yError_retorna400() throws Exception {
        Mockito.when(mensajeService.crear(any())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/v1/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idMensaje\":1, \"mensaje\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void crear_conUser_retorna403() throws Exception {
        mockMvc.perform(post("/api/v1/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idMensaje\":1, \"mensaje\":\"x\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void actualizar_conAdmin_retornaOK() throws Exception {
        Mensaje mensaje = new Mensaje();
        mensaje.setIdMensaje(1L);
        mensaje.setMensaje("Actualizado");
        Mockito.when(mensajeService.actualizar(eq(1L), any())).thenReturn(mensaje);

        mockMvc.perform(put("/api/v1/mensajes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idMensaje\":1, \"mensaje\":\"Actualizado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Actualizado"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void actualizar_conAdmin_yError_retorna400() throws Exception {
        Mockito.when(mensajeService.actualizar(eq(1L), any())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(put("/api/v1/mensajes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idMensaje\":1, \"mensaje\":\"Error\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void actualizar_conUser_retorna403() throws Exception {
        mockMvc.perform(put("/api/v1/mensajes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idMensaje\":1, \"mensaje\":\"X\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void eliminar_conAdmin_retornaOK() throws Exception {
        Mockito.doNothing().when(mensajeService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/mensajes/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void eliminar_conAdmin_yError_retorna400() throws Exception {
        Mockito.doThrow(new RuntimeException("Error")).when(mensajeService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/mensajes/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void eliminar_conUser_retorna403() throws Exception {
        mockMvc.perform(delete("/api/v1/mensajes/1"))
                .andExpect(status().isForbidden());
    }
}

