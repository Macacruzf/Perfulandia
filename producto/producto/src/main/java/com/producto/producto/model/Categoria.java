package com.producto.producto.model;

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
@Table(name = "categorias")
@Data // Genera automáticamente getters, setters, equals, hashCode y toString
@AllArgsConstructor // Genera constructor con todos los atributos
@NoArgsConstructor  // Genera constructor vacío
public class Categoria {

    /**
     * ID único de la categoría (clave primaria, autogenerada).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;

    /**
     * Nombre de la categoría (obligatorio).
     * Ejemplos: "Tecnología", "Ropa", "Electrodomésticos".
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Descripción opcional de la categoría.
     * Puede incluir detalles adicionales sobre su uso o agrupación.
     */
    @Column
    private String descripcion;
}