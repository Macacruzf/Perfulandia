
package com.gestion.privilegio.model;

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
public class Modulo {
    /** ID único del módulo */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idModulo;
    
    /** Nombre del módulo (por ejemplo: "productos", "usuarios", "ventas") */
    @Column(nullable = false, unique = true)
    private String nombre;

}



