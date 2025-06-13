package com.example.direccion.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.direccion.Repository.ComunaRepository;
import com.example.direccion.Repository.DireccionRepository;
import com.example.direccion.Repository.RegionRepository;
import com.example.direccion.model.Comuna;
import com.example.direccion.model.Direccion;
import com.example.direccion.model.Region;

import jakarta.transaction.Transactional;

@Transactional
@Service

public class DireccionService {

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private ComunaRepository comunaRepository;

    @Autowired
    private RegionRepository regionRepository;

    public Direccion crearDireccion(Direccion direccion) {
    //Validar datos básicos
    if (direccion == null) {
        throw new RuntimeException("La dirección no puede ser nula");
    }

    // Valida que tenga comuna
    if (direccion.getComuna() == null) {
        throw new RuntimeException("Debe especificar una comuna");
    }

    Long idComuna = direccion.getComuna().getIdComuna();
    if (idComuna == null) {
        throw new RuntimeException("La comuna no tiene ID");
    }

    // Busca la comuna 
    Comuna comuna = comunaRepository
        .findById(idComuna)
        .orElseThrow(() -> new RuntimeException("No existe comuna con ID: " + idComuna));

    // guarda
    direccion.setComuna(comuna);
    return direccionRepository.save(direccion);
}

    public List<Direccion> listarDireccionesPorUsuario(Long idUser) {
        return direccionRepository.findByIdUser(idUser);
    }

    public void eliminarDireccion(Long idDireccion) {
        direccionRepository.deleteById(idDireccion);
    }

    public List<Comuna> listarComunasPorRegion(Long idRegion) {
        return comunaRepository.findByRegion_IdRegion(idRegion);
    }


    public List<Region> listarRegiones() {
        return regionRepository.findAll();
    }
}
