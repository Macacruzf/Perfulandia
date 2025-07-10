package com.example.gestionventas.controller;


import com.example.gestionventas.model.DetalleVenta;
import com.example.gestionventas.model.Venta;
import com.example.gestionventas.service.VentaService;
import com.example.gestionventas.webclient.DireccionClient;
import com.example.gestionventas.webclient.ProductoClient;
import com.example.gestionventas.webclient.UsuarioClient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(VentaController.class)
public class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaService ventaService;

    @MockBean
    private UsuarioClient usuarioClient;

    @MockBean
    private DireccionClient direccionClient;

    @MockBean
    private ProductoClient productoClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllVentas_empty() throws Exception {
        when(ventaService.getVentas()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/v1/ventas"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAllVentas_success() throws Exception {
        Venta venta = new Venta();
        venta.setIdVenta(1L);
        venta.setIdUsuario(1L);
        venta.setIdDireccion(2L);
        venta.setTotal(100);
        venta.setFechaVenta(LocalDateTime.now());

        when(ventaService.getVentas()).thenReturn(List.of(venta));

        mockMvc.perform(get("/api/v1/ventas"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetVentaById_success() throws Exception {
        Venta venta = new Venta();
        venta.setIdVenta(1L);
        venta.setIdUsuario(1L);
        venta.setIdDireccion(2L);
        venta.setTotal(100);
        venta.setFechaVenta(LocalDateTime.now());

        when(ventaService.getVentaById(1L)).thenReturn(venta);

        mockMvc.perform(get("/api/v1/ventas/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetVentaById_notFound() throws Exception {
        when(ventaService.getVentaById(1L)).thenThrow(new RuntimeException("No existe"));

        mockMvc.perform(get("/api/v1/ventas/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteVenta_success() throws Exception {
        doNothing().when(ventaService).deleteVenta(1L);

        mockMvc.perform(delete("/api/v1/ventas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteVenta_notFound() throws Exception {
        doThrow(new RuntimeException("No encontrada")).when(ventaService).deleteVenta(1L);

        mockMvc.perform(delete("/api/v1/ventas/1"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void testCreateVenta_success() throws Exception {
        Map<String, Object> ventaData = new HashMap<>();
        ventaData.put("idUsuario", 1);
        ventaData.put("idDireccion", 2);

        Map<String, Object> detalle1 = Map.of("idProducto", 101, "cantidad", 2);
        Map<String, Object> detalle2 = Map.of("idProducto", 102, "cantidad", 1);

        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("venta", ventaData);
        requestPayload.put("detalles", List.of(detalle1, detalle2));

        when(usuarioClient.getUsuarioById(1L)).thenReturn(Map.of("nombre", "Usuario"));
        when(direccionClient.getDireccionById(2L)).thenReturn(Map.of("direccion", "Calle 123"));
        when(productoClient.getProductoById(101L)).thenReturn(Map.of("precio", 100));
        when(productoClient.getProductoById(102L)).thenReturn(Map.of("precio", 50));

        Venta ventaCreada = new Venta();
        ventaCreada.setIdVenta(1L);
        ventaCreada.setIdUsuario(1L);
        ventaCreada.setIdDireccion(2L);
        ventaCreada.setFechaVenta(LocalDateTime.now());
        ventaCreada.setTotal(250);

        when(ventaService.createVenta(any(), any())).thenReturn(ventaCreada);
        doNothing().when(productoClient).actualizarStockBulk(any());

        mockMvc.perform(post("/api/v1/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestPayload)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateVenta_success() throws Exception {
        Venta venta = new Venta();
        venta.setIdVenta(1L);
        venta.setIdUsuario(1L);
        venta.setIdDireccion(2L);
        venta.setTotal(100);
        venta.setFechaVenta(LocalDateTime.now());

        when(ventaService.updateVenta(eq(1L), any(Venta.class))).thenReturn(venta);

        mockMvc.perform(put("/api/v1/ventas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetDetallesByVentaId_success() throws Exception {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setIdProducto(101L);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(100);
        detalle.setSubtotal(200);

        when(ventaService.getDetallesByVentaId(1L)).thenReturn(List.of(detalle));

        mockMvc.perform(get("/api/v1/ventas/1/detalles"))
                .andExpect(status().isOk());
    }
}
