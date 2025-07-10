package com.example.direccion.controller;


import com.example.direccion.model.Direccion;
import com.example.direccion.service.DireccionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Tag(name = "Direcciones", description = "Operaciones CRUD para direcciones")
@RestController
@RequestMapping("/api/v1/direcciones")
public class DireccionController {

    @Autowired
    private DireccionService direccionService;

    private EntityModel<Direccion> toModel(Direccion direccion) {
        return EntityModel.of(direccion,
            linkTo(methodOn(DireccionController.class).obtenerPorId(direccion.getIdDireccion())).withSelfRel(),
            linkTo(methodOn(DireccionController.class).listarTodas()).withRel("todas"));
    }

    private ResponseEntity<Map<String, Object>> respuestaExitosa(String mensaje, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", mensaje);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar todas las direcciones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de direcciones obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    })
    @GetMapping
    public ResponseEntity<?> listarTodas() {
        try {
            List<Direccion> direcciones = direccionService.obtenerTodas();
            List<EntityModel<Direccion>> modelos = direcciones.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
            return respuestaExitosa("Lista de direcciones obtenida correctamente", modelos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener una dirección por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dirección obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Direccion direccion = direccionService.obtenerPorId(id);
            return respuestaExitosa("Dirección obtenida correctamente", toModel(direccion));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener direcciones por ID de comuna")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Direcciones obtenidas correctamente"),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    })
    @GetMapping("/comuna/{idComuna}")
    public ResponseEntity<?> listarPorComuna(@PathVariable Long idComuna) {
        try {
            List<Direccion> direcciones = direccionService.obtenerPorComuna(idComuna);
            List<EntityModel<Direccion>> modelos = direcciones.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
            return respuestaExitosa("Direcciones obtenidas correctamente", modelos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Crear una nueva dirección")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dirección creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Error de validación")
    })
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Direccion direccion) {
        try {
            Direccion creada = direccionService.crearDireccion(direccion);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Dirección creada correctamente", "data", toModel(creada)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Actualizar una dirección existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dirección actualizada correctamente"),
        @ApiResponse(responseCode = "400", description = "Error de validación")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Direccion direccion) {
        try {
            Direccion actualizada = direccionService.actualizarDireccion(id, direccion);
            return respuestaExitosa("Dirección actualizada correctamente", toModel(actualizada));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Eliminar una dirección")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Dirección eliminada correctamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            direccionService.eliminarDireccion(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}