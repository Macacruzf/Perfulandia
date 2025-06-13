package com.producto.producto.webclient;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class UsuarioClient {
    private final WebClient webClient;

    public UsuarioClient(@Value("${usuario-service.url}") String usuarioServiceUrl) {
        this.webClient = WebClient.builder().baseUrl(usuarioServiceUrl).build();
    }

    public Map<String, Object> getUsuarioById(Long idUsuario) {
        return this.webClient.get()
            .uri("/" + idUsuario)
            .retrieve()
            .onStatus(status -> status.is4xxClientError(),
                response -> response.bodyToMono(String.class)
                    .map(body -> new RuntimeException("Usuario no encontrado")))
            .bodyToMono(Map.class)
            .block();
    }
}