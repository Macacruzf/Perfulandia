
package com.gestion.usuario.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios") 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa a un usuario del sistema")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    @Schema(description = "ID único del usuario", example = "1")
    private Long idUsuario;

    @Column(name = "nickname", nullable = false, unique = true, length = 50)
    @Schema(description = "Nombre de usuario o alias", example = "dwyer")
    private String nickname;

    @Column(name = "password", nullable = false, length = 100)
    @Schema(description = "Contraseña del usuario (encriptada)", example = "$2a$10$...")
    private String password;

    @Column(name = "correo", nullable = false, unique = true, length = 100)
    @Schema(description = "Correo electrónico del usuario", example = "dwyer@correo.com")
    private String correo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    @JsonIgnoreProperties("usuarios")
    @Schema(description = "Rol asociado al usuario")
    private Rol rol;
}
