package com.example.direccion.controller;

import com.example.direccion.Controller.RegionController;
import com.example.direccion.Service.RegionService;
import com.example.direccion.model.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.Principal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(RegionController.class)
public class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegionService regionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Principal adminPrincipal;

    @BeforeEach
    public void setup() {
        adminPrincipal = () -> "admin";
    }

    @Test
    public void testListarRegiones_Success() throws Exception {
        Mockito.when(regionService.obtenerTodas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/regiones").principal(adminPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void testObtenerRegion_Success() throws Exception {
        Region region = new Region();
        region.setIdRegion(1L);
        region.setNombre("Valparaíso");

        Mockito.when(regionService.obtenerPorId(1L)).thenReturn(region);

        mockMvc.perform(get("/api/v1/regiones/1").principal(adminPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Valparaíso"));
    }

    @Test
    public void testCrearRegion_Success() throws Exception {
        Region region = new Region();
        region.setNombre("Metropolitana");

        Mockito.when(regionService.crear(any(Region.class))).thenReturn(region);

        mockMvc.perform(post("/api/v1/regiones")
                .principal(adminPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(region)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Metropolitana"));
    }

    @Test
    public void testActualizarRegion_Success() throws Exception {
        Region region = new Region();
        region.setNombre("Actualizada");

        Mockito.when(regionService.actualizar(eq(1L), any(Region.class))).thenReturn(region);

        mockMvc.perform(put("/api/v1/regiones/1")
                .principal(adminPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(region)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Actualizada"));
    }

    @Test
    public void testEliminarRegion_Success() throws Exception {
        doNothing().when(regionService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/regiones/1").principal(adminPrincipal))
                .andExpect(status().isOk());
    }

    @Test
    public void testAccesoDenegado() throws Exception {
        Principal noAdmin = () -> "usuario";

        mockMvc.perform(get("/api/v1/regiones").principal(noAdmin))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Acceso denegado: Solo el administrador puede usar este recurso."));
    }
}