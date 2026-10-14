package com.proyecto.serviuvg.repository;

import com.proyecto.serviuvg.entity.Contratacion;
import com.proyecto.serviuvg.entity.Servicio;
import com.proyecto.serviuvg.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContratacionRepository extends JpaRepository<Contratacion, Long> {
    List<Contratacion> findByCliente(Usuario cliente);

    List<Contratacion> findByServicioUsuario(Usuario proveedor);

    void deleteByServicio(Servicio servicio);
}