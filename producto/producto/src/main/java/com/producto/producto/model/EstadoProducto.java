package com.producto.producto.model;

import io.swagger.v3.oas.annotations.media.Schema;

// Enumeración de estados posibles para un producto
@Schema(description = "Estado actual del producto")
public enum EstadoProducto {

    @Schema(description = "Producto visible y disponible para la venta")
    ACTIVO,

    @Schema(description = "Producto oculto o no disponible temporalmente")
    INACTIVO,

    @Schema(description = "Producto pendiente de aprobación o revisión")
    EN_REVISION,

    @Schema(description = "Producto sin stock disponible")
    AGOTADO,

    @Schema(description = "Producto fuera del catálogo permanentemente")
    DESCONTINUADO
}