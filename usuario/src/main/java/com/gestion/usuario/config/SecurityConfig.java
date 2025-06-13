package com.gestion.usuario.config;

import com.gestion.usuario.model.Usuario;
import com.gestion.usuario.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/usuarios/nickname/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/cliente/**").hasRole("CLIENTE")
                .requestMatchers("/gerente/**").hasRole("GERENTE")
                .requestMatchers("/logistica/**").hasRole("LOGISTICA")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Aquí defines la autenticación directamente sin crear otra clase
    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return username -> {
            Usuario usuario = usuarioRepository.findByNickname(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

            String rol = usuario.getRol().getNombreRol().replace("ROLE_", "");

            return User.builder()
                    .username(usuario.getNickname())
                    .password(usuario.getPassword())
                    .roles(rol)
                    .build();
        };
    }
}

