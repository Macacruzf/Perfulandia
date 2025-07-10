package com.autenticado.autenticado.service;

import com.autenticado.autenticado.model.Usuario;

public interface AuthService {
    Usuario autenticar(String nickname, String password);
    Usuario buscarPorNickname(String nickname);
}
