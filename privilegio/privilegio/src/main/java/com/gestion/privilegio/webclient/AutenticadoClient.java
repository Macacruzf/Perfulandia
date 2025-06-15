
package com.gestion.privilegio.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;

import java.util.Map;
@Component
public class AutenticadoClient {

    private final WebClient webClient;

    public AutenticadoClient(@Value("${autenticado-service.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    /**
     * Llama al microservicio autenticado para obtener los datos del usuario autenticado
     * a partir del token JWT.
     * 
     * @param token el JWT recibido del cliente (sin 'Bearer')
     * @return Mapa con los datos decodificados del usuario
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> obtenerDatosDesdeToken(String token) {
        return webClient.get()
                .uri("/api/v1/autenticado/info") // ajusta esta URI según cómo esté configurado autenticado
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}
