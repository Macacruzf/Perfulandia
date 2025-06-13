
package com.gestion.privilegio.config;

import java.util.Arrays;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gestion.privilegio.model.Modulo;
import com.gestion.privilegio.model.Privilegio;
import com.gestion.privilegio.repository.ModuloRepository;
import com.gestion.privilegio.repository.PrivilegioRepository;

@Configuration
public class LoadDataBase {
      @Bean
    CommandLineRunner initDatabase(ModuloRepository moduloRepository, PrivilegioRepository privilegioRepository) {
        return args -> {
            // Crear módulos
            Modulo gestionUsuarios = moduloRepository.findByNombreModulo("Gestión Usuarios")
                    .orElseGet(() -> moduloRepository.save(new Modulo(null, "Gestión Usuarios", null)));

            Modulo gestionVentas = moduloRepository.findByNombreModulo("Gestión Ventas")
                    .orElseGet(() -> moduloRepository.save(new Modulo(null, "Gestión Ventas", null)));

            Modulo gestionEnvios = moduloRepository.findByNombreModulo("Gestión Envíos")
                    .orElseGet(() -> moduloRepository.save(new Modulo(null, "Gestión Envíos", null)));

            // Cargar privilegios para cada rol en cada módulo

            // Admin
            if (privilegioRepository.findByNombreRol("Admin").isEmpty()) {
                Privilegio p1 = new Privilegio(null, "Admin", "Puede gestionar todos los usuarios", gestionUsuarios);
                Privilegio p2 = new Privilegio(null, "Admin", "Puede gestionar todas las ventas", gestionVentas);
                Privilegio p3 = new Privilegio(null, "Admin", "Puede gestionar envíos y estados", gestionEnvios);
                privilegioRepository.saveAll(Arrays.asList(p1, p2, p3));
            }

            // Cliente
            if (privilegioRepository.findByNombreRol("Cliente").isEmpty()) {
                Privilegio p1 = new Privilegio(null, "Cliente", "Puede ver y comprar productos", gestionVentas);
                Privilegio p2 = new Privilegio(null, "Cliente", "No puede gestionar usuarios ni envíos", gestionUsuarios);
                privilegioRepository.saveAll(Arrays.asList(p1, p2));
            }

            // Gerente
            if (privilegioRepository.findByNombreRol("Gerente").isEmpty()) {
                Privilegio p1 = new Privilegio(null, "Gerente", "Puede supervisar ventas y gestionar inventarios", gestionVentas);
                Privilegio p2 = new Privilegio(null, "Gerente", "No puede gestionar usuarios ni envíos", gestionUsuarios);
                privilegioRepository.saveAll(Arrays.asList(p1, p2));
            }

            // Logística
            if (privilegioRepository.findByNombreRol("Logística").isEmpty()) {
                Privilegio p1 = new Privilegio(null, "Logística", "Puede gestionar envíos y estados de pedidos", gestionEnvios);
                Privilegio p2 = new Privilegio(null, "Logística", "No puede gestionar usuarios ni ventas", gestionUsuarios);
                privilegioRepository.saveAll(Arrays.asList(p1, p2));
            }
        };
    }
}
