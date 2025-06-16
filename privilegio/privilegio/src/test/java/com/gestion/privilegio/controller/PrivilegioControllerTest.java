package com.gestion.privilegio.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.util.Arrays;
import java.util.List;

import com.gestion.privilegio.model.Modulo;
import com.gestion.privilegio.model.Privilegio;
import com.gestion.privilegio.service.ModuloService;
import com.gestion.privilegio.service.PrivilegioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(PrivilegioController.class)
public class PrivilegioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Creamos mocks de los servicios que usa el controlador
    @MockBean
    private PrivilegioService privilegioService;

    @MockBean
    private ModuloService moduloService;

    private Privilegio privilegio;
    private Modulo modulo;

    @BeforeEach
    void setUp() {
        modulo = new Modulo();
        modulo.setIdModulo(1L);
        modulo.setNombre("productos");

        privilegio = new Privilegio();
        privilegio.setIdPrivilegio(1L);
        privilegio.setNombreRol("CLIENTE");
        privilegio.setModulo(modulo);
    }

    @Test
    void testListarPrivilegios() throws Exception {
        List<Privilegio> lista = Arrays.asList(privilegio);

        when(privilegioService.listarTodos()).thenReturn(lista);

        mockMvc.perform(get("/api/v1/privilegios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testListarModulos() throws Exception {
        List<Modulo> modulos = Arrays.asList(modulo);

        when(moduloService.obtenerTodosLosModulos()).thenReturn(modulos);

        mockMvc.perform(get("/api/v1/privilegios/modulos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testObtenerModulosPorRol() throws Exception {
        when(privilegioService.obtenerModulosPorRol("CLIENTE"))
                .thenReturn(Arrays.asList(modulo));

        mockMvc.perform(get("/api/v1/privilegios/rol/CLIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testGuardarPrivilegio() throws Exception {
        when(privilegioService.guardar(any(Privilegio.class))).thenReturn(privilegio);

        mockMvc.perform(post("/api/v1/privilegios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombreRol": "CLIENTE",
                                    "modulo": {
                                        "idModulo": 1,
                                        "nombre": "productos"
                                    }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreRol").value("CLIENTE"));
    }

    @Test
    void testEliminarPrivilegio() throws Exception {
        mockMvc.perform(delete("/api/v1/privilegios/1"))
                .andExpect(status().isNoContent());

        verify(privilegioService, times(1)).eliminar(1L);
    }
}
