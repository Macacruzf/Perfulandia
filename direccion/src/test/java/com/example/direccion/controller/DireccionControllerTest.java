package com.example.direccion.controller;


import com.example.direccion.Controller.DireccionController;
import com.example.direccion.Service.DireccionService;
import com.example.direccion.model.Direccion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doNothing;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(DireccionController.class)
public class DireccionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DireccionService direccionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private Principal adminPrincipal;
    private Principal userPrincipal;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        adminPrincipal = () -> "admin";
        userPrincipal = () -> "usuario";
    }

    @Test
    public void testListarTodas_Success() throws Exception {
        when(direccionService.obtenerTodas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/direcciones").principal(adminPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void testObtenerPorId_Success() throws Exception {
        Direccion direccion = new Direccion();
        direccion.setIdDireccion(1L);
        direccion.setCalle("Calle 1");

        when(direccionService.obtenerPorId(1L)).thenReturn(direccion);

        mockMvc.perform(get("/api/v1/direcciones/1").principal(adminPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calle").value("Calle 1"));
    }

    @Test
    public void testListarPorComuna_Success() throws Exception {
        Direccion direccion = new Direccion();
        direccion.setCalle("Otra Calle");

        when(direccionService.obtenerPorComuna(5L)).thenReturn(List.of(direccion));

        mockMvc.perform(get("/api/v1/direcciones/comuna/5").principal(adminPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].calle").value("Otra Calle"));
    }

    @Test
    public void testCrearDireccion_Success() throws Exception {
        Direccion direccion = new Direccion();
        direccion.setCalle("Nueva Calle");

        when(direccionService.crearDireccion(any(Direccion.class))).thenReturn(direccion);

        mockMvc.perform(post("/api/v1/direcciones")
                        .principal(adminPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(direccion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calle").value("Nueva Calle"));
    }

    @Test
    public void testActualizarDireccion_Success() throws Exception {
        Direccion direccion = new Direccion();
        direccion.setCalle("Actualizada");

        when(direccionService.actualizarDireccion(eq(1L), any(Direccion.class))).thenReturn(direccion);

        mockMvc.perform(put("/api/v1/direcciones/1")
                        .principal(adminPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(direccion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calle").value("Actualizada"));
    }

    @Test
    public void testEliminarDireccion_Success() throws Exception {
        doNothing().when(direccionService).eliminarDireccion(1L);

        mockMvc.perform(delete("/api/v1/direcciones/1").principal(adminPrincipal))
                .andExpect(status().isOk());
    }

    @Test
    public void testAccesoDenegado() throws Exception {
        mockMvc.perform(get("/api/v1/direcciones").principal(userPrincipal))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Acceso denegado: Solo el administrador puede usar este recurso."));
    }

    @Test
    public void testErrorObtenerPorId() throws Exception {
        when(direccionService.obtenerPorId(99L)).thenThrow(new RuntimeException("Error controlado"));

        mockMvc.perform(get("/api/v1/direcciones/99").principal(adminPrincipal))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error controlado"));
    }
}