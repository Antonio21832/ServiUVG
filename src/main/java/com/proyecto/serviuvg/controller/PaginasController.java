package com.proyecto.serviuvg.controller;

import com.proyecto.serviuvg.entity.Contratacion;
import com.proyecto.serviuvg.entity.Servicio;
import com.proyecto.serviuvg.entity.Usuario;
import com.proyecto.serviuvg.repository.ContratacionRepository;
import com.proyecto.serviuvg.repository.ServicioRepository;
import com.proyecto.serviuvg.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class PaginasController {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContratacionRepository contratacionRepository;

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

        if (principal != null) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByNombre(nombreUsuario);
            if (usuarioOpt.isPresent()) {
                Usuario usuarioActual = usuarioOpt.get();

                List<Servicio> misServicios = servicioRepository.findByUsuario(usuarioActual);
                model.addAttribute("misServicios", misServicios);

                List<Contratacion> misContrataciones = contratacionRepository.findByCliente(usuarioActual);
                model.addAttribute("misContrataciones", misContrataciones);

                List<Contratacion> solicitudesRecibidas = contratacionRepository.findByServicioUsuario(usuarioActual);
                model.addAttribute("solicitudesRecibidas", solicitudesRecibidas);

                return "perfil";
            }
        }

        model.addAttribute("misServicios", Collections.emptyList());
        model.addAttribute("misContrataciones", Collections.emptyList());
        model.addAttribute("solicitudesRecibidas", Collections.emptyList());
        return "perfil";
    }
}