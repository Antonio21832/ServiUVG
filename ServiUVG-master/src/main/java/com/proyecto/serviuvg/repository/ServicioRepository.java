package com.proyecto.serviuvg.repository;

import com.proyecto.serviuvg.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    // Spring Boot se encarga de implementar los métodos básicos como save() o findAll()
}