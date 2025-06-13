
package com.gestion.usuario.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;


import com.gestion.usuario.model.Usuario;

import com.gestion.usuario.service.UsuarioService;


@RestController
@RequestMapping("api/v1/usuarios")
@CrossOrigin(origins = "*") // Permite llamadas desde otros servicios (como autenticación)
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;

    // Registro disponible para todos
    @PostMapping("/registro")
    public Usuario registrarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.registrarUsuario(usuario);
    }

    // Solo ADMIN puede obtener usuarios por ID
    @GetMapping("/id/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long idUsuario) {
        Usuario usuario = usuarioService.buscarPorId(idUsuario);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Usuario> obtenerPorNickname(@PathVariable String nickname) {
        Usuario usuario = usuarioService.buscarPorNickname(nickname);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }

    // ADMIN puede listar todos los usuarios
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarTodos();
    }

    // ADMIN puede actualizar cualquier usuario
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Usuario actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        return usuarioService.actualizarUsuario(id, usuario);
    }

    // ADMIN puede eliminar cualquier usuario
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
    }

    // Cliente, Gerente, Logística pueden actualizar su propio perfil
    @PutMapping("/perfil")
    @PreAuthorize("hasAnyRole('CLIENTE', 'GERENTE', 'LOGISTICA', 'ADMIN')")
    public ResponseEntity<Usuario> actualizarMiPerfil(@RequestBody Usuario datos, Authentication auth) {
        String nickname = auth.getName();
        Usuario usuario = usuarioService.buscarPorNickname(nickname);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        usuario.setCorreo(datos.getCorreo());
        usuario.setNickname(datos.getNickname());
        return ResponseEntity.ok(usuarioService.actualizarUsuario(usuario.getIdUsuario(), usuario));
    }

}

