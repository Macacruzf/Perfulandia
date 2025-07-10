package com.gestion.privilegio.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gestion.privilegio.model.Modulo;
import com.gestion.privilegio.repository.ModuloRepository;

public class ModuloServiceTest {

    @Mock
    private ModuloRepository moduloRepository;

    @InjectMocks
    private ModuloService moduloService;

    @BeforeEach
    public void setUp() {
        // Inicializa los mocks antes de cada prueba
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testObtenerTodosLosModulos() {
        // Simulación de datos
        Modulo m1 = new Modulo(); m1.setIdModulo(1L); m1.setNombre("productos");
        Modulo m2 = new Modulo(); m2.setIdModulo(2L); m2.setNombre("ventas");
        when(moduloRepository.findAll()).thenReturn(Arrays.asList(m1, m2));

        // Llamada real al servicio
        List<Modulo> resultado = moduloService.obtenerTodosLosModulos();

        // Verificaciones
        assertEquals(2, resultado.size());
        verify(moduloRepository, times(1)).findAll();
    }

    @Test
    public void testObtenerPorId_Existe() {
        Modulo m = new Modulo(); m.setIdModulo(1L); m.setNombre("usuarios");
        when(moduloRepository.findById(1L)).thenReturn(Optional.of(m));

        Modulo resultado = moduloService.obtenerPorId(1L);
        assertNotNull(resultado);
        assertEquals("usuarios", resultado.getNombre());
    }

    @Test
    public void testObtenerPorId_NoExiste() {
        when(moduloRepository.findById(1L)).thenReturn(Optional.empty());
        Modulo resultado = moduloService.obtenerPorId(1L);
        assertNull(resultado);
    }

    @Test
    public void testCrearModulo() {
        Modulo nuevo = new Modulo(); nuevo.setNombre("ticket");
        when(moduloRepository.save(nuevo)).thenReturn(nuevo);

        Modulo resultado = moduloService.crearModulo(nuevo);
        assertNotNull(resultado);
        assertEquals("ticket", resultado.getNombre());
    }

    @Test
    public void testActualizarModulo_Existe() {
        Modulo existente = new Modulo(); existente.setIdModulo(1L); existente.setNombre("ventas");
        Modulo actualizado = new Modulo(); actualizado.setNombre("ventas actualizado");

        when(moduloRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(moduloRepository.save(any(Modulo.class))).thenReturn(existente);

        Modulo resultado = moduloService.actualizarModulo(1L, actualizado);
        assertNotNull(resultado);
        assertEquals("ventas actualizado", resultado.getNombre());
    }

    @Test
    public void testActualizarModulo_NoExiste() {
        Modulo actualizado = new Modulo(); actualizado.setNombre("cualquier");
        when(moduloRepository.findById(1L)).thenReturn(Optional.empty());

        Modulo resultado = moduloService.actualizarModulo(1L, actualizado);
        assertNull(resultado);
    }

    @Test
    public void testEliminarModulo_Existe() {
        when(moduloRepository.existsById(1L)).thenReturn(true);
        boolean eliminado = moduloService.eliminarModulo(1L);
        assertTrue(eliminado);
        verify(moduloRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testEliminarModulo_NoExiste() {
        when(moduloRepository.existsById(1L)).thenReturn(false);
        boolean eliminado = moduloService.eliminarModulo(1L);
        assertFalse(eliminado);
    }

    @Test
    public void testListarModulos() {
        when(moduloRepository.findAll()).thenReturn(List.of(new Modulo(), new Modulo()));
        List<Modulo> resultado = moduloService.listarModulos();
        assertEquals(2, resultado.size());
    }
}
