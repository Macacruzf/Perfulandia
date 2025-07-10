package com.example.tickets.Controller;

import com.example.tickets.config.SecurityConfig;
import com.example.tickets.controller.MotivoController;
import com.example.tickets.model.Motivo;
import com.example.tickets.service.MotivoService;

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

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(MotivoController.class)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
public class MotivoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MotivoService motivoService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void listarTodos_conAdmin_retornaOK() throws Exception {
        Motivo m1 = new Motivo(1L, "Motivo A");
        Motivo m2 = new Motivo(2L, "Motivo B");
        Mockito.when(motivoService.obtenerTodos()).thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/api/v1/motivos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idMotivo").value(1))
                .andExpect(jsonPath("$[1].nombre").value("Motivo B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void listarTodos_conUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/v1/motivos"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void obtenerPorId_conAdmin_yExistente_retornaOK() throws Exception {
        Motivo motivo = new Motivo(1L, "Motivo X");
        Mockito.when(motivoService.obtenerPorId(1L)).thenReturn(motivo);

        mockMvc.perform(get("/api/v1/motivos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMotivo").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void obtenerPorId_conAdmin_yError_retorna400() throws Exception {
        Mockito.when(motivoService.obtenerPorId(1L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/v1/motivos/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void obtenerPorId_conUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/v1/motivos/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void crear_conAdmin_retornaOK() throws Exception {
        Motivo motivo = new Motivo(1L, "Nuevo");
        Mockito.when(motivoService.crear(any())).thenReturn(motivo);

        mockMvc.perform(post("/api/v1/motivos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idMotivo\":1, \"nombre\":\"Nuevo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nuevo"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void crear_conAdmin_yError_retorna400() throws Exception {
        Mockito.when(motivoService.crear(any())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/v1/motivos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idMotivo\":1, \"nombre\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void crear_conUser_retorna403() throws Exception {
        mockMvc.perform(post("/api/v1/motivos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idMotivo\":1, \"nombre\":\"x\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void actualizar_conAdmin_retornaOK() throws Exception {
        Motivo motivo = new Motivo(1L, "Actualizado");
        Mockito.when(motivoService.actualizar(eq(1L), any())).thenReturn(motivo);

        mockMvc.perform(put("/api/v1/motivos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idMotivo\":1, \"nombre\":\"Actualizado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Actualizado"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void actualizar_conAdmin_yError_retorna400() throws Exception {
        Mockito.when(motivoService.actualizar(eq(1L), any())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(put("/api/v1/motivos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idMotivo\":1, \"nombre\":\"Error\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void actualizar_conUser_retorna403() throws Exception {
        mockMvc.perform(put("/api/v1/motivos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idMotivo\":1, \"nombre\":\"X\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void eliminar_conAdmin_retornaOK() throws Exception {
        Mockito.doNothing().when(motivoService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/motivos/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void eliminar_conAdmin_yError_retorna400() throws Exception {
        Mockito.doThrow(new RuntimeException("Error")).when(motivoService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/motivos/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void eliminar_conUser_retorna403() throws Exception {
        mockMvc.perform(delete("/api/v1/motivos/1"))
                .andExpect(status().isForbidden());
    }
}
