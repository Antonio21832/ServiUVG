package com.proyecto.serviuvg.controller;

import com.proyecto.serviuvg.entity.Servicio;
import com.proyecto.serviuvg.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class PaginasController {

    // Inyectamos el repositorio para poder leer la base de datos
    @Autowired
    private ServicioRepository servicioRepository;

    @GetMapping("/")
    public String mostrarIndex() {
        return "index";
    }

    @GetMapping("/contacto")
    public String mostrarContacto() {
        return "contacto";
    }

    @GetMapping("/servicios")
    public String mostrarServicios(Model model) {
        // 1. Buscamos todos los servicios en MySQL
        List<Servicio> listaServicios = servicioRepository.findAll();
        
        // 2. Se los pasamos al HTML bajo el nombre "serviciosBD"
        model.addAttribute("serviciosBD", listaServicios);
        
        return "servicios";
    }
    
    @GetMapping("/publicar")
    public String mostrarPublicar() {
        return "publicar";
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model, Principal principal) {
        // Captura el nombre del usuario autenticado por Spring Security
        String nombreUsuario = (principal != null) ? principal.getName() : "Estudiante";
        model.addAttribute("usuario", nombreUsuario);
        
        // Envía la lista de servicios a la vista para gestionarlos
        model.addAttribute("misServicios", servicioRepository.findAll());
        
        return "perfil";
    }
}