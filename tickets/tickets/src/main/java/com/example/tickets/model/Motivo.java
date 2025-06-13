package com.example.tickets.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name ="motivos")
@AllArgsConstructor
@NoArgsConstructor

public class Motivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMotivo;  

    @Column(nullable = false)
    private String nombre;  


    

}
