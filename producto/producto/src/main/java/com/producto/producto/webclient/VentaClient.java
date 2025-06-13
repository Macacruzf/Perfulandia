package com.producto.producto.webclient;

import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Cliente para comunicarse con el microservicio de ventas.
 * Permite consultar los detalles de venta asociados a un producto usando su ID.
 */
@Component
public class VentaClient {

    private final WebClient webClient;

    public VentaClient(@Value("${venta-service.url}") String ventaServiceUrl) {
        this.webClient = WebClient.builder().baseUrl(ventaServiceUrl).build();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDetallesVentaByProductoId(Long productoId) {
        return this.webClient.get()
            .uri("/detalle-ventas/producto/{id}", productoId)
            .retrieve()
            .onStatus(status -> status.is4xxClientError(),
                response -> response.bodyToMono(String.class)
                .map(body -> new RuntimeException("Detalles de venta no encontrados")))
            .bodyToMono(List.class)
            .block();
    }
}