package com.example.Liga_Del_Cume.data.security;

import com.example.Liga_Del_Cume.data.model.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro personalizado que integra la autenticación basada en sesiones HTTP
 * con el sistema de seguridad de Spring Security.
 *
 * Este filtro verifica si existe un usuario en la sesión HTTP y,
 * si existe, lo establece como usuario autenticado en Spring Security.
 */
@Component
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Obtener la sesión HTTP
        HttpSession session = request.getSession(false);

        // Si hay sesión y contiene un usuario
        if (session != null) {
            Object usuarioObj = session.getAttribute("usuario");

            if (usuarioObj instanceof Usuario) {
                Usuario usuario = (Usuario) usuarioObj;

                // Verificar si ya está autenticado en Spring Security
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Crear token de autenticación para Spring Security
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            usuario,
                            null,
                            Collections.emptyList() // Sin roles específicos
                        );

                    authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Establecer la autenticación en el contexto de Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // Log para debugging
                    System.out.println("🔐 Usuario autenticado via sesión: " + usuario.getNombreUsuario() +
                                     " (ID: " + usuario.getIdUsuario() + ")");
                }
            }
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}

