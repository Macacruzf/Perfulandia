package com.example.tickets.model;


import java.io.Serializable;
import java.time.LocalDateTime;

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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ticket")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Entidad que representa un ticket de soporte o consulta")
public class Ticket implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del ticket", example = "1001")
    private Long idTicket;

    @Column(nullable = false)
    @Schema(description = "Fecha y hora de creación del ticket", example = "2025-06-21T14:30:00")
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    @Schema(description = "ID del usuario que creó el ticket", example = "15")
    private Long idUsuario;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Schema(description = "Comentario o descripción del problema", example = "La impresora no enciende")
    private String comentario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idMotivo", nullable = false)
    @Schema(description = "Motivo asociado al ticket")
    private Motivo motivo;
}

