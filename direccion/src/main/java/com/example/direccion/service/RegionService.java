package com.example.direccion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.direccion.Repository.RegionRepository;
import com.example.direccion.model.Region;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RegionService {

    @Autowired
    private RegionRepository regionRepository;

    // Lista todas las regiones disponibles en la base de datos
    public List<Region> obtenerTodas() {
        return regionRepository.findAll();
    }

    // Devuelve una región específica por su ID
    public Region obtenerPorId(Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe región con ID: " + id));
    }

    // Crea una nueva región
    public Region crear(Region region) {
        if (region.getNombre() == null || region.getNombre().isEmpty()) {
            throw new RuntimeException("El nombre de la región es obligatorio");
        }

        return regionRepository.save(region);
    }

    // Actualiza los datos de una región existente
    public Region actualizar(Long id, Region nueva) {
        Region existente = regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe región con ID: " + id));

        if (nueva.getNombre() != null) {
            existente.setNombre(nueva.getNombre());
        }

        return regionRepository.save(existente);
    }

    // Elimina una región por su ID
    public void eliminar(Long id) {
        if (!regionRepository.existsById(id)) {
            throw new RuntimeException("No existe región con ID: " + id);
        }

        regionRepository.deleteById(id);
    }

}
