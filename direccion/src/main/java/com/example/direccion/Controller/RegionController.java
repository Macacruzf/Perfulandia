package com.example.direccion.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.direccion.Repository.RegionRepository;
import com.example.direccion.model.Region;

@RestController
@RequestMapping("/api/v1/regiones")
public class RegionController {

    private final RegionRepository regionRepository;

    public RegionController(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @PostMapping
    public Region crearRegion(@RequestBody Region region) {
        return regionRepository.save(region);
    }

    @GetMapping
    public List<Region> listarRegiones() {
        return regionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Region obtenerRegion(@PathVariable Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Región no encontrada con ID: " + id));
    }
}