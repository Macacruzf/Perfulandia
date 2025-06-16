package com.example.direccion.service;

import com.example.direccion.Repository.RegionRepository;
import com.example.direccion.Service.RegionService;
import com.example.direccion.model.Region;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RegionServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RegionService regionService;

    @Test
    void testObtenerTodas() {
        List<Region> lista = Arrays.asList(new Region(1L, "Región A"), new Region(2L, "Región B"));
        when(regionRepository.findAll()).thenReturn(lista);

        List<Region> resultado = regionService.obtenerTodas();

        assertEquals(2, resultado.size());
        verify(regionRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPorId_Existe() {
        Region region = new Region(1L, "Metropolitana");
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));

        Region resultado = regionService.obtenerPorId(1L);

        assertEquals("Metropolitana", resultado.getNombre());
        verify(regionRepository).findById(1L);
    }

    @Test
    void testObtenerPorId_NoExiste() {
        when(regionRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> regionService.obtenerPorId(1L));

        assertEquals("No existe región con ID: 1", ex.getMessage());
    }

    @Test
    void testCrear_Success() {
        Region region = new Region(null, "Valparaíso");

        when(regionRepository.save(region)).thenReturn(new Region(1L, "Valparaíso"));

        Region resultado = regionService.crear(region);

        assertEquals("Valparaíso", resultado.getNombre());
        verify(regionRepository).save(region);
    }

    @Test
    void testCrear_ErrorNombreVacio() {
        Region region = new Region();
        region.setNombre("");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> regionService.crear(region));

        assertEquals("El nombre de la región es obligatorio", ex.getMessage());
    }

    @Test
    void testActualizar_Success() {
        Region original = new Region(1L, "Antigua");
        Region nueva = new Region(null, "Nueva Región");

        when(regionRepository.findById(1L)).thenReturn(Optional.of(original));
        when(regionRepository.save(original)).thenReturn(new Region(1L, "Nueva Región"));

        Region resultado = regionService.actualizar(1L, nueva);

        assertEquals("Nueva Región", resultado.getNombre());
    }

    @Test
    void testActualizar_NoExiste() {
        when(regionRepository.findById(1L)).thenReturn(Optional.empty());

        Region nueva = new Region(null, "X Región");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> regionService.actualizar(1L, nueva));
        assertEquals("No existe región con ID: 1", ex.getMessage());
    }

    @Test
    void testEliminar_Success() {
        when(regionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(regionRepository).deleteById(1L);

        assertDoesNotThrow(() -> regionService.eliminar(1L));
        verify(regionRepository).deleteById(1L);
    }

    @Test
    void testEliminar_NoExiste() {
        when(regionRepository.existsById(1L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> regionService.eliminar(1L));
        assertEquals("No existe región con ID: 1", ex.getMessage());
    }
}