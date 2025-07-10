package com.autenticado.autenticado.model;

import org.springframework.hateoas.RepresentationModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Modelo de usuario con soporte HATEOAS")
public class Usuario extends RepresentationModel<Usuario> {

    @Schema(description = "ID del usuario", example = "5")
    private Long idUsuario;

    @Schema(description = "Nombre de usuario (nickname)", example = "dwyer")
    private String nickname;

    @Schema(description = "Correo del usuario", example = "dwyer@correo.com")
    private String correo;

    @Schema(description = "Contraseña encriptada del usuario", example = "$2a$10...")
    private String password;

    @Schema(description = "Rol del usuario")
    private Rol rol;
}