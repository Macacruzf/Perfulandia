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

@Service
@Transactional
public class DireccionService {

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private ComunaRepository comunaRepository;

    @Autowired
    private RegionRepository regionRepository;

    // Crea una nueva dirección validando que pertenezca a una comuna existente
    public Direccion crearDireccion(Direccion direccion) {
        if (direccion == null) {
            throw new RuntimeException("La dirección no puede ser nula");
        }

        if (direccion.getComuna() == null || direccion.getComuna().getIdComuna() == null) {
            throw new RuntimeException("Debe especificar una comuna válida");
        }

        Comuna comuna = comunaRepository
                .findById(direccion.getComuna().getIdComuna())
                .orElseThrow(() -> new RuntimeException("No existe comuna con ID: " + direccion.getComuna().getIdComuna()));

        direccion.setComuna(comuna);
        return direccionRepository.save(direccion);
    }

    // Devuelve todas las direcciones registradas
    public List<Direccion> obtenerTodas() {
        return direccionRepository.findAll();
    }

    // Obtiene una dirección por su ID
    public Direccion obtenerPorId(Long id) {
        return direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la dirección con ID: " + id));
    }

    // Devuelve todas las direcciones asociadas a una comuna específica
    public List<Direccion> obtenerPorComuna(Long idComuna) {
        if (!comunaRepository.existsById(idComuna)) {
            throw new RuntimeException("No se encontró la comuna con ID: " + idComuna);
        }
        return direccionRepository.findByComuna_IdComuna(idComuna);
    }

// Actualiza los datos de una dirección
    public Direccion actualizarDireccion(Long id, Direccion nuevaDireccion) {
        Direccion existente = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe dirección con ID: " + id));

        // Verifico y actualizo cada campo si viene con un nuevo valor
        if (nuevaDireccion.getCalle() != null) {
            existente.setCalle(nuevaDireccion.getCalle());
        }

        if (nuevaDireccion.getNumero() != null) {
            existente.setNumero(nuevaDireccion.getNumero());
        }

        if (nuevaDireccion.getDepartamento() != null) {
            existente.setDepartamento(nuevaDireccion.getDepartamento());
        }

        if (nuevaDireccion.getCodigoPostal() != null) {
            existente.setCodigoPostal(nuevaDireccion.getCodigoPostal());
        }

        if (nuevaDireccion.getRegion() != null && nuevaDireccion.getRegion().getIdRegion() != null) {
            Region region = regionRepository.findById(nuevaDireccion.getRegion().getIdRegion())
                    .orElseThrow(() -> new RuntimeException("No existe región con ID: " + nuevaDireccion.getRegion().getIdRegion()));
            existente.setRegion(region);
        }

        if (nuevaDireccion.getComuna() != null && nuevaDireccion.getComuna().getIdComuna() != null) {
            Comuna comuna = comunaRepository.findById(nuevaDireccion.getComuna().getIdComuna())
                    .orElseThrow(() -> new RuntimeException("No existe comuna con ID: " + nuevaDireccion.getComuna().getIdComuna()));
            existente.setComuna(comuna);
        }

        return direccionRepository.save(existente);
    }

    // Elimina una dirección
    public void eliminarDireccion(Long idDireccion) {
        if (!direccionRepository.existsById(idDireccion)) {
            throw new RuntimeException("No existe dirección con ID: " + idDireccion);
        }
        direccionRepository.deleteById(idDireccion);
    }

    // Métodos adicionales para otras entidades (no requeridos directamente por el CRUD de direcciones)
    public List<Comuna> listarComunasPorRegion(Long idRegion) {
        return comunaRepository.findByRegion_IdRegion(idRegion);
    }

    public List<Region> listarRegiones() {
        return regionRepository.findAll();
    }
}
