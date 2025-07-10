package com.autenticado.autenticado.config;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // Spring detecta esta clase automáticamente
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil; // Inyectamos la clase que sabe cómo manejar los tokens

    // Este método se ejecuta en cada request que llega a la API
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 🔍 Obtenemos el token JWT desde el header "Authorization"
        final String authHeader = request.getHeader("Authorization");

        // Validamos que el token comience con "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            
            String token = authHeader.substring(7);

            // Validamos que el token sea legítimo y no esté vencido
            if (jwtUtil.validateToken(token)) {
                // Extraemos el username desde el token
                String username = jwtUtil.extractUsername(token);

                // Creamos una autenticación válida (aunque sin roles todavía)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username, // principal (quién es)
                                null,     // credenciales (ya están en el token)
                                List.of() // roles/permisos, opcional por ahora
                        );

                // Asignamos información adicional del request
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Guardamos esta autenticación en el contexto de seguridad de Spring
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        //  Dejamos que el request continúe su camino
        filterChain.doFilter(request, response);
    }
}
