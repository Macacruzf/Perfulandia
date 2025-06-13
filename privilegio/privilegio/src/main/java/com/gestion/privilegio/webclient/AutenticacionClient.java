
package com.gestion.privilegio.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.gestion.privilegio.model.Usuario;

@Component
public class AutenticacionClient {
   private final WebClient webClient;

    public AutenticacionClient(@Value("${gestionautenticacion.url}") String baseUrl, WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Usuario autenticar(String nickname, String password) {
        return webClient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/auth/login")
                .queryParam("nickname", nickname)
                .queryParam("password", password)
                .build())
            .retrieve()
            .bodyToMono(Usuario.class)
            .block();
    }

   
}
