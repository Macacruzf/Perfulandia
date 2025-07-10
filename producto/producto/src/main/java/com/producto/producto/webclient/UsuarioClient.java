package com.producto.producto.webclient;


import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@Component
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(@Value("${usuario-service.url}") String usuarioServiceUrl) {
        this.webClient = WebClient.builder().baseUrl(usuarioServiceUrl).build();
    }

    public Map<String, Object> getUsuarioById(Long idUsuario) {
        return this.webClient.get()
            .uri("/id/{id}", idUsuario)
            .headers(headers -> headers.setBasicAuth("admin", "admin")) // Cambiado de Bearer a Basic
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                this::handleError)
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .blockOptional()
            .filter(body -> body.containsKey("idUsuario"))
            .orElseThrow(() -> new RuntimeException("No se pudo obtener el usuario: respuesta vacía o incompleta."));
    }

    private Mono<? extends Throwable> handleError(ClientResponse response) {
        return response.bodyToMono(String.class)
            .flatMap(body -> Mono.error(new RuntimeException("Error al obtener usuario: " + body)));
    }
}