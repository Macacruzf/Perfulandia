
package com.gestion.privilegio.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "privilegio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un privilegio asociado a un módulo y rol")
public class Privilegio {

    //ID único del privilegio 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del privilegio", example = "1")
    private Long idPrivilegio;

    //Nombre del rol asociado al privilegio (como texto plano) 
    @Column(nullable = false)
    @Schema(description = "Nombre del rol que tiene el privilegio", example = "ADMIN")
    private String nombreRol;

    // Módulo sobre el cual se otorgan los permisos del privilegio 
    @ManyToOne
    @JoinColumn(name = "id_modulo", nullable = false)
    @Schema(description = "Módulo asociado al privilegio")
    private Modulo modulo;
}