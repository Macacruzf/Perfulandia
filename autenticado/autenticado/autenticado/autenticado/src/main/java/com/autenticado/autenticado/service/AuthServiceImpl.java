package com.autenticado.autenticado.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.autenticado.autenticado.model.Usuario;
import com.autenticado.autenticado.webclient.UsuarioClient;

@Service
public class AuthServiceImpl implements AuthService {
    private final UsuarioClient usuarioClient;
    private final PasswordEncoder passwordEncoder;

   
    public AuthServiceImpl(UsuarioClient usuarioClient, PasswordEncoder passwordEncoder) {
        this.usuarioClient = usuarioClient;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario buscarPorNickname(String nickname) {
        return usuarioClient.obtenerPorNickname(nickname);
    }

    @Override
    public Usuario autenticar(String nickname, String password) {
        Usuario usuario = usuarioClient.obtenerPorNickname(nickname);
        if (usuario != null && passwordEncoder.matches(password, usuario.getPassword())) {
            return usuario;
        }
        return null;
    }
} 