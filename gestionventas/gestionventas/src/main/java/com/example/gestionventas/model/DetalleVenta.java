package com.example.gestionventas.model;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalleventa")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Representa el detalle de una venta: producto, cantidad, subtotal y venta asociada.")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    @Schema(description = "ID del detalle auto-incrementable")
    private Long idDetalle;

    @Column(name = "id_producto", nullable = false)
    @Schema(description = "ID del producto vendido")
    private Long idProducto;

    @Column(nullable = false)
    @Schema(description = "Cantidad de unidades vendidas")
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    @Schema(description = "Precio unitario del producto en el momento de la venta")
    private Integer precioUnitario;

    @Column(nullable = false)
    @Schema(description = "Subtotal de este producto en la venta")
    private Integer subtotal;

    @ManyToOne
    @JoinColumn(name = "id_venta", nullable = false)
    @Schema(description = "Venta a la que pertenece este detalle")
    private Venta venta;
}