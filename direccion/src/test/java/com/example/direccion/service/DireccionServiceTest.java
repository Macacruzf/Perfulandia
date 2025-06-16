package com.example.direccion.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.*;

import com.example.direccion.model.Comuna;
import com.example.direccion.model.Direccion;
import com.example.direccion.model.Region;
import com.example.direccion.Repository.ComunaRepository;
import com.example.direccion.Repository.DireccionRepository;
import com.example.direccion.Repository.RegionRepository;
import com.example.direccion.Service.DireccionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DireccionServiceTest {

    @Mock
    private DireccionRepository direccionRepository;

    @Mock
    private ComunaRepository comunaRepository;

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private DireccionService direccionService;

    private Comuna comuna;
    private Region region;
    private Direccion direccion;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        region = new Region();
        region.setIdRegion(1L);
        region.setNombre("Región Metropolitana");

        comuna = new Comuna();
        comuna.setIdComuna(1L);
        comuna.setNombre("Santiago");
        comuna.setRegion(region);

        direccion = new Direccion();
        direccion.setIdDireccion(1L);
        direccion.setIdUsuario(100L);
        direccion.setComuna(comuna);
        direccion.setRegion(region);
        direccion.setCalle("Av. Siempre Viva");
        direccion.setNumero("742");
        direccion.setDepartamento("Apt 1");
        direccion.setCodigoPostal("1234567");
    }

    @Test
    void crearDireccion_conComunaValida_deberiaGuardarDireccion() {
        when(comunaRepository.findById(1L)).thenReturn(Optional.of(comuna));
        when(direccionRepository.save(any(Direccion.class))).thenReturn(direccion);

        Direccion resultado = direccionService.crearDireccion(direccion);

        assertNotNull(resultado);
        assertEquals("Santiago", resultado.getComuna().getNombre());
        verify(direccionRepository, times(1)).save(direccion);
    }

    @Test
    void crearDireccion_conComunaInvalida_deberiaLanzarExcepcion() {
        direccion.setComuna(new Comuna());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            direccionService.crearDireccion(direccion);
        });

        assertTrue(exception.getMessage().contains("Debe especificar una comuna"));
    }

    @Test
    void obtenerTodas_deberiaRetornarLista() {
        List<Direccion> lista = Arrays.asList(direccion);
        when(direccionRepository.findAll()).thenReturn(lista);

        List<Direccion> resultado = direccionService.obtenerTodas();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorId_existente_deberiaRetornarDireccion() {
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion));

        Direccion resultado = direccionService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("Av. Siempre Viva", resultado.getCalle());
    }

    @Test
    void obtenerPorId_noExistente_deberiaLanzarExcepcion() {
        when(direccionRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            direccionService.obtenerPorId(2L);
        });

        assertTrue(exception.getMessage().contains("No se encontr"));
    }
}

