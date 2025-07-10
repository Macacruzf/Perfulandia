package com.gestion.privilegio.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.privilegio.config.SecurityConfig;
import com.gestion.privilegio.model.Modulo;
import com.gestion.privilegio.model.Privilegio;
import com.gestion.privilegio.service.ModuloService;
import com.gestion.privilegio.service.PrivilegioService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PrivilegioController.class)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
class PrivilegioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PrivilegioService privilegioService;

    @MockBean
    private ModuloService moduloService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListarPrivilegios() throws Exception {
        Privilegio p = new Privilegio(1L, "ADMIN", new Modulo(1L, "Usuarios"));
        when(privilegioService.listarTodos()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/v1/privilegios"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("_embedded").exists());
    }

    @Test
    void testListarModulos() throws Exception {
        Modulo m = new Modulo(1L, "Usuarios");
        when(moduloService.obtenerTodosLosModulos()).thenReturn(List.of(m));

        mockMvc.perform(get("/api/v1/privilegios/modulos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("_embedded").exists());
    }

    @Test
    void testObtenerModulosPorRolConResultado() throws Exception {
        Modulo m = new Modulo(2L, "Ventas");
        when(privilegioService.obtenerModulosPorRol("ADMIN")).thenReturn(List.of(m));

        mockMvc.perform(get("/api/v1/privilegios/rol/ADMIN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nombre").value("Ventas"));
    }

    @Test
    void testObtenerModulosPorRolSinResultado() throws Exception {
        when(privilegioService.obtenerModulosPorRol("VACIO")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/privilegios/rol/VACIO"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testGuardarPrivilegio() throws Exception {
        Modulo modulo = new Modulo(1L, "Usuarios");
        Privilegio privilegio = new Privilegio(1L, "ADMIN", modulo);

        when(privilegioService.guardar(any())).thenReturn(privilegio);

        mockMvc.perform(post("/api/v1/privilegios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(privilegio)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombreRol").value("ADMIN"));
    }

    @Test
    void testEliminarPrivilegio() throws Exception {
        doNothing().when(privilegioService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/privilegios/1"))
            .andExpect(status().isNoContent());
    }
}
