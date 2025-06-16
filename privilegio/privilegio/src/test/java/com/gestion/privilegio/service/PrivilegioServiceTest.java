package com.gestion.privilegio.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gestion.privilegio.model.Modulo;
import com.gestion.privilegio.model.Privilegio;
import com.gestion.privilegio.repository.PrivilegioRepository;

public class PrivilegioServiceTest {

    @Mock
    private PrivilegioRepository privilegioRepository;

    @InjectMocks
    private PrivilegioService privilegioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks antes de cada prueba
    }

    @Test
    void testListarTodos() {
        // Preparamos datos simulados
        Privilegio p1 = new Privilegio();
        Privilegio p2 = new Privilegio();
        List<Privilegio> lista = Arrays.asList(p1, p2);

        // Indicamos que al llamar a findAll() devuelva nuestra lista
        when(privilegioRepository.findAll()).thenReturn(lista);

        // Llamamos al método del servicio
        List<Privilegio> resultado = privilegioService.listarTodos();

        // Verificamos que la lista tenga el tamaño esperado
        assertEquals(2, resultado.size());
        verify(privilegioRepository, times(1)).findAll(); // Verifica que se haya llamado 1 vez
    }

    @Test
    void testGuardarPrivilegio() {
        Privilegio privilegio = new Privilegio();
        when(privilegioRepository.save(privilegio)).thenReturn(privilegio);

        Privilegio resultado = privilegioService.guardar(privilegio);

        assertNotNull(resultado);
        verify(privilegioRepository, times(1)).save(privilegio);
    }

    @Test
    void testEliminarPrivilegio() {
        Long id = 1L;

        privilegioService.eliminar(id);

        verify(privilegioRepository, times(1)).deleteById(id);
    }

    @Test
    void testObtenerModulosPorRol() {
        String rol = "ADMIN";
        Modulo m1 = new Modulo();
        Modulo m2 = new Modulo();
        List<Modulo> modulos = Arrays.asList(m1, m2);

        when(privilegioRepository.findModulosByRolNombre(rol)).thenReturn(modulos);

        List<Modulo> resultado = privilegioService.obtenerModulosPorRol(rol);

        assertEquals(2, resultado.size());
        verify(privilegioRepository).findModulosByRolNombre(rol);
    }
}