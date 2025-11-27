package com.example.direccion.model;


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
@Table(name = "regiones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una región geográfica")
public class Region{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID de la región", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idRegion;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre de la región", example = "Región Metropolitana", required = true)
    private String nombre;
}