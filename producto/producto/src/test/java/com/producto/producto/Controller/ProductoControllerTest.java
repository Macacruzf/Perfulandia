package com.producto.producto.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.producto.producto.model.Categoria;
import com.producto.producto.model.EstadoProducto;
import com.producto.producto.model.Producto;
import com.producto.producto.service.ProductoService;
import com.producto.producto.webclient.UsuarioClient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simula peticiones HTTP para probar el controlador

    @MockBean
    private ProductoService productoService; // Simula la lógica del servicio

    @MockBean
    private UsuarioClient usuarioClient; // Simula la llamada al microservicio de usuario

    private final ObjectMapper objectMapper = new ObjectMapper(); // Convierte objetos Java en JSON y viceversa

    @Test
    public void testObtenerProductos() throws Exception {
        // Creamos un producto simulado
        Producto producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombre("Producto de prueba");
        producto.setDescripcion("Descripción de prueba");
        producto.setPrecioUnitario(1000);
        producto.setStock(10);
        producto.setEstado(EstadoProducto.ACTIVO);
        producto.setCategoria(new Categoria(1L, "Categoría A", "Descripción"));

        List<Producto> productos = Collections.singletonList(producto);

        // Simulamos el resultado del servicio
        when(productoService.getProductos(null, null)).thenReturn(productos);

        // Realizamos la petición GET y esperamos una respuesta 200 OK
        mockMvc.perform(get("/api/v1/productos/productos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nombre").value("Producto de prueba"));
    }

    @Test
    public void testAgregarProducto_AdminTienePermiso() throws Exception {
        // Creamos un producto nuevo
        Producto producto = new Producto();
        producto.setNombre("Producto nuevo");
        producto.setDescripcion("Nuevo producto");
        producto.setPrecioUnitario(1500);
        producto.setStock(5);
        producto.setEstado(EstadoProducto.ACTIVO);
        producto.setCategoria(new Categoria(1L, "Categoría A", "Descripción"));

        // Simulamos que el usuario tiene rol ADMIN
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("privilegio", "ADMIN");

        when(usuarioClient.getUsuarioById(1L)).thenReturn(usuario);
        when(productoService.agregarProducto(any(Producto.class))).thenReturn(producto);

        // Realizamos una petición POST con el objeto convertido a JSON
        mockMvc.perform(post("/api/v1/productos/productos?idUsuario=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
            .andExpect(status().isCreated()) // Esperamos 201 CREATED
            .andExpect(jsonPath("$.nombre").value("Producto nuevo"));
    }

    @Test
    public void testEliminarProducto_ProductoExiste() throws Exception {
        // No necesitamos simular nada porque eliminarProducto no retorna nada

        // Llamada sin errores
        doNothing().when(productoService).eliminarProducto(1L);

        // Realizamos una petición DELETE y esperamos una respuesta 200 OK
        mockMvc.perform(delete("/api/v1/productos/productos/1"))
            .andExpect(status().isOk());
    }
}
