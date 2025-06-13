
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPrivilegio;

    @Column(nullable = false)
    private String nombreRol; //  "Admin", "Cliente", etc.

    @Column(nullable = false, length = 500)
    private String mensaje;

    @ManyToOne
    @JoinColumn(name = "modulo_id")
    private Modulo modulo;

}
