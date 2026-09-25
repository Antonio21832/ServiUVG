package com.proyecto.serviuvg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas que cualquier persona puede ver (El trabajo de Antonio)
                .requestMatchers("/", "/index", "/contacto", "/servicios", "/css/**", "/js/**", "/img/**").permitAll()
                // Rutas privadas que requieren haber iniciado sesión (Tu trabajo y el de Erwin)
                .requestMatchers("/publicar", "/dashboard").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login") // Ruta al HTML de login de Erwin
                .defaultSuccessUrl("/dashboard", true) // A dónde va al ingresar
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout") // A dónde va al salir
                .permitAll()
            );
            
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}