package com.example.direccion.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doNothing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

import com.example.direccion.model.Comuna;
import com.example.direccion.model.Direccion;
import com.example.direccion.model.Region;
import com.example.direccion.service.DireccionService;

@WebMvcTest(DireccionController.class)
class DireccionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DireccionService direccionService;

    private Direccion direccion;
    private Comuna comuna;
    private Region region;

    @BeforeEach
    void setUp() {
        region = new Region(1L, "Metropolitana");
        comuna = new Comuna(1L, "Providencia", region);
        direccion = new Direccion(1L, 12L, comuna, region, "Av. Providencia", "1234", "Dpto. 202", "8320000");
    }

    @Test
    void testListarTodas() throws Exception {
        when(direccionService.obtenerTodas()).thenReturn(List.of(direccion));

        MvcResult result = mockMvc.perform(get("/api/v1/direcciones"))
            .andExpect(status().isOk())
            .andReturn();

        String json = result.getResponse().getContentAsString();
        assertThat(json).contains("Lista de direcciones obtenida correctamente");
    }

    @Test
    void testObtenerPorId() throws Exception {
        when(direccionService.obtenerPorId(1L)).thenReturn(direccion);

        MvcResult result = mockMvc.perform(get("/api/v1/direcciones/1"))
            .andExpect(status().isOk())
            .andReturn();

        String json = result.getResponse().getContentAsString();
        assertThat(json).contains("Dirección obtenida correctamente");
    }

    @Test
    void testCrearDireccion() throws Exception {
        when(direccionService.crearDireccion(any(Direccion.class))).thenReturn(direccion);

        ObjectMapper mapper = new ObjectMapper();
        String direccionJson = mapper.writeValueAsString(direccion);

        MvcResult result = mockMvc.perform(post("/api/v1/direcciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(direccionJson))
            .andExpect(status().isCreated())
            .andReturn();

        String json = result.getResponse().getContentAsString();
        assertThat(json).contains("Dirección creada correctamente");
    }

    @Test
    void testActualizarDireccion() throws Exception {
        when(direccionService.actualizarDireccion(eq(1L), any(Direccion.class))).thenReturn(direccion);

        ObjectMapper mapper = new ObjectMapper();
        String direccionJson = mapper.writeValueAsString(direccion);

        MvcResult result = mockMvc.perform(put("/api/v1/direcciones/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(direccionJson))
            .andExpect(status().isOk())
            .andReturn();

        String json = result.getResponse().getContentAsString();
        assertThat(json).contains("Dirección actualizada correctamente");
    }

    @Test
    void testEliminarDireccion() throws Exception {
        doNothing().when(direccionService).eliminarDireccion(1L);

        mockMvc.perform(delete("/api/v1/direcciones/1"))
            .andExpect(status().isNoContent());
    }
}