package com.producto.producto.webclient;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Cliente para comunicarse con el microservicio de resenas.
 * Permite consultar y validar resenas asociadas a productos.
 */
@Component
public class ResenaClient {


   private final WebClient webClient;

    public ResenaClient(@Value("${resena-service.url}") String resenaServiceUrl) {
        this.webClient = WebClient.builder().baseUrl(resenaServiceUrl).build();
    }

    public List<Map<String, Object>> getResenasByProductoId(Long idProducto) {
        return this.webClient.get()
            .uri("/producto/{id}", idProducto)
            .retrieve()
            .onStatus(status -> status.is4xxClientError(),
                response -> response.bodyToMono(String.class)
                .map(body -> new RuntimeException("Resenas no encontradas")))
            .bodyToMono(List.class)
            .block();
    }
}