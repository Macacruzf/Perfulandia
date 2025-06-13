
package com.gestion.usuario.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gestion.usuario.model.Rol;
import com.gestion.usuario.model.Usuario;
import com.gestion.usuario.repository.RolRepository;
import com.gestion.usuario.repository.UsuarioRepository;


@Configuration
public class LoadDatabase {
    @Bean
    CommandLineRunner initRolesAndUsers(
        RolRepository rolRepository,
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder) {

        return args -> {
            // Crear roles si no existen
            Rol adminRol = createRoleIfNotExists("ROLE_ADMIN", rolRepository);
            Rol clienteRol = createRoleIfNotExists("ROLE_CLIENTE", rolRepository);
            Rol gerenteRol = createRoleIfNotExists("ROLE_GERENTE", rolRepository);
            Rol logisticaRol = createRoleIfNotExists("ROLE_LOGISTICA", rolRepository);

            // Crear usuarios de prueba si no existen
            createUserIfNotExists("admin", "admin@correo.cl", "admin123", adminRol, usuarioRepository, passwordEncoder);
            createUserIfNotExists("cliente", "cliente@correo.cl", "cliente123", clienteRol, usuarioRepository, passwordEncoder);
            createUserIfNotExists("gerente", "gerente@correo.cl", "gerente123", gerenteRol, usuarioRepository, passwordEncoder);
            createUserIfNotExists("logistica", "logistica@correo.cl", "logistica123", logisticaRol, usuarioRepository, passwordEncoder);
        };
    }

   private Rol createRoleIfNotExists(String nombreRol, RolRepository repository) {
    return repository.findByNombreRol(nombreRol)
        .orElseGet(() -> {
            Rol nuevoRol = new Rol();
            nuevoRol.setNombreRol(nombreRol);
            return repository.save(nuevoRol); // 🔥 Aquí está el return necesario
        });

   }
    private void createUserIfNotExists(String nickname, String correo, String rawPassword,
                                       Rol rol, UsuarioRepository usuarioRepository,
                                       PasswordEncoder passwordEncoder) {
        if (usuarioRepository.findByNickname(nickname).isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setNickname(nickname);
            usuario.setCorreo(correo);
            usuario.setPassword(passwordEncoder.encode(rawPassword));
            usuario.setRol(rol);
            usuarioRepository.save(usuario);
        }
    }
}