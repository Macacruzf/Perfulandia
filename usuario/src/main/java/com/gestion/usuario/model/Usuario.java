
package com.gestion.usuario.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
public class Usuario {
   /**
     * Nombre de usuario o apodo (debe ser único).
     */
    @Column(name = "nickname", nullable = false, unique = true, length = 50)
    private String nickname;

    /**
     * Contraseña del usuario (encriptada). No se muestra en las respuestas JSON.
     */
    @JsonIgnore
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    /**
     * Correo electrónico del usuario (debe ser único).
     */
    @Column(name = "correo", nullable = false, unique = true, length = 100)
    private String correo;

    /**
     * Rol al que pertenece el usuario.
     * Se usa para determinar qué acciones puede realizar en el sistema.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    @JsonIgnoreProperties("usuarios") // Evita recursividad infinita al convertir a JSON
    private Rol rol;
}
