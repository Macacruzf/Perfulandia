package com.autenticado.autenticado.webclient;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.autenticado.autenticado.model.Usuario;



@Component
public class UsuarioClient {
    private final WebClient webClient;

    public UsuarioClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8027/api/usuarios").build();  // URL del microservicio usuarios
    }

    public Usuario obtenerPorNickname(String nickname) {
        return webClient.get()
                .uri("/nickname/{nickname}", nickname)
                .retrieve()
                .bodyToMono(Usuario.class)
                .block();  // Sincrónico para simplificar, usa async en producción
    }
}