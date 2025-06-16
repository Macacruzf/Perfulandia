package com.example.direccion.controller;

import com.example.direccion.Controller.ComunaController;
import com.example.direccion.Service.ComunaService;
import com.example.direccion.model.Comuna;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ComunaController.class)
public class ComunaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComunaService comunaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Principal adminPrincipal;
    private Principal userPrincipal;

    @BeforeEach
    void setup() {
        adminPrincipal = () -> "admin";
        userPrincipal = () -> "user";
    }

    @Test
    void testListarComunas_Admin() throws Exception {
        when(comunaService.obtenerTodas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/comunas").principal(adminPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void testListarComunas_NoAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/comunas").principal(userPrincipal))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Acceso denegado: Solo el administrador puede usar este recurso."));
    }

    @Test
    void testObtenerComunaPorId() throws Exception {
        Comuna comuna = new Comuna();
        comuna.setIdComuna(1L);
        comuna.setNombre("Prueba");

        when(comunaService.obtenerPorId(1L)).thenReturn(comuna);

        mockMvc.perform(get("/api/v1/comunas/1").principal(adminPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Prueba"));
    }

    @Test
    void testCrearComuna() throws Exception {
        Comuna comuna = new Comuna();
        comuna.setNombre("Nueva Comuna");

        when(comunaService.crear(any(Comuna.class))).thenReturn(comuna);

        mockMvc.perform(post("/api/v1/comunas")
                .principal(adminPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comuna)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nueva Comuna"));
    }

    @Test
    void testActualizarComuna() throws Exception {
        Comuna comuna = new Comuna();
        comuna.setNombre("Comuna Actualizada");

        when(comunaService.actualizar(eq(1L), any(Comuna.class))).thenReturn(comuna);

        mockMvc.perform(put("/api/v1/comunas/1")
                .principal(adminPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comuna)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Comuna Actualizada"));
    }

    @Test
    void testEliminarComuna() throws Exception {
        doNothing().when(comunaService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/comunas/1").principal(adminPrincipal))
                .andExpect(status().isOk());
    }
}
