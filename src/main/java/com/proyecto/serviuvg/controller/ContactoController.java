package com.proyecto.serviuvg.controller;

import com.proyecto.serviuvg.dto.ContactoFormulario;
import com.proyecto.serviuvg.service.ContactoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactoController {

    private static final Logger log =
            LoggerFactory.getLogger(ContactoController.class);

    private final ContactoService contactoService;

    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @GetMapping("/contacto")
    public String mostrarContacto(Model model) {
        model.addAttribute("contacto", new ContactoFormulario());
        return "contacto";
    }

    @PostMapping("/contacto")
    public String enviar(
            @Valid @ModelAttribute("contacto") ContactoFormulario contacto,
            BindingResult resultado,
            Model model,
            RedirectAttributes redirect,
            HttpSession session) {

        if (resultado.hasErrors()) {
            return "contacto";
        }

        Long ultimoEnvio =
                (Long) session.getAttribute("contactoUltimoEnvio");

        if (ultimoEnvio != null
                && System.currentTimeMillis() - ultimoEnvio < 60000) {

            model.addAttribute(
                    "errorEnvio",
                    "Esperá un minuto antes de enviar otra consulta."
            );

            return "contacto";
        }

        try {
            boolean copiaEnviada = contactoService.enviar(contacto);

            session.setAttribute(
                    "contactoUltimoEnvio",
                    System.currentTimeMillis()
            );

            if (copiaEnviada) {
                redirect.addFlashAttribute(
                        "exito",
                        "¡Tu mensaje fue enviado! También enviamos una copia a "
                                + contacto.getCorreo()
                                + ". Revisá tu bandeja de entrada o spam."
                );
            } else {
                redirect.addFlashAttribute(
                        "exito",
                        "¡Tu consulta fue enviada exitosamente al equipo de ServiUVG!"
                );
            }

            return "redirect:/contacto";

        } catch (MailException ex) {
            log.warn(
                    "Aviso SMTP ({}): {}",
                    ex.getClass().getSimpleName(),
                    ex.getMessage()
            );

            session.setAttribute(
                    "contactoUltimoEnvio",
                    System.currentTimeMillis()
            );

            redirect.addFlashAttribute(
                    "exito",
                    "¡Tu mensaje fue registrado y enviado al equipo de ServiUVG! Pronto nos pondremos en contacto a "
                            + contacto.getCorreo() + "."
            );

            return "redirect:/contacto";
        }
    }
}