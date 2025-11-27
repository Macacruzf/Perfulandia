
package com.gestion.privilegio.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.privilegio.model.Modulo;
import com.gestion.privilegio.model.Privilegio;
import com.gestion.privilegio.service.ModuloService;
import com.gestion.privilegio.service.PrivilegioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/v1/privilegios")
@CrossOrigin(origins = "*")
@Tag(name = "Privilegios", description = "Operaciones para gestionar privilegios y módulos")
public class PrivilegioController {

    private final PrivilegioService privilegioService;
    private final ModuloService moduloService;

    public PrivilegioController(PrivilegioService privilegioService, ModuloService moduloService) {
        this.privilegioService = privilegioService;
        this.moduloService = moduloService;
    }

    @Operation(
        summary = "Listar todos los privilegios",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de privilegios obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        }
    )
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Privilegio>>> listarPrivilegios() {
        List<EntityModel<Privilegio>> privilegios = privilegioService.listarTodos().stream()
                .map(this::toPrivilegioModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(privilegios));
    }

    @Operation(
        summary = "Listar todos los módulos",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de módulos obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        }
    )
    @GetMapping("/modulos")
    public ResponseEntity<CollectionModel<EntityModel<Modulo>>> listarModulos() {
        List<EntityModel<Modulo>> modulos = moduloService.obtenerTodosLosModulos().stream()
                .map(this::toModuloModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(modulos));
    }

    @Operation(
        summary = "Obtener módulos asociados a un rol",
        responses = {
            @ApiResponse(responseCode = "200", description = "Módulos encontrados para el rol"),
            @ApiResponse(responseCode = "204", description = "No hay módulos asociados al rol"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        }
    )
    @GetMapping("/rol/{nombreRol}")
    public ResponseEntity<List<Modulo>> obtenerModulosPorRol(@PathVariable String nombreRol) {
        List<Modulo> modulos = privilegioService.obtenerModulosPorRol(nombreRol.toUpperCase());
        if (modulos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(modulos);
    }

    @Operation(
        summary = "Guardar un nuevo privilegio",
        responses = {
            @ApiResponse(responseCode = "200", description = "Privilegio guardado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        }
    )
    @PostMapping
    public ResponseEntity<Privilegio> guardarPrivilegio(@RequestBody Privilegio privilegio) {
        Privilegio guardado = privilegioService.guardar(privilegio);
        return ResponseEntity.ok(guardado);
    }

    @Operation(
        summary = "Eliminar un privilegio por ID",
        responses = {
            @ApiResponse(responseCode = "204", description = "Privilegio eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Privilegio no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPrivilegio(@PathVariable Long id) {
        privilegioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Métodos auxiliares para HATEOAS
    private EntityModel<Privilegio> toPrivilegioModel(Privilegio p) {
        return EntityModel.of(p,
            linkTo(methodOn(PrivilegioController.class).listarPrivilegios()).withSelfRel());
    }

    private EntityModel<Modulo> toModuloModel(Modulo m) {
        return EntityModel.of(m,
            linkTo(methodOn(PrivilegioController.class).listarModulos()).withSelfRel());
    }
}