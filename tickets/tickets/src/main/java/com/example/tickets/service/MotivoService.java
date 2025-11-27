package com.example.tickets.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tickets.model.Motivo;
import com.example.tickets.repository.MotivoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class MotivoService {

    @Autowired
    private MotivoRepository motivoRepository;

    public List<Motivo> obtenerTodos() {
        return motivoRepository.findAll();
    }

    public Motivo obtenerPorId(Long id) {
        return motivoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el motivo con ID: " + id));
    }

    public Motivo crear(Motivo motivo) {
        return motivoRepository.save(motivo);
    }

    public Motivo actualizar(Long id, Motivo actualizado) {
        Motivo existente = obtenerPorId(id);
        existente.setNombre(actualizado.getNombre());
        return motivoRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!motivoRepository.existsById(id)) {
            throw new RuntimeException("No existe el motivo con ID: " + id);
        }
        motivoRepository.deleteById(id);
    }
}
