package com.example.direccion.webclient;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class VentaClient {

    private final WebClient webClient;

    public VentaClient(@Value("${venta-service.url}") String ventaServiceUrl) {
        this.webClient = WebClient.builder()
                                  .baseUrl(ventaServiceUrl)
                                  .build();
    }

    public void notificarVenta(Long idVenta, Long idDireccion) {
        webClient.post()
                 .uri("/api/v1/ventas/notificar-direccion")
                 .bodyValue(Map.of("idVenta", idVenta, "idDireccion", idDireccion))
                 .retrieve()
                 .bodyToMono(Void.class)
                 .block();
    }

    // Si se necesita obtener información de una venta
    @SuppressWarnings("rawtypes")
    public Mono<Map> obtenerVenta(Long idVenta) {
        return webClient.get()
                        .uri("/api/v1/ventas/{idVenta}", idVenta)
                        .retrieve()
                        .bodyToMono(Map.class);
    }
}