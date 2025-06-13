package com.example.tickets.model;


import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Data
@Table(name = "tickets")
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ticket")
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime fCreacion;

    @Column(nullable = false)
    private Long idUsers;  // ID del usuario que creó el ticket

    @ManyToOne
    @JoinColumn(name = "id_motivo", nullable = false)
    private Motivo motivo;  // Aquí se añade el campo motivo

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mensaje> mensajes;

}


