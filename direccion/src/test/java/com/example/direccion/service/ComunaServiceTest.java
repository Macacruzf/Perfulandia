package com.example.direccion.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.direccion.Repository.ComunaRepository;
import com.example.direccion.Repository.RegionRepository;
import com.example.direccion.model.Comuna;
import com.example.direccion.model.Region;
import com.example.direccion.service.ComunaService;

import java.util.Optional;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;

public class ComunaServiceTest {

    @Mock
    private ComunaRepository comunaRepository;

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private ComunaService comunaService;

    private Comuna comuna;
    private Region region;

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
    }

    @Test
    void testObtenerTodas() {
        when(comunaRepository.findAll()).thenReturn(Collections.singletonList(comuna));

        List<Comuna> resultado = comunaService.obtenerTodas();

        assertEquals(1, resultado.size());
        verify(comunaRepository).findAll();
    }

    @Test
    void testObtenerPorId_Existente() {
        when(comunaRepository.findById(1L)).thenReturn(Optional.of(comuna));

        Comuna resultado = comunaService.obtenerPorId(1L);

        assertEquals("Santiago", resultado.getNombre());
        verify(comunaRepository).findById(1L);
    }

    @Test
    void testObtenerPorId_NoExistente() {
        when(comunaRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> comunaService.obtenerPorId(2L));
        assertEquals("No se encontró la comuna con ID: 2", ex.getMessage());
    }

    @Test
    void testCrearComuna_Exito() {
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));
        when(comunaRepository.save(any(Comuna.class))).thenReturn(comuna);

        Comuna creada = comunaService.crear(comuna);

        assertNotNull(creada);
        assertEquals("Santiago", creada.getNombre());
        verify(comunaRepository).save(comuna);
    }

    @Test
    void testCrearComuna_RegionInvalida() {
        comuna.getRegion().setIdRegion(99L);
        when(regionRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> comunaService.crear(comuna));
        assertEquals("No existe región con ID: 99", ex.getMessage());
    }

    @Test
    void testActualizarComuna_Exito() {
        Comuna nueva = new Comuna();
        nueva.setNombre("Providencia");
        nueva.setRegion(region);

        when(comunaRepository.findById(1L)).thenReturn(Optional.of(comuna));
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));
        when(comunaRepository.save(any(Comuna.class))).thenReturn(comuna);

        Comuna actualizada = comunaService.actualizar(1L, nueva);

        assertEquals("Providencia", actualizada.getNombre());
        verify(comunaRepository).save(comuna);
    }

    @Test
    void testEliminarComuna_Existe() {
        when(comunaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(comunaRepository).deleteById(1L);

        comunaService.eliminar(1L);

        verify(comunaRepository).deleteById(1L);
    }

    @Test
    void testEliminarComuna_NoExiste() {
        when(comunaRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> comunaService.eliminar(99L));
        assertEquals("No existe comuna con ID: 99", ex.getMessage());
    }
}
