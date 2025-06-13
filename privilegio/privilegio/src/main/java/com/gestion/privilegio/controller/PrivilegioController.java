
package com.gestion.privilegio.controller;

import java.util.List;


import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.privilegio.model.Privilegio;
import com.gestion.privilegio.service.PrivilegioService;
@RestController
@RequestMapping("/privilegios")


public class PrivilegioController {
   private final PrivilegioService privilegioService;

    public PrivilegioController(PrivilegioService privilegioService) {
        this.privilegioService = privilegioService;
    }

    @GetMapping("/rol/{nombreRol}")
    public ResponseEntity<List<Privilegio>> obtenerPrivilegiosPorRol(@PathVariable String nombreRol) {
        List<Privilegio> privilegios = privilegioService.obtenerPrivilegiosPorRol(nombreRol);
        if (privilegios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(privilegios);
    }

    @PostMapping("/mensaje")
    public ResponseEntity<String> obtenerMensajePorCredenciales(
            @RequestParam String nickname,
            @RequestParam String password) {
        String mensaje = privilegioService.obtenerMensajePorCredenciales(nickname, password);
        return ResponseEntity.ok(mensaje);
    }
}
