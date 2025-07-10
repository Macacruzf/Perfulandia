package com.example.tickets.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tickets.model.Mensaje;
import com.example.tickets.repository.MensajeRepository;

@Service
public class MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    public List<Mensaje> obtenerTodos() {
        return mensajeRepository.findAll();
    }

    public Mensaje obtenerPorId(Long id) {
        return mensajeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el mensaje con ID: " + id));
    }

    public Mensaje crear(Mensaje mensaje) {
        return mensajeRepository.save(mensaje);
    }

    public Mensaje actualizar(Long id, Mensaje actualizado) {
        Mensaje existente = obtenerPorId(id);

        existente.setMensaje(actualizado.getMensaje());
        existente.setFechaMensaje(actualizado.getFechaMensaje());
        existente.setTipoMensaje(actualizado.getTipoMensaje());
        existente.setIdUsuario(actualizado.getIdUsuario());
        existente.setTicket(actualizado.getTicket());

        return mensajeRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!mensajeRepository.existsById(id)) {
            throw new RuntimeException("No existe el mensaje con ID: " + id);
        }
        mensajeRepository.deleteById(id);
    }
}
