package com.example.direccion.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.direccion.Service.DireccionService;
import com.example.direccion.model.Comuna;
import com.example.direccion.model.Direccion;
import com.example.direccion.model.Region;

@RestController
@RequestMapping("/api/v1/direcciones")
public class DireccionController {

    private final DireccionService direccionService;

    public DireccionController(DireccionService direccionService) {
        this.direccionService = direccionService;
    }

 
    @PostMapping
    public Direccion crearDireccion(@RequestBody Direccion direccion) {
        return direccionService.crearDireccion(direccion);
    }

    @GetMapping("/usuarios/{idUser}") //cambiar dependiendo de como lo identificaron
    public List<Direccion> listarPorUsuario(@PathVariable Long idUser) {
        return direccionService.listarDireccionesPorUsuario(idUser);
    }

    @DeleteMapping("/{idDireccion}")
    public void eliminarDireccion(@PathVariable Long idDireccion) {
        direccionService.eliminarDireccion(idDireccion);
    }

   
    @GetMapping("/comunas/region/{idRegion}")
    public List<Comuna> listarComunasPorRegion(@PathVariable Long idRegion) {
        return direccionService.listarComunasPorRegion(idRegion);
    }

    @GetMapping("/regiones")
    public List<Region> listarRegiones() {
        return direccionService.listarRegiones();
    }
}