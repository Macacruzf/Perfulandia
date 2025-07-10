
package com.gestion.usuario.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.gestion.usuario.model.Usuario;
import com.gestion.usuario.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("api/v1/usuarios")
@CrossOrigin(origins = "*")
@Tag(name = "Usuario", description = "Operaciones relacionadas con usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Registrar un nuevo usuario")
    @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente", content = @Content(schema = @Schema(implementation = Usuario.class)))
    @PostMapping("/registro")
    public EntityModel<Usuario> registrarUsuario(@RequestBody Usuario usuario) {
        Usuario registrado = usuarioService.registrarUsuario(usuario);
        return agregarLinks(registrado);
    }

    @Operation(summary = "Obtener usuario por ID", 
         security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
         content = @Content(schema = @Schema(implementation = Usuario.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @GetMapping("/id/{idUsuario}")
    public ResponseEntity<EntityModel<Usuario>> obtenerPorId(@PathVariable Long idUsuario) {
        Usuario usuario = usuarioService.buscarPorId(idUsuario);
        return usuario != null
            ? ResponseEntity.ok(agregarLinks(usuario))
            : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener usuario por nickname")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado", 
                     content = @Content(schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<EntityModel<Usuario>> obtenerPorNickname(@PathVariable String nickname) {
        Usuario usuario = usuarioService.buscarPorNickname(nickname);
        return usuario != null
            ? ResponseEntity.ok(agregarLinks(usuario))
            : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Listar todos los usuarios")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    })
    @GetMapping
    public CollectionModel<EntityModel<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodos();

        List<EntityModel<Usuario>> recursos = usuarios.stream()
            .map(this::agregarLinks)
            .collect(Collectors.toList());

        return CollectionModel.of(recursos,
            linkTo(methodOn(UsuarioController.class).listarUsuarios()).withSelfRel());
    }

    @Operation(summary = "Actualizar un usuario por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente", 
                     content = @Content(schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "204", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            usuarioService.actualizarUsuario(id, usuario);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Usuario actualizado correctamente"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    @Operation(summary = "Eliminar un usuario por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Usuario eliminado correctamente"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
    @Operation(summary = "Actualizar el perfil del usuario autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/perfil")
    public ResponseEntity<Map<String, String>> actualizarMiPerfil(@RequestBody Usuario datos, Authentication auth) {
        String nickname = auth.getName();
        Usuario usuario = usuarioService.buscarPorNickname(nickname);

        if (usuario == null) {
            return ResponseEntity.status(404).body(Map.of(
                "status", "error",
                "message", "Usuario no encontrado"
            ));
        }

        usuario.setCorreo(datos.getCorreo());
        usuario.setNickname(datos.getNickname());
        usuarioService.actualizarUsuario(usuario.getIdUsuario(), usuario);

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Perfil actualizado correctamente"
        ));
    }
    @Operation(summary = "Obtener usuario por ID (versión directa con HATEOAS)",
        description = "Devuelve un usuario dado su ID, incluyendo enlaces HATEOAS",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obtenerPorIdSimple(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return usuario != null
            ? ResponseEntity.ok(agregarLinks(usuario))
            : ResponseEntity.notFound().build();
    }

    // Este método puede ir aquí o en una clase HATEOASHelper
    private EntityModel<Usuario> agregarLinks(Usuario usuario) {
        return EntityModel.of(usuario,
            linkTo(methodOn(UsuarioController.class).obtenerPorNickname(usuario.getNickname())).withSelfRel(),
            linkTo(methodOn(UsuarioController.class).listarUsuarios()).withRel("usuarios"));
    }
}


