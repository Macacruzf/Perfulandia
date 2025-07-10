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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comunas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una comuna")
public class Comuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID de la comuna", example = "1")
    private Long idComuna;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre de la comuna", example = "Providencia")
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_region", nullable = false)
    @Schema(description = "Región a la que pertenece")
    private Region region;
}