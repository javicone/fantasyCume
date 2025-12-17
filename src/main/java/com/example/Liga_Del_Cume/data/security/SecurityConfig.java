package com.example.Liga_Del_Cume.data.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de Spring Security
 *
 * Protege todas las rutas bajo /liga/** requiriendo autenticación.
 * Solo permite acceso sin login a:
 * - / (página principal con formulario de login)
 * - /index
 * - /usuario/login y /usuario/registro
 * - Recursos estáticos (CSS, JS, imágenes)
 *
 * Cuando un usuario intenta acceder a /liga/** sin estar autenticado,
 * se redirige automáticamente a /?error=unauthorized
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SessionAuthenticationFilter sessionAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // Desactivar CSRF para permitir POST sin token
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas (sin autenticación)
                        .requestMatchers(
                                "/",
                                "/index",
                                "/error",
                                "/usuario/login",
                                "/usuario/registro",
                                // Recursos estáticos
                                "/css/**", "/js/**", "/images/**", "/static/**",
                                "/*.css", "/*.js", "/*.png", "/*.jpg", "/*.jpeg", "/*.gif", "/*.ico"
                        ).permitAll()
                        // TODO lo demás requiere autenticación (incluye /liga/**)
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Redirigir a la página de error con mensaje descriptivo
                            response.sendRedirect("/error?error=" +
                                java.net.URLEncoder.encode(
                                    "Debes iniciar sesión para acceder a esta página",
                                    "UTF-8"
                                )
                            );
                        })
                )
                .addFilterBefore(sessionAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}