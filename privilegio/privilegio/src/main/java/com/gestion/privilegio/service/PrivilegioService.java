package com.gestion.privilegio.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gestion.privilegio.model.Modulo;
import com.gestion.privilegio.model.Privilegio;
import com.gestion.privilegio.repository.PrivilegioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PrivilegioService {

    private final PrivilegioRepository privilegioRepository;

    // Constructor con inyección de dependencias automática
    public PrivilegioService(PrivilegioRepository privilegioRepository) {
        this.privilegioRepository = privilegioRepository;
    }

    /**
     * Listar todos los privilegios en la base de datos.
     */
    public List<Privilegio> listarTodos() {
        return privilegioRepository.findAll();
    }

    /**
     * Guardar un nuevo privilegio.
     */
    public Privilegio guardar(Privilegio privilegio) {
        return privilegioRepository.save(privilegio);
    }

    /**
     * Eliminar un privilegio por su ID.
     */
    public void eliminar(Long id) {
        privilegioRepository.deleteById(id);
    }

    /**
     * Obtiene los módulos accesibles según el nombre del rol.
     * @param nombreRol nombre del rol (ej. ADMIN, CLIENTE)
     * @return lista de módulos accesibles
     */
    public List<Modulo> obtenerModulosPorRol(String nombreRol) {
        return privilegioRepository.findModulosByRolNombre(nombreRol);
    }
}
