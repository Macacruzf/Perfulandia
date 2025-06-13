package com.example.direccion.webclient;


import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UsuarioClient {
    private final WebClient webClient;

    public UsuarioClient(@Value("${usuario-service.url}") String usuarioServiceUrl) {
        this.webClient = WebClient.builder()
                                  .baseUrl(usuarioServiceUrl)
                                  .build();
    }

    public Mono<Object> obtenerUsuario(Long idUsuario) {
        return webClient.get()
                        .uri("/api/v1/usuarios/{idUsuario}", idUsuario)
                        .retrieve()
                        .bodyToMono(Object.class);
    }
}