
package com.gestion.usuario.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa un rol de usuario")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    @Schema(description = "ID del rol", example = "1")
    private Long id;

    @Column(name = "nombre_rol", nullable = false, unique = true, length = 50)
    @Schema(description = "Nombre del rol", example = "Admin")
    private String nombreRol;

    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Usuario> usuarios;
}
