package com.autenticado.autenticado.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autenticado.autenticado.model.Usuario;
import com.autenticado.autenticado.service.AuthService;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    // Endpoint manual de login (sin usar Basic Auth)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String nickname, @RequestParam String password) {
        Usuario usuario = authService.buscarPorNickname(nickname);
        if (usuario == null) {
            return ResponseEntity.status(401).body("Usuario no encontrado");
        }

        boolean passwordMatches = passwordEncoder.matches(password, usuario.getPassword());
        if (!passwordMatches) {
            return ResponseEntity.status(401).body("Contraseña incorrecta");
        }

        // Si todo OK, retornamos el usuario (sin password)
        usuario.setPassword(null);
        return ResponseEntity.ok(usuario);
    }

    // 🚀 Endpoint GET para probar Basic Auth
    @GetMapping("/test")
    public String testAuth() {
        return "Hello Authenticated!";
    }
}