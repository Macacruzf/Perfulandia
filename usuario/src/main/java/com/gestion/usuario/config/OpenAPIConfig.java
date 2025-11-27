package com.gestion.usuario.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API de Usuarios")
                .version("1.0")
                .description("Documentación de la API de gestión de usuarios con autenticación JWT"))
            
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            
            .components(new Components()
                .addSecuritySchemes("bearerAuth",  
                    new SecurityScheme()
                        .name("Authorization")      // Nombre del header
                        .type(SecurityScheme.Type.HTTP) // Tipo HTTP
                        .scheme("bearer")              // Esquema Bearer
                        .bearerFormat("JWT")           // Formato JWT
                        .in(SecurityScheme.In.HEADER)) // Se envía en el header
            );
    }
}
