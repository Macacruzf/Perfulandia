package com.gestion.privilegio.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gestion.privilegio.model.Modulo;
import com.gestion.privilegio.repository.ModuloRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ModuloService {
    private final ModuloRepository moduloRepository;

    public ModuloService(ModuloRepository moduloRepository) {
        this.moduloRepository = moduloRepository;
    }

    /**
     * Obtiene todos los módulos registrados.
     * @return lista de módulos
     */
    public List<Modulo> obtenerTodosLosModulos() {
        return moduloRepository.findAll();
    }

    /**
     * Busca un módulo por su ID.
     * @param id ID del módulo
     * @return módulo encontrado o null si no existe
     */
    public Modulo obtenerPorId(Long id) {
        Optional<Modulo> modulo = moduloRepository.findById(id);
        return modulo.orElse(null);
    }

    /**
     * Crea un nuevo módulo.
     * @param modulo datos del módulo a crear
     * @return módulo creado
     */
    public Modulo crearModulo(Modulo modulo) {
        return moduloRepository.save(modulo);
    }

    /**
     * Actualiza los datos de un módulo existente.
     * @param id ID del módulo a actualizar
     * @param moduloActualizado datos actualizados
     * @return módulo actualizado o null si no existe
     */
    public Modulo actualizarModulo(Long id, Modulo moduloActualizado) {
        Optional<Modulo> optionalModulo = moduloRepository.findById(id);
        if (optionalModulo.isPresent()) {
            Modulo moduloExistente = optionalModulo.get();
            moduloExistente.setNombre(moduloActualizado.getNombre());
            return moduloRepository.save(moduloExistente);
        }
        return null;
    }

    /**
     * Elimina un módulo por su ID.
     * @param id ID del módulo a eliminar
     * @return true si fue eliminado, false si no existe
     */
    public boolean eliminarModulo(Long id) {
        if (moduloRepository.existsById(id)) {
            moduloRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Devuelve la lista de todos los módulos.
     * @return lista de módulos
     */
    public List<Modulo> listarModulos() {
        return moduloRepository.findAll();
    }
}
