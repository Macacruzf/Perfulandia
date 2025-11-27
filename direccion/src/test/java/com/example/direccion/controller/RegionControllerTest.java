package com.example.direccion.controller;

import com.example.direccion.controller.RegionController;
import com.example.direccion.model.Region;
import com.example.direccion.service.RegionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(RegionController.class)
class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegionService regionService;

    private Region region;

    @BeforeEach
    void setUp() {
        region = new Region(1L, "Región Metropolitana");
    }

    @Test
    void testListarRegiones() throws Exception {
        when(regionService.obtenerTodas()).thenReturn(List.of(region));

        MvcResult result = mockMvc.perform(get("/api/v1/regiones"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        assertThat(json).contains("Lista de regiones obtenida exitosamente");
        assertThat(json).contains("Región Metropolitana");
    }

    @Test
    void testObtenerRegionPorId() throws Exception {
        when(regionService.obtenerPorId(1L)).thenReturn(region);

        MvcResult result = mockMvc.perform(get("/api/v1/regiones/1"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        assertThat(json).contains("Región encontrada");
        assertThat(json).contains("Región Metropolitana");
    }

    @Test
    void testCrearRegion() throws Exception {
        when(regionService.crear(any(Region.class))).thenReturn(region);

        ObjectMapper mapper = new ObjectMapper();
        String jsonBody = mapper.writeValueAsString(region);

        MvcResult result = mockMvc.perform(post("/api/v1/regiones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        assertThat(json).contains("Región creada exitosamente");
    }

    @Test
    void testActualizarRegion() throws Exception {
        when(regionService.actualizar(eq(1L), any(Region.class))).thenReturn(region);

        ObjectMapper mapper = new ObjectMapper();
        String jsonBody = mapper.writeValueAsString(region);

        MvcResult result = mockMvc.perform(put("/api/v1/regiones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        assertThat(json).contains("Región actualizada exitosamente");
    }

    @Test
    void testEliminarRegion() throws Exception {
        doNothing().when(regionService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/regiones/1"))
                .andExpect(status().isNoContent());
    }
}
