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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.hateoas.RepresentationModel;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "productos")
@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa un producto en el sistema")
public class Producto extends RepresentationModel<Producto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idproducto")
    @Schema(description = "ID único del producto", example = "1")
    private Long idProducto;

    @Column(nullable = false)
    @Schema(description = "Nombre del producto", example = "Yara Rose")
    private String nombre;

    @Column
    @Schema(description = "Descripción del producto", example = "Aromasa floral con toques de rosa")
    private String descripcion;

    @Column(nullable = false)
    @Schema(description = "Precio unitario del producto", example = "9999")
    private Integer precioUnitario;

    @Column(nullable = false)
    @Schema(description = "Stock disponible del producto", example = "20")
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Estado actual del producto", example = "ACTIVO")
    private EstadoProducto estado;

    @ManyToOne
    @JoinColumn(name = "id_categoria", referencedColumnName = "idCategoria", nullable = false)
    @Schema(description = "Categoría a la que pertenece el producto")
    private Categoria categoria;
}
