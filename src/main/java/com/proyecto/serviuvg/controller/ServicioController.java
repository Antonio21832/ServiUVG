package com.proyecto.serviuvg.controller;

import com.proyecto.serviuvg.entity.Servicio;
import com.proyecto.serviuvg.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ServicioController {

    @Autowired
    private ServicioRepository servicioRepository;

    @PostMapping("/publicar")
    public String guardarServicio(@ModelAttribute Servicio servicio, RedirectAttributes redirectAttributes) {
        // 1. Guardar el objeto en la base de datos
        servicioRepository.save(servicio);
        
        // 2. Enviar un mensaje de éxito a la vista HTML (Thymeleaf lo leerá con th:if="${exito}")
        redirectAttributes.addFlashAttribute("exito", true);
        
        // 3. Recargar la página limpia
        return "redirect:/publicar";
    }

    @PostMapping("/servicios/eliminar")
    public String eliminarServicio(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        servicioRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("exito", "Servicio eliminado correctamente.");
        return "redirect:/perfil";
    }
}