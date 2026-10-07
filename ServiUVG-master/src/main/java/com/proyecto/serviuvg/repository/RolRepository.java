package com.proyecto.serviuvg.repository;

import com.proyecto.serviuvg.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

//Métodos para interactuar con la base de datos para la entidad Rol
public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByNombre(String nombre);
}