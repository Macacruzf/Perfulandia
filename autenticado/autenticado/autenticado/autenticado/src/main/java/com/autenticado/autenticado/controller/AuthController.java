package com.autenticado.autenticado.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autenticado.autenticado.config.JwtUtil;
import com.autenticado.autenticado.model.Usuario;
import com.autenticado.autenticado.service.AuthService;
import com.autenticado.autenticado.webclient.UsuarioClient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioClient usuarioClient;

    public AuthController(AuthService authService, PasswordEncoder passwordEncoder, UsuarioClient usuarioClient) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.usuarioClient = usuarioClient;
    }

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Iniciar sesión con nickname y contraseña", 
               description = "Genera un token JWT si las credenciales son válidas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso",
                     content = @Content(schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
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

        String token = jwtUtil.generateToken(usuario.getNickname());
        usuario.setPassword(null);

        return ResponseEntity.ok(Map.of(
            "token", token,
            "usuario", usuario
        ));
    }

    @Operation(summary = "Probar autenticación", description = "Prueba simple para validar conexión")
    @ApiResponse(responseCode = "200", description = "Respuesta exitosa",
                 content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/test")
    public String testAuth() {
        return "Hola usted ha entrado correctamente";
    }

    @Operation(summary = "Obtener datos del usuario autenticado",
               description = "Devuelve los datos del usuario actual")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Datos del usuario autenticado con enlaces",
                     content = @Content(schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/me")
    public ResponseEntity<?> obtenerMiPerfil(Principal principal) {
        return ResponseEntity.ok(Map.of(
            "message", "Inicio de sesión correcto"
        ));
    }
}


