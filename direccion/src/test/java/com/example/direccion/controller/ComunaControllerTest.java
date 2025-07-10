package com.example.direccion.controller;

import com.example.direccion.model.Comuna;
import com.example.direccion.model.Region;
import com.example.direccion.service.ComunaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(ComunaController.class)
public class ComunaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComunaService comunaService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Region region = new Region(1L, "Región Metropolitana");

    @Test
    public void testListarComunas() throws Exception {
        List<Comuna> comunas = Arrays.asList(
                new Comuna(1L, "Santiago", region),
                new Comuna(2L, "Providencia", region)
        );

        when(comunaService.obtenerTodas()).thenReturn(comunas);

        mockMvc.perform(get("/api/v1/comunas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Lista de comunas obtenida correctamente"))
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    @Test
    public void testObtenerComunaPorId() throws Exception {
        Comuna comuna = new Comuna(1L, "Santiago", region);
        when(comunaService.obtenerPorId(1L)).thenReturn(comuna);

        mockMvc.perform(get("/api/v1/comunas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Comuna obtenida correctamente"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    public void testCrearComuna() throws Exception {
        Comuna comuna = new Comuna(null, "Ñuñoa", region);
        Comuna creada = new Comuna(3L, "Ñuñoa", region);

        when(comunaService.crear(any(Comuna.class))).thenReturn(creada);

        mockMvc.perform(post("/api/v1/comunas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comuna)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Comuna creada correctamente"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    public void testActualizarComuna() throws Exception {
        Comuna actualizada = new Comuna(1L, "Las Condes", region);

        when(comunaService.actualizar(eq(1L), any(Comuna.class))).thenReturn(actualizada);

        mockMvc.perform(put("/api/v1/comunas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Comuna actualizada correctamente"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    public void testEliminarComuna() throws Exception {
        doNothing().when(comunaService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/comunas/1"))
                .andExpect(status().isNoContent());
    }
}

