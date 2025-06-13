package com.example.gestionventas.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gestionventas.Repository.DetalleVentaRepository;
import com.example.gestionventas.Repository.VentaRepository;
import com.example.gestionventas.model.DetalleVenta;
import com.example.gestionventas.model.Venta;
import com.example.gestionventas.webclient.UsuarioClient;
import com.example.gestionventas.webclient.DireccionClient;
import com.example.gestionventas.webclient.EstadoClient;
import com.example.gestionventas.webclient.ProductoClient;


import jakarta.transaction.Transactional;


@Service
@Transactional
public class VentaService {
    @Autowired
    private VentaRepository ventaRepository;
    @Autowired
    private DetalleVentaRepository detalleVentaRepository;
    @Autowired
    private UsuarioClient clienteClient;
    @Autowired
    private DireccionClient direccionClient;
    @Autowired
    private EstadoClient estadoClient;
    @Autowired
    private ProductoClient productoClient;

    public List<Venta> getVentas() {
        return ventaRepository.findAll();
    }

    public List<DetalleVenta> getDetallesVentaByProductoId(Long idProducto) {
        return detalleVentaRepository.findByIdProducto(idProducto);
    }

    public Venta registrarVenta(Venta venta, List<DetalleVenta> detalles) {
        // Validar cliente
        Map<String, Object> cliente = clienteClient.getUsuarioById(venta.getIdUsuario());
        if (cliente == null || cliente.isEmpty()) {
            throw new RuntimeException("Cliente no encontrado");
        }

        // Validar dirección
        Map<String, Object> direccion = direccionClient.getDireccionById(venta.getIdDireccion());
        if (direccion == null || direccion.isEmpty()) {
            throw new RuntimeException("Dirección no encontrada");
        }

        // Validar estado_envio
        Map<String, Object> estadoEnvio = estadoClient.getEstadoEnvioById(venta.getIdEstadoEnvio());
        if (estadoEnvio == null || estadoEnvio.isEmpty()) {
            throw new RuntimeException("Estado de envío no encontrado");
        }

        // Validar productos, calcular subtotales con descuentos y preparar actualizaciones de stock
        BigDecimal total = BigDecimal.ZERO;
        List<Map<String, Object>> stockUpdates = new ArrayList<>();
        for (DetalleVenta detalle : detalles) {
            Map<String, Object> producto = productoClient.getProductoById(detalle.getIdProducto());
            if (producto == null || producto.isEmpty()) {
                throw new RuntimeException("Producto no encontrado: " + detalle.getIdProducto());
            }
            Integer stock = (Integer) producto.get("stock");
            if (stock < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + detalle.getIdProducto());
            }

            // Obtener descuentos del producto con verificación de tipo
            BigDecimal precio = new BigDecimal(producto.get("precio_unitario").toString());
            BigDecimal descuentoPorcentaje = BigDecimal.ZERO;
            Long idDescuento = null;

            Object descuentosObj = producto.get("descuentos");
            if (descuentosObj instanceof List) {
                List<?> descuentosRaw = (List<?>) descuentosObj;
                for (Object descuento : descuentosRaw) {
                    if (descuento instanceof Map) {
                        Map<?, ?> descuentoMap = (Map<?, ?>) descuento;
                        try {
                            LocalDateTime fechaInicio = LocalDateTime.parse((String) descuentoMap.get("fechaInicio"));
                            LocalDateTime fechaFin = LocalDateTime.parse((String) descuentoMap.get("fechaFin"));
                            LocalDateTime ahora = LocalDateTime.now();
                            if (ahora.isAfter(fechaInicio) && ahora.isBefore(fechaFin)) {
                                descuentoPorcentaje = new BigDecimal(descuentoMap.get("porcentaje").toString());
                                idDescuento = Long.valueOf(descuentoMap.get("descuentoId").toString());
                                break;
                            }
                        } catch (Exception e) {
                            // Ignorar descuentos con formato inválido
                            continue;
                        }
                    }
                }
            }

            // Calcular subtotal con descuento
            BigDecimal precioConDescuento = precio.multiply(BigDecimal.ONE.subtract(descuentoPorcentaje.divide(BigDecimal.valueOf(100))));
            detalle.setSubtotal(precioConDescuento.multiply(BigDecimal.valueOf(detalle.getCantidad())));
            detalle.setIdDescuento(idDescuento); // Asignar idDescuento al detalle
            total = total.add(detalle.getSubtotal());

            // Preparar actualización de stock
            stockUpdates.add(Map.of("productoId", detalle.getIdProducto(), "cantidad", detalle.getCantidad()));
        }

        // Actualizar stock en bloque
        try {
            productoClient.actualizarStockBulk(stockUpdates);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al actualizar stock: " + e.getMessage());
        }

        // Guardar venta
        venta.setFechaventa(LocalDateTime.now());
        venta.setTotal(total);
        Venta savedVenta = ventaRepository.save(venta);

        // Guardar detalles
        for (DetalleVenta detalle : detalles) {
            detalle.setVenta(savedVenta);
            detalleVentaRepository.save(detalle);
        }

        return savedVenta;
    }

    public String generarFactura(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        List<DetalleVenta> detalles = detalleVentaRepository.findByIdVenta(ventaId);
        Map<String, Object> cliente = clienteClient.getUsuarioById(venta.getIdUsuario());
        Map<String, Object> direccion = direccionClient.getDireccionById(venta.getIdDireccion());
        Map<String, Object> estadoEnvio = estadoClient.getEstadoEnvioById(venta.getIdEstadoEnvio());

        StringBuilder factura = new StringBuilder();
        factura.append("Factura Electrónica\n");
        factura.append("Cliente: ").append(cliente.get("nombre")).append("\n");
        factura.append("Dirección: ").append(direccion.get("direccion")).append("\n");
        factura.append("Estado de Envío: ").append(estadoEnvio.get("estado")).append("\n");
        factura.append("Fecha: ").append(venta.getFechaventa()).append("\n");
        factura.append("Total: ").append(venta.getTotal()).append("\n");
        factura.append("Detalles:\n");
        for (DetalleVenta detalle : detalles) {
            Map<String, Object> producto = productoClient.getProductoById(detalle.getIdProducto());
            factura.append("- ").append(producto.get("nombre"))
                    .append(": ").append(detalle.getCantidad())
                    .append(" = ").append(detalle.getSubtotal());
            if (detalle.getIdDescuento() != null) {
                Object descuentosObj = producto.get("descuentos");
                if (descuentosObj instanceof List) {
                    List<?> descuentosRaw = (List<?>) descuentosObj;
                    for (Object descuento : descuentosRaw) {
                        if (descuento instanceof Map) {
                            Map<?, ?> descuentoMap = (Map<?, ?>) descuento;
                            if (Long.valueOf(descuentoMap.get("descuentoId").toString()).equals(detalle.getIdDescuento())) {
                                factura.append(" (Descuento ID: ").append(detalle.getIdDescuento())
                                        .append(", ").append(descuentoMap.get("porcentaje")).append("% )");
                                break;
                            }
                        }
                    }
                }
            }
            factura.append("\n");
        }
        return factura.toString();
    }
}