package com.autenticado.autenticado.controller;

import com.autenticado.autenticado.model.Usuario;
import com.autenticado.autenticado.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva los filtros de Spring Security para esta prueba
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Se crea un usuario ficticio para pruebas
        usuario = new Usuario();
        usuario.setNickname("cliente");
        usuario.setPassword("claveEncriptada");
        usuario.setCorreo("cliente@correo.cl");
    }

    /**
     * Prueba: login exitoso debe retornar 200 y datos del usuario sin contraseña
     */
    @Test
    void loginExitosoDebeRetornarUsuario() throws Exception {
        when(authService.buscarPorNickname("cliente")).thenReturn(usuario);
        when(passwordEncoder.matches("clave123", "claveEncriptada")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .param("nickname", "cliente")
                        .param("password", "clave123"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value("cliente"));
    }

    /**
     * Prueba: cuando el usuario no existe debe retornar 401 Unauthorized
     */
    @Test
    void loginUsuarioNoEncontradoDebeRetornar401() throws Exception {
        when(authService.buscarPorNickname("inexistente")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .param("nickname", "inexistente")
                        .param("password", "clave123"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Prueba: si la contraseña no coincide debe retornar 401 Unauthorized
     */
    @Test
    void loginContrasenaIncorrectaDebeRetornar401() throws Exception {
        when(authService.buscarPorNickname("cliente")).thenReturn(usuario);
        when(passwordEncoder.matches("claveIncorrecta", "claveEncriptada")).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .param("nickname", "cliente")
                        .param("password", "claveIncorrecta"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Prueba: si accedemos a /test con usuario autenticado, debe retornar mensaje de éxito
     */
    @Test
    @WithMockUser
    void testAuthEndpointDebeRetornarMensaje() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/test"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Hello Authenticated!"));
    }
}

