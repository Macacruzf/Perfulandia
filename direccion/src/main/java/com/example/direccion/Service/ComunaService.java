package com.example.direccion.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.direccion.Repository.ComunaRepository;
import com.example.direccion.Repository.RegionRepository;
import com.example.direccion.model.Comuna;
import com.example.direccion.model.Region;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ComunaService {

    @Autowired
    private ComunaRepository comunaRepository;

    @Autowired
    private RegionRepository regionRepository;

    // Devuelve todas las comunas disponibles
    public List<Comuna> obtenerTodas() {
        return comunaRepository.findAll();
    }

    // Devuelve una comuna específica por su ID
    public Comuna obtenerPorId(Long id) {
        return comunaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la comuna con ID: " + id));
    }

    // Crea una nueva comuna validando que la región asociada exista
    public Comuna crear(Comuna comuna) {
        if (comuna.getNombre() == null || comuna.getNombre().isEmpty()) {
            throw new RuntimeException("El nombre de la comuna es obligatorio");
        }

        if (comuna.getRegion() == null || comuna.getRegion().getIdRegion() == null) {
            throw new RuntimeException("Debe especificar la región de la comuna");
        }

        Region region = regionRepository.findById(comuna.getRegion().getIdRegion())
                .orElseThrow(() -> new RuntimeException("No existe región con ID: " + comuna.getRegion().getIdRegion()));

        comuna.setRegion(region); // me aseguro que es una región válida
        return comunaRepository.save(comuna);
    }

    // Actualiza los datos de una comuna existente
    public Comuna actualizar(Long id, Comuna nueva) {
        Comuna existente = comunaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la comuna con ID: " + id));

        if (nueva.getNombre() != null) {
            existente.setNombre(nueva.getNombre());
        }

        if (nueva.getRegion() != null && nueva.getRegion().getIdRegion() != null) {
            Region region = regionRepository.findById(nueva.getRegion().getIdRegion())
                    .orElseThrow(() -> new RuntimeException("No existe región con ID: " + nueva.getRegion().getIdRegion()));
            existente.setRegion(region);
        }

        return comunaRepository.save(existente);
    }

    // Elimina una comuna por su ID
    public void eliminar(Long id) {
        if (!comunaRepository.existsById(id)) {
            throw new RuntimeException("No existe comuna con ID: " + id);
        }

        comunaRepository.deleteById(id);
    }
}
