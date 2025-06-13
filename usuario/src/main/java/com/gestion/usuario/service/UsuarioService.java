
package com.gestion.usuario.service;

import java.util.List;

import com.gestion.usuario.model.Usuario;

public interface UsuarioService {
    Usuario registrarUsuario(Usuario usuario);
    Usuario buscarPorNickname(String nickname);
    Usuario buscarPorId(Long idUsuario);
    List<Usuario> listarTodos();
    void eliminarUsuario(Long id);
    Usuario actualizarUsuario(Long id, Usuario datos);
}

