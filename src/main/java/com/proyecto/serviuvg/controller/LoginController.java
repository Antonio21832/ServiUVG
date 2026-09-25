package com.proyecto.serviuvg.controller;

import com.proyecto.serviuvg.entity.Rol;
import com.proyecto.serviuvg.entity.Usuario;
import com.proyecto.serviuvg.repository.RolRepository;
import com.proyecto.serviuvg.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Controlador para manejar el inicio de sesión y el registro de usuarios
@Controller
public class LoginController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginController(UsuarioRepository usuarioRepository, 
                           RolRepository rolRepository, 
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("username") String username,
                               @RequestParam("password") String password) {
        if (usuarioRepository.findByNombre(username).isPresent()) {
            return "redirect:/register?error=exists";
        }

        Rol rolUsuario = rolRepository.findByNombre("ROLE_USUARIO")
                .orElseGet(() -> rolRepository.save(new Rol("ROLE_USUARIO")));

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(username);
        nuevoUsuario.setPassword(passwordEncoder.encode(password));
        nuevoUsuario.setRol(rolUsuario);

        usuarioRepository.save(nuevoUsuario);

        return "redirect:/login?registered";
    }
}