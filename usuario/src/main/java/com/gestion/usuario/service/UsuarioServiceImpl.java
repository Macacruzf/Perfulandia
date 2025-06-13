
package com.gestion.usuario.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gestion.usuario.model.Rol;
import com.gestion.usuario.model.Usuario;
import com.gestion.usuario.repository.RolRepository;
import com.gestion.usuario.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con ese correo.");
        }

        String correo = usuario.getCorreo().toLowerCase();
        String nombreRol = correo.contains("admin") ? "Admin"
                          : correo.contains("gerente") ? "Gerente"
                          : correo.contains("logistica") ? "Logística"
                          : "Cliente";

        Rol rol = rolRepository.findByNombreRol(nombreRol)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombreRol));

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRol(rol);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario buscarPorNickname(String nickname) {
        return usuarioRepository.findByNickname(nickname).orElse(null);
    }

    @Override
    public Usuario buscarPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario).orElse(null);
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario actualizarUsuario(Long id, Usuario datos) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setCorreo(datos.getCorreo());
        usuario.setNickname(datos.getNickname());
        usuario.setPassword(passwordEncoder.encode(datos.getPassword()));
        usuario.setRol(datos.getRol());

        return usuarioRepository.save(usuario);
    }
}
