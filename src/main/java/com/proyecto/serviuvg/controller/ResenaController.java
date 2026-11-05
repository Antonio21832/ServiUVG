package com.proyecto.serviuvg.controller;

import com.proyecto.serviuvg.dto.ResenaFormulario;
import com.proyecto.serviuvg.service.ResenaService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class ResenaController {
    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @PostMapping("/servicios/resenar")
    public String guardarResena(@RequestParam("servicioId") Long servicioId,
            @Valid @ModelAttribute("resenaFormulario") ResenaFormulario formulario,
            BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorResena",
                "Elegí de 1 a 5 estrellas y escribí un comentario de hasta 500 caracteres.");
        } else {
            try {
                resenaService.calificar(servicioId, principal.getName(), formulario.getEstrellas(),
                    formulario.getComentario());
                redirectAttributes.addFlashAttribute("exitoResena", "¡Gracias! Tu reseña ya es visible para todos.");
            } catch (IllegalArgumentException ex) {
                redirectAttributes.addFlashAttribute("errorResena", ex.getMessage());
            } catch (DataIntegrityViolationException ex) {
                // La transacción del servicio ya se revirtió antes de llegar aquí.
                redirectAttributes.addFlashAttribute("errorResena",
                    "No se pudo guardar la reseña: ya calificaste este servicio o dejó de estar disponible.");
            }
        }
        return "redirect:/servicios?resenas=" + servicioId;
    }
}