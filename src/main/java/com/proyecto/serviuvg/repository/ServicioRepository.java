package com.proyecto.serviuvg.repository;

import com.proyecto.serviuvg.entity.Servicio;
import com.proyecto.serviuvg.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    List<Servicio> findByUsuario(Usuario usuario);
}