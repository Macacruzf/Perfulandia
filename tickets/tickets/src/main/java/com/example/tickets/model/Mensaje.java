package com.example.tickets.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "mensaje")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Entidad que representa un mensaje relacionado a un ticket")
public class Mensaje implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del mensaje", example = "1")
    private Long idMensaje;

    @Column(nullable = false)
    @Schema(description = "Fecha y hora del mensaje", example = "2025-06-21T14:00:00")
    private LocalDateTime fechaMensaje;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Schema(description = "Contenido del mensaje", example = "Se resolvió el problema.")
    private String mensaje;

    @Column(nullable = false)
    @Schema(description = "Tipo del mensaje", example = "comentario")
    private String tipoMensaje;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "idTicket", nullable = false)
    @Schema(description = "Ticket al que pertenece el mensaje")
    private Ticket ticket;

    @Column(nullable = false)
    @Schema(description = "ID del usuario que escribió el mensaje", example = "5")
    private Long idUsuario;
}