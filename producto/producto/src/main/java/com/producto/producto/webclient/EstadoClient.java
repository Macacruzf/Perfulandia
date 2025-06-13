package com.producto.producto.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.producto.producto.service.ProductoService;

import reactor.core.publisher.Mono;
import java.util.Map;

@Component
public class EstadoClient {
    private final WebClient webClient;

    public EstadoClient(@Value("${estado-service.url}") String estadoServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(estadoServiceUrl)
                .build();
    }

    public Map<String, Object> getEstadoById(Long id) {
        if (id == null) {
            return null;
        }
        try {
            Map<String, Object> estado = webClient.get()
                    .uri("/{id}", id)  // <-- esta es la corrección clave
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> 
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                    new RuntimeException("Error al obtener estado: " + body)
                                )))
                    .bodyToMono(Map.class)
                    .block();

            if (estado == null || !estado.containsKey("nombre")) {
                return null;
            }
            return estado;

        } catch (RuntimeException e) {
            System.err.println("Error en EstadoClient: " + e.getMessage());
            return null;
        }
    }

    public void validarEstado(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del estado es obligatorio");
        }
        Map<String, Object> estado = getEstadoById(id);
        if (estado == null) {
            throw new ProductoService.ResourceNotFoundException("Estado con ID " + id + " no existe");
        }
    }
}