package com.producto.producto.model;



// Enumeración de estados posibles para un producto
public enum EstadoProducto {
    ACTIVO,         // Producto visible y disponible para la venta
    INACTIVO,       // Producto oculto o no disponible temporalmente
    EN_REVISION,    // Producto pendiente de aprobación o revisión
    AGOTADO,        // Producto sin stock disponible
    DESCONTINUADO   // Producto fuera del catálogo permanentemente
}