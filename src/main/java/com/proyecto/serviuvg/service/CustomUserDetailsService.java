package com.proyecto.serviuvg.service;

import com.proyecto.serviuvg.entity.Usuario;
import com.proyecto.serviuvg.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombre(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return User.builder()
                .username(usuario.getNombre())
                .password(usuario.getPassword())
                .roles(usuario.getRol().getNombre().replace("ROLE_", ""))
                .build();
    }
}