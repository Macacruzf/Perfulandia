package com.producto.producto.webclient;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class UsuarioClient {

    private final WebClient webClient;

    /**
     * Constructor del cliente de usuarios.
     *
     * @param usuarioServiceUrl URL base del microservicio de usuarios (inyectado desde propiedades).
     */
    public UsuarioClient(@Value("${usuario-service.url}") String usuarioServiceUrl) {
        this.webClient = WebClient.builder().baseUrl(usuarioServiceUrl).build();
    }

    /**
     * Obtiene los datos de un usuario desde otro microservicio usando su ID.
     *
     * @param idUsuario ID del usuario a consultar.
     * @return Un mapa con los datos del usuario, típicamente incluyendo privilegios, nombre, etc.
     * @throws RuntimeException si el usuario no es encontrado (error 4xx).
     */
    public Map<String, Object> getUsuarioById(Long idUsuario) {
        return this.webClient.get()
            .uri("/" + idUsuario)
            .retrieve()
            .onStatus(status -> status.is4xxClientError(),
                response -> response.bodyToMono(String.class)
                    .map(body -> new RuntimeException("Usuario no encontrado")))
            .bodyToMono(Map.class)
            .block(); // Ejecuta la llamada sincrónicamente
    }
}