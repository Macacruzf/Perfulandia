package com.producto.producto.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "productos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Producto {

    // Identificador único del producto (clave primaria)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idproducto")
    private Long idProducto;

    // Nombre del producto (campo obligatorio)
    @Column(nullable = false)
    private String nombre;

    // Descripción opcional del producto
    @Column
    private String descripcion;

    // Precio unitario del producto (obligatorio)
    @Column(nullable = false)
    private Integer precioUnitario;

    // Stock disponible del producto (obligatorio)
    @Column(nullable = false)
    private Integer stock;

    // Estado actual del producto, almacenado como texto en la base de datos
    // Solo puede tener uno de los valores definidos en el enum EstadoProducto
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProducto estado;

    // Relación con la categoría a la que pertenece el producto (obligatorio)
    @ManyToOne
    @JoinColumn(name = "id_categoria", referencedColumnName = "idCategoria", nullable = false)
    private Categoria categoria;
}
