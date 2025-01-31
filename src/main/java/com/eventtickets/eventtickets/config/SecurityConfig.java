package com.eventtickets.eventtickets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para pruebas en Postman
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/register", "/api/users/login").permitAll() // Permitir acceso sin autenticación
                .anyRequest().permitAll() // Permitir todo para pruebas
            )
            .formLogin(login -> login.disable()) // Deshabilitar formulario de login de Spring Security
            .httpBasic(basic -> basic.disable()); // Deshabilitar autenticación básica

        return http.build();
    }
}
