
package com.gestion.privilegio.controller;

import java.util.List;


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

@RestController
@RequestMapping("/api/v1/privilegios")
@CrossOrigin(origins = "*") // Permitir acceso desde otros servicios o frontends
public class PrivilegioController {


    private final PrivilegioService privilegioService;
    private final ModuloService moduloService;

    // Constructor con inyección de dependencias
    public PrivilegioController(PrivilegioService privilegioService, ModuloService moduloService) {
        this.privilegioService = privilegioService;
        this.moduloService = moduloService;
    }

    /**
     * Lista todos los privilegios registrados en el sistema.
     * @return lista de privilegios
     */
    @GetMapping
    public ResponseEntity<List<Privilegio>> listarPrivilegios() {
        List<Privilegio> lista = privilegioService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    /**
     * Lista todos los módulos funcionales disponibles para asignación de privilegios.
     * @return lista de módulos
     */
    @GetMapping("/modulos")
    public ResponseEntity<List<Modulo>> listarModulos() {
        List<Modulo> modulos = moduloService.obtenerTodosLosModulos(); // Se usa el método ya existente
        return ResponseEntity.ok(modulos);
    }

    /**
     * Retorna los módulos a los que un rol tiene acceso.
     */
    @GetMapping("/rol/{nombreRol}")
    public ResponseEntity<List<Modulo>> obtenerModulosPorRol(@PathVariable String nombreRol) {
        List<Modulo> modulos = privilegioService.obtenerModulosPorRol(nombreRol.toUpperCase());
        
        if (modulos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(modulos);
    }

    /**
     * Crea y guarda un nuevo privilegio con su respectivo módulo y rol.
     * @param privilegio objeto de tipo Privilegio
     * @return privilegio guardado
     */
    @PostMapping
    public ResponseEntity<Privilegio> guardarPrivilegio(@RequestBody Privilegio privilegio) {
        Privilegio guardado = privilegioService.guardar(privilegio);
        return ResponseEntity.ok(guardado);
    }

    /**
     * Elimina un privilegio por su ID.
     * @param id ID del privilegio a eliminar
     * @return respuesta sin contenido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPrivilegio(@PathVariable Long id) {
        privilegioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    
}

