package com.example.direccion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import com.example.direccion.model.Region;
import com.example.direccion.service.RegionService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Tag(name = "Regiones", description = "Operaciones CRUD para regiones")
@RestController
@RequestMapping("/api/v1/regiones")
public class RegionController {

    @Autowired
    private RegionService regionService;

    private EntityModel<Region> toModel(Region region) {
        return EntityModel.of(region,
                linkTo(methodOn(RegionController.class).obtenerRegion(region.getIdRegion())).withSelfRel(),
                linkTo(methodOn(RegionController.class).listarRegiones()).withRel("todas"));
    }

    private ResponseEntity<Map<String, Object>> respuestaExitosa(String mensaje, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", mensaje);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar todas las regiones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de regiones obtenida exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "400", description = "Error de solicitud")
    })
    @GetMapping
    public ResponseEntity<?> listarRegiones() {
        try {
            List<EntityModel<Region>> modelos = regionService.obtenerTodas().stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
            return respuestaExitosa("Lista de regiones obtenida exitosamente", CollectionModel.of(modelos,
                    linkTo(methodOn(RegionController.class).listarRegiones()).withSelfRel()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener región por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Región encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "400", description = "ID inválido o región no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerRegion(@PathVariable Long id) {
        try {
            Region region = regionService.obtenerPorId(id);
            return respuestaExitosa("Región encontrada", toModel(region));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Crear nueva región")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Región creada exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<?> crearRegion(@Valid @RequestBody Region region) {
        try {
            Region creada = regionService.crear(region);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Región creada exitosamente");
            response.put("data", toModel(creada));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Actualizar región existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Región actualizada exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "400", description = "Error de solicitud")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarRegion(@PathVariable Long id, @Valid @RequestBody Region region) {
        try {
            Region actualizada = regionService.actualizar(id, region);
            return respuestaExitosa("Región actualizada exitosamente", toModel(actualizada));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Eliminar una región")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Región eliminada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "400", description = "ID inválido o región no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRegion(@PathVariable Long id) {
        try {
            regionService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
