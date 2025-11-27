package com.example.tickets.service;

import com.example.tickets.model.Motivo;
import com.example.tickets.repository.MotivoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

public class MotivoServiceTest {

    @Mock
    private MotivoRepository motivoRepository;

    @InjectMocks
    private MotivoService motivoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testObtenerTodos() {
        List<Motivo> mockList = Arrays.asList(new Motivo(1L, "Falla"), new Motivo(2L, "Instalación"));
        when(motivoRepository.findAll()).thenReturn(mockList);

        List<Motivo> resultado = motivoService.obtenerTodos();

        assertEquals(2, resultado.size());
        verify(motivoRepository).findAll();
    }

    @Test
    void testObtenerPorId_Existe() {
        Motivo motivo = new Motivo(1L, "Falla");
        when(motivoRepository.findById(1L)).thenReturn(Optional.of(motivo));

        Motivo resultado = motivoService.obtenerPorId(1L);

        assertEquals("Falla", resultado.getNombre());
        verify(motivoRepository).findById(1L);
    }

    @Test
    void testObtenerPorId_NoExiste() {
        when(motivoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> motivoService.obtenerPorId(1L));
    }

    @Test
    void testCrear() {
        Motivo motivo = new Motivo(null, "Falla");
        when(motivoRepository.save(motivo)).thenReturn(new Motivo(1L, "Falla"));

        Motivo resultado = motivoService.crear(motivo);

        assertNotNull(resultado);
        assertEquals("Falla", resultado.getNombre());
        verify(motivoRepository).save(motivo);
    }

    @Test
    void testActualizar() {
        Motivo existente = new Motivo(1L, "Antiguo");
        Motivo actualizado = new Motivo(null, "Nuevo");

        when(motivoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(motivoRepository.save(any(Motivo.class))).thenReturn(existente);

        Motivo resultado = motivoService.actualizar(1L, actualizado);

        assertEquals("Nuevo", resultado.getNombre());
        verify(motivoRepository).save(existente);
    }

    @Test
    void testEliminar_Existe() {
        when(motivoRepository.existsById(1L)).thenReturn(true);

        motivoService.eliminar(1L);

        verify(motivoRepository).deleteById(1L);
    }

    @Test
    void testEliminar_NoExiste() {
        when(motivoRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> motivoService.eliminar(1L));
    }
}