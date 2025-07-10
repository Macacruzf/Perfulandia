package com.example.direccion.model;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "direcciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una dirección física de un usuario")
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la dirección", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idDireccion;

    @Column(nullable = false)
    @Schema(description = "ID del usuario asociado a la dirección", example = "12", required = true)
    private Long idUsuario;
    
    
    @ManyToOne
    @JoinColumn(name = "idComuna", nullable = false)
    @Valid
    @Schema(description = "Comuna asociada a la dirección")
    private Comuna comuna;

    @ManyToOne
    @JoinColumn(name = "id_region", referencedColumnName = "idRegion", nullable = false)
    @Valid
    @Schema(description = "Región asociada a la dirección")
    private Region region;

    @Size(max = 200)
    @Column(nullable = false)
    @Schema(description = "Nombre de la calle", example = "Av. Providencia", required = true)
    private String calle;

    @Column(length = 10)
    @Schema(description = "Número del domicilio", example = "1234")
    private String numero;

    @Column(length = 120)
    @Schema(description = "Número de departamento ", example = "Dpto. 202")
    private String departamento;

    @Column(length = 12)
    @Schema(description = "Código postal", example = "8320000")
    private String codigoPostal;
}
