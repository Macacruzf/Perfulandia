package com.example.gestionventas.model;


import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta; // Cambiado de 'id' a 'idVenta'

    @Column(nullable = false)
    private Long idUsuario; // Asociado al microservicio de usuario

    @Column(nullable = false)
    private Long idDireccion; // Asociado al microservicio de dirección

    @Column(nullable = false)
    private Long idEstadoEnvio; // Asociado al microservicio de estado (tabla estado_envio)

    @Column(nullable = false)
    private LocalDateTime fechaventa; // Cambiado de 'fecha' a 'fecha_venta'

    @Column(nullable = false)
    private BigDecimal total;

}


