package com.proyecto.serviuvg.config;

import com.proyecto.serviuvg.entity.Rol;
import com.proyecto.serviuvg.entity.Usuario;
import com.proyecto.serviuvg.repository.RolRepository;
import com.proyecto.serviuvg.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

// Cuando se inicia la aplicación verifica que exista el usuario, si no crea el ejemplo
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepo, RolRepository rolRepo, PasswordEncoder encoder) {
        return args -> {
            // CORRECCIÓN: Ahora busca si existe "Alejandro" en lugar de "estudiante"
            if (usuarioRepo.findByNombre("Alejandro").isEmpty()) {
                Rol rolEstudiante = rolRepo.findByNombre("ROLE_USUARIO")
                        .orElseGet(() -> rolRepo.save(new Rol("ROLE_USUARIO")));

                Usuario user = new Usuario();
                user.setNombre("Alejandro");
                user.setPassword(encoder.encode("Hola12345"));
                user.setRol(rolEstudiante);

                usuarioRepo.save(user);
            }
        };
    }
}