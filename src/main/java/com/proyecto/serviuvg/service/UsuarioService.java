package com.proyecto.serviuvg.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.serviuvg.entity.Rol;
import com.proyecto.serviuvg.entity.Usuario;
import com.proyecto.serviuvg.repository.RolRepository;
import com.proyecto.serviuvg.repository.UsuarioRepository;

import java.util.Collections;

@Service
public class UsuarioService implements UserDetailsService {

    private static final String ROL_POR_DEFECTO = "ROLE_USUARIO";
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombre(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        String nombreRol = (usuario.getRol() != null) ? usuario.getRol().getNombre() : ROL_POR_DEFECTO;

        return new User(
                usuario.getNombre(),
                usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(nombreRol))
        );
    }

    @Transactional
    public boolean registrarUsuario(Usuario datos) {
        String nombre = (datos.getNombre() == null) ? "" : datos.getNombre().trim();
        String password = datos.getPassword();
        if (nombre.isEmpty() || password == null || password.isBlank()) {
            return false;
        }
        if (usuarioRepository.findByNombre(nombre).isPresent()) {
            return false;
        }

        Rol rol = rolRepository.findByNombre(ROL_POR_DEFECTO)
                .orElseGet(() -> rolRepository.save(new Rol(ROL_POR_DEFECTO)));

        Usuario nuevo = new Usuario(nombre, passwordEncoder.encode(password), rol);
        usuarioRepository.save(nuevo);
        return true;
    }
}