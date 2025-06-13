package com.example.tickets.webclient;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
public class UsuarioClient {
    private final WebClient webClient;

    public UsuarioClient(@Value("${usuario-service.url}") String usuarioServiceUrl) {
        this.webClient = WebClient.builder().baseUrl(usuarioServiceUrl).build();
    }

    public Map<String, Object> obtenerUsuarioPorId(Long id) {
        return webClient.get()
                .uri("/api/v1/usuarios/{id}", id)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}
