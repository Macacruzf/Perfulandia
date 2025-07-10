package com.gestion.usuario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.usuario.model.Rol;
import com.gestion.usuario.model.Usuario;
import com.gestion.usuario.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import com.gestion.usuario.config.SecurityConfig;

@WebMvcTest(UsuarioController.class)
@Import(SecurityConfig.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Rol crearRolCliente() {
        Rol rol = new Rol();
        rol.setNombreRol("CLIENTE");
        return rol;
    }

    @Test
    @WithMockUser
    void registrarUsuario_deberiaRetornarStatus200() throws Exception {
        Usuario usuario = new Usuario(1L, "nick", "mail@test.com", "1234", crearRolCliente());
        when(usuarioService.registrarUsuario(any())).thenReturn(usuario);

        mockMvc.perform(post("/api/v1/usuarios/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void obtenerUsuarioPorId_existente() throws Exception {
        Usuario usuario = new Usuario(1L, "nick", "correo@mail.com", "pass", crearRolCliente());
        when(usuarioService.buscarPorId(1L)).thenReturn(usuario);

        mockMvc.perform(get("/api/v1/usuarios/id/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void obtenerUsuarioPorId_noEncontrado() throws Exception {
        when(usuarioService.buscarPorId(1L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/usuarios/id/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void actualizarUsuario_exitoso() throws Exception {
        Usuario usuario = new Usuario(1L, "nick", "correo@mail.com", "pass", crearRolCliente());

        mockMvc.perform(put("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void actualizarUsuario_noExiste() throws Exception {
        Usuario usuario = new Usuario();
        doThrow(new RuntimeException("No encontrado")).when(usuarioService).actualizarUsuario(eq(1L), any());

        mockMvc.perform(put("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void eliminarUsuario_exitoso() throws Exception {
        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void eliminarUsuario_noExiste() throws Exception {
        doThrow(new RuntimeException("No encontrado")).when(usuarioService).eliminarUsuario(1L);

        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void obtenerUsuarioPorNickname_existente() throws Exception {
        Usuario usuario = new Usuario(1L, "nick", "correo@mail.com", "pass", crearRolCliente());
        when(usuarioService.buscarPorNickname("nick")).thenReturn(usuario);

        mockMvc.perform(get("/api/v1/usuarios/nickname/nick"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void obtenerUsuarioPorNickname_noEncontrado() throws Exception {
        when(usuarioService.buscarPorNickname("nick")).thenReturn(null);

        mockMvc.perform(get("/api/v1/usuarios/nickname/nick"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void listarUsuarios_exitoso() throws Exception {
        List<Usuario> lista = List.of(new Usuario(1L, "nick", "correo@mail.com", "pass", crearRolCliente()));
        when(usuarioService.listarTodos()).thenReturn(lista);

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "nick")
    void actualizarMiPerfil_exitoso() throws Exception {
        Usuario datos = new Usuario();
        datos.setCorreo("nuevo@mail.com");
        datos.setNickname("nuevoNick");

        Usuario existente = new Usuario(1L, "nick", "correo@mail.com", "pass", crearRolCliente());
        when(usuarioService.buscarPorNickname("nick")).thenReturn(existente);

        mockMvc.perform(put("/api/v1/usuarios/perfil")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "nick")
    void actualizarMiPerfil_usuarioNoEncontrado() throws Exception {
        Usuario datos = new Usuario();
        datos.setCorreo("nuevo@mail.com");
        datos.setNickname("nuevoNick");

        when(usuarioService.buscarPorNickname("nick")).thenReturn(null);

        mockMvc.perform(put("/api/v1/usuarios/perfil")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isNotFound());
    }
}
