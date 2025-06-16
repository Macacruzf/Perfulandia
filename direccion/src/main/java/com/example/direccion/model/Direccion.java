package com.example.direccion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "direcciones")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDireccion;

    @Column(nullable = false)
    private Long idUsuario;  

    @ManyToOne
    @JoinColumn(name = "idComuna", nullable = false)
    private Comuna comuna;

    @ManyToOne
    @JoinColumn(name = "id_region", referencedColumnName = "idRegion", nullable = false)
    private Region region;

    @NotBlank(message = "La calle no puede estar vacía")
    @Size(max = 200)
    @Column(nullable = false)
    private String calle;

    @Column(length = 10)
    private String numero;

    @Column(length = 120)
    private String departamento; 

    @Column(length = 12)
    private String codigoPostal; 
}
