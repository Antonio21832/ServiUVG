package com.proyecto.serviuvg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proyecto.serviuvg.entity.Rol;
import com.proyecto.serviuvg.entity.Usuario;
import com.proyecto.serviuvg.repository.RolRepository;
import com.proyecto.serviuvg.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registrarUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        Rol rolUsuario = rolRepository.findByNombre("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        usuarioRepository.save(usuario);
    }
}