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

// Recibe las solicitudes para mostrar y enviar el formulario.
@Controller
public class ContactoController {

    private static final Logger log =
            LoggerFactory.getLogger(ContactoController.class);

    private final ContactoService contactoService;

    // Spring proporciona el servicio que envía los correos.
    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    // Muestra contacto.html con un formulario vacío.
    @GetMapping("/contacto")
    public String mostrarContacto(Model model) {
        model.addAttribute("contacto", new ContactoFormulario());
        return "contacto";
    }

    // Recibe los datos cuando el usuario presiona "Enviar mensaje".
    @PostMapping("/contacto")
    public String enviar(
            @Valid @ModelAttribute("contacto") ContactoFormulario contacto,
            BindingResult resultado,
            Model model,
            RedirectAttributes redirect,
            HttpSession session) {

        // Si los datos son inválidos, muestra los errores en el formulario.
        // BindingResult debe ir inmediatamente después del objeto validado.
        if (resultado.hasErrors()) {
            return "contacto";
        }

        // Evita envíos repetidos durante un minuto en la misma sesión.
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
            // Envía la consulta y luego intenta enviar la copia.
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
                // La consulta llegó al servidor de correo, pero falló la copia.
                redirect.addFlashAttribute(
                        "advertencia",
                        "Tu consulta fue enviada al equipo, pero no pudimos "
                                + "enviar la copia a tu correo. "
                                + "No necesitás volver a enviar el formulario."
                );
            }

            // Redirige para evitar repetir el envío al actualizar la página.
            return "redirect:/contacto";

        } catch (MailException ex) {
            // Registra el tipo de error sin mostrar credenciales al usuario.
            log.warn(
                    "No se pudo enviar la consulta ({})",
                    ex.getClass().getSimpleName()
            );

            // Conserva los datos escritos y muestra un aviso.
            model.addAttribute(
                    "errorEnvio",
                    "No pudimos enviar tu mensaje. Intentá de nuevo más tarde. "
                            + "Si el problema continúa, el equipo debe revisar "
                            + "la configuración de correo."
            );

            return "contacto";
        }
    }
}