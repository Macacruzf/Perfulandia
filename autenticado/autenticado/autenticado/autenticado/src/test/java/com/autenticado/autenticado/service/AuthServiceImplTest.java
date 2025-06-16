package com.autenticado.autenticado.service;

import com.autenticado.autenticado.model.Usuario;
import com.autenticado.autenticado.webclient.UsuarioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;


public class AuthServiceImplTest {

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario();
        usuario.setNickname("cliente");
        usuario.setPassword("claveEncriptada");
        usuario.setCorreo("cliente@correo.cl");
    }

    /**
     * Prueba: debe retornar el usuario cuando se encuentra por nickname
     */
    @Test
    void buscarPorNicknameDebeRetornarUsuario() {
        when(usuarioClient.obtenerPorNickname("cliente")).thenReturn(usuario);

        Usuario resultado = authService.buscarPorNickname("cliente");

        assertNotNull(resultado);
        assertEquals("cliente", resultado.getNickname());
    }

    /**
     * Prueba: debe retornar el usuario si la contraseña coincide
     */
    @Test
    void autenticarContrasenaCorrectaDebeRetornarUsuario() {
        when(usuarioClient.obtenerPorNickname("cliente")).thenReturn(usuario);
        when(passwordEncoder.matches("clave123", "claveEncriptada")).thenReturn(true);

        Usuario resultado = authService.autenticar("cliente", "clave123");

        assertNotNull(resultado);
        assertEquals("cliente", resultado.getNickname());
    }

    /**
     * Prueba: debe retornar null si la contraseña no coincide
     */
    @Test
    void autenticarContrasenaIncorrectaDebeRetornarNull() {
        when(usuarioClient.obtenerPorNickname("cliente")).thenReturn(usuario);
        when(passwordEncoder.matches("claveIncorrecta", "claveEncriptada")).thenReturn(false);

        Usuario resultado = authService.autenticar("cliente", "claveIncorrecta");

        assertNull(resultado);
    }

    /**
     * Prueba: debe retornar null si el usuario no existe
     */
    @Test
    void autenticarUsuarioNoEncontradoDebeRetornarNull() {
        when(usuarioClient.obtenerPorNickname("desconocido")).thenReturn(null);

        Usuario resultado = authService.autenticar("desconocido", "cualquierClave");

        assertNull(resultado);
    }
}

