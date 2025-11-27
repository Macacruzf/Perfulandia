package com.example.gestionventas.service;

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
    private ProductoClient productoClient;

    // Obtener todas las ventas registradas
    public List<Venta> getVentas() {
        return ventaRepository.findAll();
    }

    // Obtener una venta por su ID
    public Venta getVentaById(Long id) {
        return ventaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    // Obtener los detalles de una venta específica
    public List<DetalleVenta> getDetallesByVentaId(Long idVenta) {
        return detalleVentaRepository.findByVenta_IdVenta(idVenta);
    }

    // Crear una nueva venta con sus detalles
    public Venta createVenta(Venta venta, List<DetalleVenta> detalles) {
        return registrarVenta(venta, detalles);
    }

    // Actualizar una venta existente (solo cabecera, no detalles)
    public Venta updateVenta(Long id, Venta ventaActualizada) {
        Venta ventaExistente = ventaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        ventaExistente.setIdUsuario(ventaActualizada.getIdUsuario());
        ventaExistente.setIdDireccion(ventaActualizada.getIdDireccion());
        ventaExistente.setFechaVenta(ventaActualizada.getFechaVenta());
        ventaExistente.setTotal(ventaActualizada.getTotal());

        return ventaRepository.save(ventaExistente);
    }

    // Eliminar una venta junto con sus detalles
    public void deleteVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        List<DetalleVenta> detalles = detalleVentaRepository.findByVenta_IdVenta(id);
        detalleVentaRepository.deleteAll(detalles);
        ventaRepository.delete(venta);
    }

    // Registrar venta y detalles con validaciones
    public Venta registrarVenta(Venta venta, List<DetalleVenta> detalles) {

        Map<String, Object> cliente = clienteClient.getUsuarioById(venta.getIdUsuario());
        if (cliente == null || cliente.isEmpty()) {
            throw new RuntimeException("Cliente no encontrado");
        }

        Map<String, Object> direccion = direccionClient.getDireccionById(venta.getIdDireccion());
        if (direccion == null || direccion.isEmpty()) {
            throw new RuntimeException("Dirección no encontrada");
        }

        Integer total = 0;
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

            Integer precio = Integer.valueOf(producto.get("precio").toString());
            Integer subtotal = precio * detalle.getCantidad();

            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(subtotal);

            total += subtotal;

            stockUpdates.add(Map.of("productoId", detalle.getIdProducto(), "cantidad", detalle.getCantidad()));
        }

    try {
        productoClient.actualizarStockBulk(stockUpdates);
    } catch (RuntimeException e) {
        throw new RuntimeException("Error al actualizar stock: " + e.getMessage());
    }

    venta.setFechaVenta(LocalDateTime.now());
    venta.setTotal(total);

    Venta savedVenta = ventaRepository.save(venta);

    for (DetalleVenta detalle : detalles) {
        detalle.setVenta(savedVenta);
        detalleVentaRepository.save(detalle);
    }

    return savedVenta;
}

}
