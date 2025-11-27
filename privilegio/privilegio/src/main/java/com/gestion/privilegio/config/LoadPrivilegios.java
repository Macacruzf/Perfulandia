package com.gestion.privilegio.config;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.gestion.privilegio.model.Modulo;
import com.gestion.privilegio.model.Privilegio;
import com.gestion.privilegio.repository.ModuloRepository;
import com.gestion.privilegio.repository.PrivilegioRepository;

@Configuration
public class LoadPrivilegios {

    @Bean
    public CommandLineRunner initPrivilegios(ModuloRepository moduloRepository, PrivilegioRepository privilegioRepository) {
        return args -> {
            // Verifica si ya hay privilegios cargados
            if (privilegioRepository.count() > 0) return;

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
            asignarPrivilegios("ADMIN", nombresModulos, moduloRepository, privilegioRepository); // ADMIN accede a todo
            asignarPrivilegios("CLIENTE", List.of("productos", "ticket"), moduloRepository, privilegioRepository);
            asignarPrivilegios("LOGISTICA", List.of("productos", "direccion"), moduloRepository, privilegioRepository);
            asignarPrivilegios("GERENTE", List.of("ventas", "usuarios"), moduloRepository, privilegioRepository);
        };
    }

    private void asignarPrivilegios(String rol, List<String> nombresModulos, ModuloRepository moduloRepository, PrivilegioRepository privilegioRepository) {
        for (String nombreModulo : nombresModulos) {
            // Busca el módulo por nombre
            Modulo modulo = moduloRepository.findByNombre(nombreModulo).orElse(null);

            // Si existe y no está asignado aún al rol, lo asigna
            if (modulo != null && !privilegioRepository.existsByNombreRolAndModulo(rol, modulo)) {
                Privilegio privilegio = new Privilegio();
                privilegio.setNombreRol(rol);
                privilegio.setModulo(modulo);
                privilegioRepository.save(privilegio);
            }
        }
    }
}
