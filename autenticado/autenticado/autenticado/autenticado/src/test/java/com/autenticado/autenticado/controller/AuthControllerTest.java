package com.autenticado.autenticado.controller;

import com.autenticado.autenticado.config.JwtUtil;
import com.autenticado.autenticado.model.Usuario;
import com.autenticado.autenticado.model.Rol;
import com.autenticado.autenticado.service.AuthService;
import com.autenticado.autenticado.webclient.UsuarioClient;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.mockito.Mockito.when;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva filtros como el JwtFilter para pruebas más simples
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Se mockean las dependencias usadas dentro del AuthController
    @MockBean
    private AuthService authService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UsuarioClient usuarioClient;

    @MockBean
    private JwtUtil jwtUtil;

    // 🧪 Test exitoso de login
    @Test
    void loginSuccess() throws Exception {
        // Se crea un usuario mock con datos simulados
        Usuario mockUsuario = new Usuario();
        mockUsuario.setNickname("dwyer");
        mockUsuario.setPassword("encryptedPassword");
        mockUsuario.setCorreo("dwyer@correo.com");
        mockUsuario.setRol(new Rol(2L, "CLIENTE"));

        // Simulamos que el servicio devuelve ese usuario
        when(authService.buscarPorNickname("dwyer")).thenReturn(mockUsuario);
        when(passwordEncoder.matches("123456", "encryptedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("dwyer")).thenReturn("fakeToken");

        // Simulamos una petición POST al endpoint /login
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("nickname", "dwyer")
                        .param("password", "123456"))
                .andExpect(status().isOk()) // Esperamos un 200 OK
                .andExpect(jsonPath("$.token").value("fakeToken")) // Validamos el token retornado
                .andExpect(jsonPath("$.usuario.nickname").value("dwyer")); // Validamos los datos del usuario
    }

    // 🧪 Test cuando el usuario no es encontrado
    @Test
    void loginUserNotFound() throws Exception {
        // Simulamos que no se encontró el usuario
        when(authService.buscarPorNickname("dwyer")).thenReturn(null);

        // Hacemos la solicitud con nickname inexistente
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("nickname", "dwyer")
                        .param("password", "123456"))
                .andExpect(status().isUnauthorized()) // Esperamos un 401
                .andExpect(content().string("Usuario no encontrado")); // Y el mensaje de error correspondiente
    }

    // 🧪 Test cuando la contraseña es incorrecta
    @Test
    void loginWrongPassword() throws Exception {
        // Creamos el usuario pero la contraseña no coincide
        Usuario mockUsuario = new Usuario();
        mockUsuario.setNickname("dwyer");
        mockUsuario.setPassword("encryptedPassword");

        when(authService.buscarPorNickname("dwyer")).thenReturn(mockUsuario);
        when(passwordEncoder.matches("123456", "encryptedPassword")).thenReturn(false);

        mockMvc.perform(post("/api/v1/auth/login")
                        .param("nickname", "dwyer")
                        .param("password", "123456"))
                .andExpect(status().isUnauthorized()) // 401 por contraseña incorrecta
                .andExpect(content().string("Contraseña incorrecta"));
    }

    // 🧪 Test del endpoint /test (solo para validar respuesta sencilla)
    @Test
    void testAuthEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/auth/test"))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(content().string("Hola usted ha entrado correctamente")); // Mensaje esperado
    }

    // 🧪 Test del endpoint /me con Principal mockeado
    @Test
    void obtenerMiPerfil() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me")
                        .principal(() -> "dwyer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Inicio de sesión correcto"));
    }          
}
