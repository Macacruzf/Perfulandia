
package com.gestion.privilegio.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "modulo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un módulo del sistema")
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del módulo", example = "1")
    private Long idModulo;
    
    //Nombre del módulo (por ejemplo: "productos", "usuarios", "ventas") 
    @Column(nullable = false, unique = true)
    @Schema(description = "Nombre del módulo", example = "Usuarios")
    private String nombre;
}




