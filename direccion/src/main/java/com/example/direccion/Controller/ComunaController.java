package com.example.direccion.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.direccion.Repository.ComunaRepository;
import com.example.direccion.Repository.RegionRepository;
import com.example.direccion.model.Comuna;
import com.example.direccion.model.Region;

@RestController
@RequestMapping("/api/v1/comunas")
public class ComunaController {

    private final ComunaRepository comunaRepository;
    private final RegionRepository regionRepository;

    public ComunaController(ComunaRepository comunaRepository, RegionRepository regionRepository) {
        this.comunaRepository = comunaRepository;
        this.regionRepository = regionRepository;
    }

    @PostMapping
    public Comuna crearComuna(@RequestBody Comuna comuna) {
        if (comuna.getRegion() == null || comuna.getRegion().getIdRegion() == null) {
            throw new RuntimeException("Se requiere una región válida");
        }
        Region region = regionRepository.findById(comuna.getRegion().getIdRegion())
                .orElseThrow(() -> new RuntimeException("Región no encontrada"));
        comuna.setRegion(region);
        return comunaRepository.save(comuna);
    }

    @GetMapping
    public List<Comuna> listarComunas() {
        return comunaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Comuna obtenerComuna(@PathVariable Long id) {
        return comunaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comuna no encontrada con ID: " + id));
    }
}