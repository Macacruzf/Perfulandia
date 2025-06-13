package com.example.gestionventas.webclient;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class EstadoClient {
    private final WebClient webClient;

    public EstadoClient(@Value("${estado-service.url}") String estadoServiceUrl) {
        this.webClient = WebClient.builder().baseUrl(estadoServiceUrl).build();
    }

    public Map<String, Object> getEstadoEnvioById(Long id) {
        return this.webClient.get()
                .uri("/estado-envio/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Estado de envío no encontrado")))
                .bodyToMono(Map.class).block();
    }
}