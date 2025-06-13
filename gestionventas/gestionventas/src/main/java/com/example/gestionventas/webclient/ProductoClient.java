package com.example.gestionventas.webclient;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Producto no encontrado")))
                .bodyToMono(Map.class).block();
    }

    public void actualizarStock(Long productoId, Integer cantidad) {
        this.webClient.put()
                .uri("/productos/{id}/stock", productoId)
                .bodyValue(Map.of("cantidad", cantidad))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Error al actualizar stock")))
                .bodyToMono(Void.class).block();
    }

    public void actualizarStockBulk(List<Map<String, Object>> updates) {
        this.webClient.put()
                .uri("/productos/stock/bulk")
                .bodyValue(updates)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Error al actualizar stock en bloque")))
                .bodyToMono(Void.class).block();
    }
}
