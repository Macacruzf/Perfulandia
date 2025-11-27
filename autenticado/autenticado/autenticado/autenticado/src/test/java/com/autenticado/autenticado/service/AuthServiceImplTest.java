package com.autenticado.autenticado.service;

import com.autenticado.autenticado.model.Usuario;
import com.autenticado.autenticado.webclient.UsuarioClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    // Test para buscar un usuario por su nickname
    @Test
    void testBuscarPorNickname() {
        Usuario mockUsuario = new Usuario();
        mockUsuario.setNickname("dwyer");

        // Simulamos que el cliente devuelve un usuario con nickname "dwyer"
        when(usuarioClient.obtenerPorNickname("dwyer")).thenReturn(mockUsuario);

        // Ejecutamos el método del servicio
        Usuario result = authService.buscarPorNickname("dwyer");

        // Verificamos que el resultado no sea null y tenga el nickname esperado
        assertNotNull(result);
        assertEquals("dwyer", result.getNickname());
    }

    // Test de autenticación exitosa: usuario existe y contraseña coincide
    @Test
    void testAutenticar_Success() {
        Usuario mockUsuario = new Usuario();
        mockUsuario.setNickname("dwyer");
        mockUsuario.setPassword("encryptedPassword");

        // Simula obtener al usuario
        when(usuarioClient.obtenerPorNickname("dwyer")).thenReturn(mockUsuario);
        // Simula que la contraseña coincide
        when(passwordEncoder.matches("123456", "encryptedPassword")).thenReturn(true);

        // Ejecuta el método a probar
        Usuario result = authService.autenticar("dwyer", "123456");

        // Verifica que se devuelva el usuario correctamente autenticado
        assertNotNull(result);
        assertEquals("dwyer", result.getNickname());
    }

    // Test de autenticación fallida: usuario no existe
    @Test
    void testAutenticar_UsuarioNoExiste() {
        // Simulamos que el usuario no existe
        when(usuarioClient.obtenerPorNickname("dwyer")).thenReturn(null);

        Usuario result = authService.autenticar("dwyer", "123456");

        // Se espera null porque no existe el usuario
        assertNull(result);
    }

    // Test de autenticación fallida: contraseña incorrecta
    @Test
    void testAutenticar_ContrasenaIncorrecta() {
        Usuario mockUsuario = new Usuario();
        mockUsuario.setNickname("dwyer");
        mockUsuario.setPassword("encryptedPassword");

        when(usuarioClient.obtenerPorNickname("dwyer")).thenReturn(mockUsuario);
        // Simulamos que la contraseña no coincide
        when(passwordEncoder.matches("123456", "encryptedPassword")).thenReturn(false);

        Usuario result = authService.autenticar("dwyer", "123456");

        // Debe devolver null por contraseña incorrecta
        assertNull(result);
    }
}

