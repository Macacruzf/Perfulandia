package com.example.tickets.model;

import java.time.LocalDateTime;

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
@Data
@Table(name = "mensajes")
@AllArgsConstructor
@NoArgsConstructor
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMensaje;  // Clave primaria

    @Column(nullable = false)
    private LocalDateTime fMensaje = LocalDateTime.now();  // Fecha del mensaje

    @Column(nullable = false, length = 500)
    private String mensaje;  // Contenido del mensaje

    @ManyToOne
    @JoinColumn(name = "id_ticket", nullable = false)
    private Ticket ticket;  // Relación con Ticket

    @Column(nullable = false)
    private Long idUsers;  // ID del usuario que envió el mensaje

    @Column(nullable = false)
    private String tipo;  // Tipo del mensaje (puedes cambiar por enum)
}