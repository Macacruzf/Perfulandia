package com.producto.producto.model;

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
@Table(name = "productodescuento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDescuento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProductodescuento;

    @ManyToOne
    @JoinColumn(name = "idproducto", nullable = false) // Nombre de columna en BD
    private Producto producto; // Relación N:1

    @Column(nullable = false, name = "idDescuento")
    private Long idDescuento; // ID del descuento (microservicio Promoción)
}
