
package com.gestion.usuario.service;

import java.util.List;
import java.util.Optional;

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

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        String correo = usuario.getCorreo().toLowerCase();

        //  Si viene un rol en el request, se respeta
        if (usuario.getRol() != null && usuario.getRol().getId() != null) {
            Rol rol = rolRepository.findById(usuario.getRol().getId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + usuario.getRol().getId()));
            usuario.setRol(rol);
        } else {
            // Si NO viene un rol, lo asignamos automáticamente según el correo
            String nombreRol = correo.contains("admin") ? "Admin"
                            : correo.contains("gerente") ? "Gerente"
                            : correo.contains("logistica") ? "Logística"
                            : "Cliente";

            Rol rol = rolRepository.findByNombreRol(nombreRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombreRol));
            usuario.setRol(rol);
        }

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
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioRepository.delete(usuario);
    }

    @Override
    public Usuario actualizarUsuario(Long id, Usuario datos) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar si el correo ya existe y pertenece a otro usuario
        Optional<Usuario> existenteCorreo = usuarioRepository.findByCorreo(datos.getCorreo());
        if (existenteCorreo.isPresent() && !existenteCorreo.get().getIdUsuario().equals(id)) {
            throw new RuntimeException("El correo ya está en uso por otro usuario.");
        }

        // Validar si el nickname ya existe y pertenece a otro usuario
        Optional<Usuario> existenteNick = usuarioRepository.findByNickname(datos.getNickname());
        if (existenteNick.isPresent() && !existenteNick.get().getIdUsuario().equals(id)) {
            throw new RuntimeException("El nickname ya está en uso por otro usuario.");
        }

        // Solo actualiza si no es nulo
        if (datos.getCorreo() != null) {
            usuario.setCorreo(datos.getCorreo());
        }

        if (datos.getNickname() != null) {
            usuario.setNickname(datos.getNickname());
        }

        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(datos.getPassword()));
        }

        if (datos.getRol() != null && datos.getRol().getId() != null) {
            usuario.setRol(datos.getRol());
        }

        return usuarioRepository.save(usuario);
    }
}
