package com.gestion.usuario.config;

import com.gestion.usuario.model.Usuario;
import com.gestion.usuario.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Swagger sin restricciones
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // Endpoints públicos
                .requestMatchers("/api/v1/usuarios/registro").permitAll()
                .requestMatchers("/api/v1/usuarios/nickname/**").permitAll()
                .requestMatchers("/api/v1/usuarios/id/**").permitAll()

                // Permitir acceso libre a todos los endpoints de usuario
                .requestMatchers("/api/v1/usuarios/**").permitAll()

                // Rutas protegidas por rol
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/cliente/**").hasRole("CLIENTE")
                .requestMatchers("/gerente/**").hasRole("GERENTE")
                .requestMatchers("/logistica/**").hasRole("LOGISTICA")

                // Todo lo demás sigue protegido
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults()); // Autenticación básica

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return username -> {
            Usuario usuario = usuarioRepository.findByNickname(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

            String rol = usuario.getRol().getNombreRol();

            return User.builder()
                    .username(usuario.getNickname())
                    .password(usuario.getPassword())
                    .roles(rol) // Spring requiere el rol sin "ROLE_"
                    .build();
        };
    }
}