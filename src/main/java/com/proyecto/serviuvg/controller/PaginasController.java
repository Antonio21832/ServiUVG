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
        List<Servicio> listaServicios = servicioRepository.findAll();
        
        model.addAttribute("serviciosBD", listaServicios);
        
        return "servicios";
    }
    
    @GetMapping("/publicar")
    public String mostrarPublicar() {
        return "publicar";
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model, Principal principal) {
        String nombreUsuario = (principal != null) ? principal.getName() : "Estudiante";
        model.addAttribute("usuario", nombreUsuario);
        
        model.addAttribute("misServicios", servicioRepository.findAll());
        
        return "perfil";
    }
}