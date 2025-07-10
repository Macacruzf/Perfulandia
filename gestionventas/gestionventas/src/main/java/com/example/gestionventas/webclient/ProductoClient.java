package com.example.gestionventas.webclient;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;


@Component
public class ProductoClient {

    private final WebClient webClient;

    public ProductoClient(@Value("${producto-service.url}") String productoServiceUrl) {
        this.webClient = WebClient.builder().baseUrl(productoServiceUrl).build();
    }

    public Map<String, Object> getProductoById(Long id) {
        return this.webClient.get()
                .uri("/productos/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                    response -> response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new RuntimeException("Producto no encontrado (ID " + id + ")")))
                )
                .onStatus(HttpStatusCode::is5xxServerError,
                    response -> response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new RuntimeException("Error del servidor de productos al obtener el ID " + id)))
                )
                .bodyToMono(Map.class)
                .block();
    }

    public void actualizarStock(Long productoId, Integer cantidad) {
        this.webClient.put()
                .uri("/productos/{id}/stock", productoId)
                .bodyValue(Map.of("cantidad", cantidad))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                    response -> response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new RuntimeException("Error al actualizar stock (producto ID " + productoId + ")")))
                )
                .onStatus(HttpStatusCode::is5xxServerError,
                    response -> response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new RuntimeException("Error del servidor al actualizar stock")))
                )
                .bodyToMono(Void.class)
                .block();
    }

    public void actualizarStockBulk(List<Map<String, Object>> updates) {
        this.webClient.put()
                .uri("/productos/stock/bulk")
                .bodyValue(updates)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                    response -> response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new RuntimeException("Error al actualizar stock en bloque (4xx)")))
                )
                .onStatus(HttpStatusCode::is5xxServerError,
                    response -> response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new RuntimeException("Error del servidor al actualizar stock en bloque")))
                )
                .bodyToMono(Void.class)
                .block();
    }
}
