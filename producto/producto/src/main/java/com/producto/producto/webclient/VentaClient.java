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

    /**
     * Constructor que configura el cliente con la URL base del microservicio de ventas.
     *
     * @param ventaServiceUrl URL del microservicio de ventas, inyectada desde propiedades.
     */
    public VentaClient(@Value("${venta-service.url}") String ventaServiceUrl) {
        this.webClient = WebClient.builder().baseUrl(ventaServiceUrl).build();
    }

    /**
     * Consulta los detalles de venta asociados a un producto específico.
     *
     * @param productoId ID del producto del cual se desean obtener las ventas.
     * @return Lista de mapas, cada uno representando un detalle de venta.
     *         El contenido de cada mapa dependerá del diseño del microservicio de ventas.
     * @throws RuntimeException si no se encuentran detalles (por ejemplo, error 404).
     */
    @SuppressWarnings("unchecked") // Se suprime el warning porque WebClient no puede inferir tipos genéricos en runtime
    public List<Map<String, Object>> getDetallesVentaByProductoId(Long productoId) {
        return this.webClient.get()
            .uri("/detalle-ventas/producto/{id}", productoId)
            .retrieve()
            .onStatus(status -> status.is4xxClientError(),
                response -> response.bodyToMono(String.class)
                    .map(body -> new RuntimeException("Detalles de venta no encontrados")))
            .bodyToMono(List.class)
            .block(); // Se bloquea la ejecución hasta obtener la respuesta
    }
}