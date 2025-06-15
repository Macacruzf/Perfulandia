
package com.gestion.privilegio.model;

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
public class Privilegio {
    /** ID único del privilegio */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPrivilegio;

    /** Nombre del rol asociado al privilegio (como texto plano) */
    @Column(nullable = false)
    private String nombreRol;

    /** Módulo sobre el cual se otorgan los permisos del privilegio */
    @ManyToOne
    @JoinColumn(name = "id_modulo", nullable = false)
    private Modulo modulo;
    
}