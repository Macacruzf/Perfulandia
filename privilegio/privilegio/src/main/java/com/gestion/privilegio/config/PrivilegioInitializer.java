
package com.gestion.privilegio.config;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.gestion.privilegio.model.Modulo;
import com.gestion.privilegio.model.Privilegio;
import com.gestion.privilegio.repository.ModuloRepository;
import com.gestion.privilegio.repository.PrivilegioRepository;

import jakarta.annotation.PostConstruct;
@Component
public class PrivilegioInitializer {

    private final ModuloRepository moduloRepository;
    private final PrivilegioRepository privilegioRepository;

    // Inyección de dependencias por constructor
    public PrivilegioInitializer(ModuloRepository moduloRepository, PrivilegioRepository privilegioRepository) {
        this.moduloRepository = moduloRepository;
        this.privilegioRepository = privilegioRepository;
    }

    /**
     * Este método se ejecuta automáticamente al iniciar la aplicación.
     * Se asegura de que los módulos base y privilegios por rol estén cargados en la base de datos.
     */
    @PostConstruct
    public void inicializarPrivilegios() {
        // Lista de módulos funcionales principales
        List<String> nombresModulos = Arrays.asList("productos", "ventas", "usuarios", "ticket", "direccion");

        // Crear módulos si no existen aún
        for (String nombre : nombresModulos) {
            Optional<Modulo> moduloExistente = moduloRepository.findByNombre(nombre);
            if (moduloExistente.isEmpty()) {
                Modulo nuevoModulo = new Modulo();
                nuevoModulo.setNombre(nombre);
                moduloRepository.save(nuevoModulo);
            }
        }

        // Asignación de privilegios predeterminados por rol
        asignarPrivilegios("ADMIN", nombresModulos); // ADMIN accede a todo
        asignarPrivilegios("CLIENTE", List.of("productos", "ticket"));
        asignarPrivilegios("LOGISTICA", List.of("productos", "direccion"));
        asignarPrivilegios("GERENTE", List.of("ventas", "usuarios"));
    }

    /**
     * Asigna una lista de módulos a un rol específico si aún no existen los privilegios.
     *
     * @param rol nombre del rol (ej: "CLIENTE")
     * @param nombresModulos módulos que debe tener ese rol
     */
    private void asignarPrivilegios(String rol, List<String> nombresModulos) {
        for (String nombreModulo : nombresModulos) {
            // Busca el módulo por nombre
            Modulo modulo = moduloRepository.findByNombre(nombreModulo).orElse(null);

            // Si existe y no está asignado aún al rol, lo asigna
            if (modulo != null && !privilegioRepository.existsByRolAndModulo(rol, modulo)) {
                Privilegio privilegio = new Privilegio();
                privilegio.setNombreRol(rol);
                privilegio.setModulo(modulo);
                privilegioRepository.save(privilegio);
            }
        }
    }
}



