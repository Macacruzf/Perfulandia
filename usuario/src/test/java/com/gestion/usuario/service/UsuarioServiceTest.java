package com.gestion.usuario.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gestion.usuario.model.Rol;
import com.gestion.usuario.model.Usuario;
import com.gestion.usuario.repository.RolRepository;
import com.gestion.usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Rol rolCliente;

    @BeforeEach
    void setUp() {
        rolCliente = new Rol();
        rolCliente.setId(1L);
        rolCliente.setNombreRol("Cliente");
    }

    @Test
    void registrarUsuario_conCorreoUnicoYRolAsociado() {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setCorreo("cliente@mail.com");
        nuevoUsuario.setPassword("1234");
        nuevoUsuario.setNickname("nick");

        when(usuarioRepository.findByCorreo("cliente@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234")).thenReturn("hashed");
        when(rolRepository.findByNombreRol("Cliente")).thenReturn(Optional.of(rolCliente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.registrarUsuario(nuevoUsuario);

        assertNotNull(resultado);
        assertEquals("hashed", resultado.getPassword());
        assertEquals("Cliente", resultado.getRol().getNombreRol());
    }

    @Test
    void buscarPorNickname_existente() {
        Usuario usuario = new Usuario();
        usuario.setNickname("nick");
        when(usuarioRepository.findByNickname("nick")).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorNickname("nick");

        assertNotNull(resultado);
        assertEquals("nick", resultado.getNickname());
    }

    @Test
    void actualizarUsuario_conDatosValidos() {
        Long id = 1L;
        Rol rol = new Rol(1L, "Cliente", new ArrayList<>());

        Usuario existente = new Usuario(id, "nickAntiguo", "antiguo@mail.com", "passAntigua", rol);

        Usuario actualizacion = new Usuario();
        actualizacion.setCorreo("nuevo@mail.com");
        actualizacion.setNickname("nuevoNick");
        actualizacion.setPassword("nuevaPass");
        actualizacion.setRol(rol);

        // Mocks
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existente));
        when(usuarioRepository.findByCorreo("nuevo@mail.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByNickname("nuevoNick")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("nuevaPass")).thenReturn("hashedPass");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Usuario actualizado = usuarioService.actualizarUsuario(id, actualizacion);

        // Assert
        assertEquals("nuevo@mail.com", actualizado.getCorreo());
        assertEquals("nuevoNick", actualizado.getNickname());
        assertEquals("hashedPass", actualizado.getPassword());
        assertEquals("Cliente", actualizado.getRol().getNombreRol());
    }

    @Test
    void eliminarUsuario_existente() {
        Usuario usuario = new Usuario(1L, "nick", "mail", "pass", rolCliente);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertDoesNotThrow(() -> usuarioService.eliminarUsuario(1L));
        verify(usuarioRepository).delete(usuario);
    }
}