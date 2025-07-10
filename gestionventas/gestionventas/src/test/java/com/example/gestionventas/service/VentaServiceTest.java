package com.example.gestionventas.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.gestionventas.Repository.DetalleVentaRepository;
import com.example.gestionventas.Repository.VentaRepository;
import com.example.gestionventas.model.DetalleVenta;
import com.example.gestionventas.model.Venta;
import com.example.gestionventas.webclient.DireccionClient;
import com.example.gestionventas.webclient.ProductoClient;
import com.example.gestionventas.webclient.UsuarioClient;

@ExtendWith(MockitoExtension.class)
public class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @Mock
    private UsuarioClient clienteClient;

    @Mock
    private DireccionClient direccionClient;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private VentaService ventaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetVentas_returnsList() {
        when(ventaRepository.findAll()).thenReturn(List.of(new Venta()));
        List<Venta> result = ventaService.getVentas();
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetVentaById_success() {
        Venta venta = new Venta();
        venta.setIdVenta(1L);
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        Venta result = ventaService.getVentaById(1L);
        assertEquals(venta, result);
    }

    @Test
    void testGetVentaById_notFound() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> ventaService.getVentaById(1L));
    }

    @Test
    void testDeleteVenta_success() {
        Venta venta = new Venta();
        venta.setIdVenta(1L);
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(detalleVentaRepository.findByVenta_IdVenta(1L)).thenReturn(List.of());
        assertDoesNotThrow(() -> ventaService.deleteVenta(1L));
    }

    @Test
    void testDeleteVenta_notFound() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> ventaService.deleteVenta(1L));
    }

    @Test
    void testUpdateVenta_success() {
        Venta existing = new Venta();
        existing.setIdVenta(1L);
        existing.setIdUsuario(1L);

        Venta updated = new Venta();
        updated.setIdUsuario(2L);
        updated.setIdDireccion(2L);
        updated.setFechaVenta(LocalDateTime.now());
        updated.setTotal(200);

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(ventaRepository.save(any(Venta.class))).thenReturn(updated);

        Venta result = ventaService.updateVenta(1L, updated);
        assertEquals(2L, result.getIdUsuario());
    }

    @Test
    void testGetDetallesByVentaId_success() {
        DetalleVenta detalle = new DetalleVenta();
        when(detalleVentaRepository.findByVenta_IdVenta(1L)).thenReturn(List.of(detalle));
        List<DetalleVenta> detalles = ventaService.getDetallesByVentaId(1L);
        assertFalse(detalles.isEmpty());
    }

    @Test
    void testRegistrarVenta_success() {
        Venta venta = new Venta();
        venta.setIdUsuario(1L);
        venta.setIdDireccion(2L);

        DetalleVenta detalle = new DetalleVenta();
        detalle.setIdProducto(10L);
        detalle.setCantidad(2);

        List<DetalleVenta> detalles = List.of(detalle);

        when(clienteClient.getUsuarioById(1L)).thenReturn(Map.of("nombre", "Cliente"));
        when(direccionClient.getDireccionById(2L)).thenReturn(Map.of("direccion", "Av. X"));
        when(productoClient.getProductoById(10L)).thenReturn(Map.of("stock", 10, "precio", 50));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(i -> i.getArgument(0));
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(new DetalleVenta());
        doNothing().when(productoClient).actualizarStockBulk(any());

        Venta result = ventaService.registrarVenta(venta, detalles);
        assertNotNull(result.getFechaVenta());
        assertEquals(100, result.getTotal());
    }

    @Test
    void testRegistrarVenta_clienteNoExiste() {
        Venta venta = new Venta();
        venta.setIdUsuario(1L);
        venta.setIdDireccion(2L);
        when(clienteClient.getUsuarioById(1L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> ventaService.registrarVenta(venta, List.of()));
    }

    @Test
    void testRegistrarVenta_productoSinStock() {
        Venta venta = new Venta();
        venta.setIdUsuario(1L);
        venta.setIdDireccion(2L);

        DetalleVenta detalle = new DetalleVenta();
        detalle.setIdProducto(10L);
        detalle.setCantidad(10);

        when(clienteClient.getUsuarioById(1L)).thenReturn(Map.of("nombre", "Cliente"));
        when(direccionClient.getDireccionById(2L)).thenReturn(Map.of("direccion", "Av. X"));
        when(productoClient.getProductoById(10L)).thenReturn(Map.of("stock", 5, "precio", 50));

        assertThrows(RuntimeException.class, () -> ventaService.registrarVenta(venta, List.of(detalle)));
    }
}



