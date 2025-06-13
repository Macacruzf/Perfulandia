package com.example.tickets.controller;

import com.example.tickets.model.Mensaje;
import com.example.tickets.repository.MensajeRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    @Autowired
    private MensajeRepository mensajeRepository;

    @GetMapping
    public List<Mensaje> listarTodos() {
        return mensajeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Mensaje obtenerPorId(@PathVariable Long id) {
        return mensajeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        mensajeRepository.deleteById(id);
    }
}