package com.proyecto.serviuvg.controller;
 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
 
import com.proyecto.serviuvg.entity.Usuario;
import com.proyecto.serviuvg.service.UsuarioService;
 
@Controller
@RequestMapping("/registro")
public class RegistroController {
 
    private final UsuarioService usuarioService;
 
    public RegistroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
 
    @GetMapping
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping
    public String registrarUsuario(@ModelAttribute("usuario") Usuario usuario) {
        boolean registrado = usuarioService.registrarUsuario(usuario);
 
        if (!registrado) {
            return "redirect:/registro?error";
        }
        return "redirect:/login?exito";
    }
}