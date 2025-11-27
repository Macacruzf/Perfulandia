package com.example.gestionventas.webclient;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class DireccionClient {

    private final WebClient webClient;

    public DireccionClient(@Value("${direccion-service.url}") String direccionServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(direccionServiceUrl)
                .build();
    }

    public Map<String, Object> getDireccionById(Long id) {
        return this.webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Error al obtener dirección: " + body)))
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
}