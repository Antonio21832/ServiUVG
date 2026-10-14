package com.proyecto.serviuvg.repository;

import com.proyecto.serviuvg.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

//Ayuda a interactuar con la tabla de usuarios
//findByNombre: busca un usuarios al momento de iniciar sesión
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNombre(String nombre);
}
