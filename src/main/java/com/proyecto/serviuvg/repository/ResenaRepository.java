package com.proyecto.serviuvg.repository;

import com.proyecto.serviuvg.entity.Resena;
import com.proyecto.serviuvg.entity.Servicio;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Long> {
    boolean existsByServicioIdAndUsuarioId(Long servicioId, Long usuarioId);

    @EntityGraph(attributePaths = "usuario")
    List<Resena> findByServicioIdInOrderByFechaDescIdDesc(Collection<Long> servicioIds);

    @Modifying(flushAutomatically = true)
    @Query("delete from Resena r where r.servicio = :servicio")
    void deleteByServicio(@Param("servicio") Servicio servicio);
}