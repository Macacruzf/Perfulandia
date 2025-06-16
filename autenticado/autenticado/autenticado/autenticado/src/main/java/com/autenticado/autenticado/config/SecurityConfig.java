package com.autenticado.autenticado.config;

import com.autenticado.autenticado.model.Usuario;
import com.autenticado.autenticado.webclient.UsuarioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
@EnableMethodSecurity
public class SecurityConfig {

    private final UsuarioClient usuarioClient;

    public SecurityConfig(UsuarioClient usuarioClient) {
        this.usuarioClient = usuarioClient;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactiva CSRF para facilitar pruebas con Postman (solo para APIs sin frontend)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/test").permitAll() // Estas rutas no requieren autenticación
                .anyRequest().authenticated() // Todas las demás sí
            )
            .httpBasic(Customizer.withDefaults()); // Habilita autenticación básica HTTP
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                System.out.println("➡️ Buscando usuario: " + username);

                Usuario usuario = usuarioClient.obtenerPorNickname(username);

                if (usuario == null) {
                    System.out.println("❌ Usuario no encontrado: " + username);
                    throw new UsernameNotFoundException("Usuario no encontrado: " + username);
                }

                if (usuario.getRol() == null) {
                    System.out.println("❌ El usuario no tiene Rol asociado.");
                    throw new UsernameNotFoundException("El usuario no tiene Rol asociado.");
                } else {
                    System.out.println("➡️ Rol: " + usuario.getRol().getNombreRol());
                }

                System.out.println("✅ Usuario encontrado: " + usuario.getNickname());
                System.out.println("➡️ Password (hash): " + usuario.getPassword());

                // 🚀 Corrección: remover "ROLE_" para que .roles() no duplique "ROLE_"
                String rol = usuario.getRol().getNombreRol();

                return User.builder()
                    .username(usuario.getNickname())
                    .password(usuario.getPassword()) // aquí va el hash de la base
                    .roles(rol) // sin "ROLE_" duplicado
                    .build();
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Utiliza BCrypt para verificar hash de contraseñas
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // Expone el AuthenticationManager para login manual
    }
}