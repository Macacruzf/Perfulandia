package com.example.direccion.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import com.example.direccion.model.Comuna;
import com.example.direccion.service.ComunaService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Comunas", description = "Operaciones CRUD para comunas")
@RestController
@RequestMapping("/api/v1/comunas")
public class ComunaController {

    @Autowired
    private ComunaService comunaService;

    private EntityModel<Comuna> toModel(Comuna comuna) {
        return EntityModel.of(comuna,
            linkTo(methodOn(ComunaController.class).obtenerComuna(comuna.getIdComuna())).withSelfRel(),
            linkTo(methodOn(ComunaController.class).listarComunas()).withRel("todas"));
    }

    private ResponseEntity<Map<String, Object>> respuestaExitosa(String mensaje, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", mensaje);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar todas las comunas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de comunas obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    })
    @GetMapping
    public ResponseEntity<?> listarComunas() {
        try {
            List<Comuna> comunas = comunaService.obtenerTodas();
            List<EntityModel<Comuna>> modelos = comunas.stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
            return respuestaExitosa("Lista de comunas obtenida correctamente", modelos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener una comuna por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comuna obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerComuna(@PathVariable Long id) {
        try {
            Comuna comuna = comunaService.obtenerPorId(id);
            return respuestaExitosa("Comuna obtenida correctamente", toModel(comuna));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Crear una nueva comuna")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Comuna creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Error de validación")
    })
    @PostMapping
    public ResponseEntity<?> crearComuna(@Valid @RequestBody Comuna comuna) {
        try {
            Comuna creada = comunaService.crear(comuna);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensaje", "Comuna creada correctamente",
                "data", toModel(creada)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Actualizar una comuna")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comuna actualizada correctamente"),
        @ApiResponse(responseCode = "400", description = "Error de validación")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarComuna(@PathVariable Long id, @Valid @RequestBody Comuna comuna) {
        try {
            Comuna actualizada = comunaService.actualizar(id, comuna);
            return respuestaExitosa("Comuna actualizada correctamente", toModel(actualizada));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Eliminar una comuna")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Comuna eliminada correctamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarComuna(@PathVariable Long id) {
        try {
            comunaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}


