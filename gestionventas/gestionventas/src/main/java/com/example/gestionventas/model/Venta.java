package com.example.gestionventas.model;

import java.time.LocalDateTime;

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
@Table(name = "venta")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Representa una venta realizada por un usuario.")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    @Schema(description = "ID de la venta auto-incrementable")
    private Long idVenta;

    @Column(name = "id_usuario", nullable = false)
    @Schema(description = "ID del usuario que hizo la compra")
    private Long idUsuario;

    @Column(name = "id_direccion", nullable = false)
    @Schema(description = "ID de la dirección de envío")
    private Long idDireccion;

    @Column(name = "fecha_venta", nullable = false)
    @Schema(description = "Fecha de la venta")
    private LocalDateTime fechaVenta;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    @Schema(description = "Total de la venta")
    private Integer total;

}


