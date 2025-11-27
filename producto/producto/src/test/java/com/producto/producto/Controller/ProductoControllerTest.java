package com.producto.producto.Controller;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.producto.producto.config.SecurityConfig;
import com.producto.producto.model.Categoria;
import com.producto.producto.model.EstadoProducto;
import com.producto.producto.model.Producto;
import com.producto.producto.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.security.Principal;
import java.util.Collections;

import java.util.List;


@ActiveProfiles("test")
@WebMvcTest(ProductoController.class)
@Import(SecurityConfig.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    private RequestPostProcessor adminAuth() {
        return SecurityMockMvcRequestPostProcessors.httpBasic("admin", "admin");
    }

    @Test
    void agregarProductoComoAdmin_deberiaCrear() throws Exception {
        Producto producto = new Producto(1L, "Perfume X", "Aroma cítrico", 15000, 10, EstadoProducto.ACTIVO, new Categoria());

        when(productoService.agregarProducto(any(Producto.class))).thenReturn(producto);

        mockMvc.perform(post("/api/v1/productos")
                .with(adminAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nombre").value("Perfume X"));
    }

    @Test
    @WithMockUser // Usuario autenticado pero sin rol ADMIN
    void agregarProductoComoNoAdmin_deberiaDenegar() throws Exception {
        Producto producto = new Producto(1L, "Perfume Básico", "Aroma suave", 8000, 10, EstadoProducto.ACTIVO, new Categoria());

        mockMvc.perform(post("/api/v1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
            .andExpect(status().isForbidden()); // Ahora correctamente espera 403
    }
    @Test
    @WithMockUser
    void obtenerProductoPorId_existente_deberiaRetornarProducto() throws Exception {
        Producto producto = new Producto(1L, "Perfume A", "Aroma fresco", 9000, 5, EstadoProducto.ACTIVO, new Categoria());
        when(productoService.getProductoConDetalles(1L)).thenReturn(producto);

        mockMvc.perform(get("/api/v1/productos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Perfume A"));
    }

    @Test
    void actualizarProductoComoAdmin_deberiaActualizar() throws Exception {
        Producto producto = new Producto(1L, "Perfume Editado", "Actualizado", 18000, 8, EstadoProducto.ACTIVO, new Categoria());

        when(productoService.actualizarProducto(eq(1L), any(Producto.class))).thenReturn(producto);

        mockMvc.perform(put("/api/v1/productos/1")
                .with(adminAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Perfume Editado"));
    }

    @Test
    void eliminarProductoComoAdmin_deberiaEliminar() throws Exception {
        doNothing().when(productoService).eliminarProducto(1L);

        mockMvc.perform(delete("/api/v1/productos/1")
                .with(adminAuth()))
            .andExpect(status().isOk());
    }
}