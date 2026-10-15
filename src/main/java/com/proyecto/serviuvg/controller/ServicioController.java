package com.proyecto.serviuvg.controller;

import com.proyecto.serviuvg.entity.Contratacion;
import com.proyecto.serviuvg.entity.Servicio;
import com.proyecto.serviuvg.entity.Usuario;
import com.proyecto.serviuvg.repository.ContratacionRepository;
import com.proyecto.serviuvg.repository.ServicioRepository;
import com.proyecto.serviuvg.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
public class ServicioController {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContratacionRepository contratacionRepository;

    @PostMapping("/publicar")
    public String guardarServicio(@ModelAttribute Servicio servicio,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombre(principal.getName());
        usuarioOpt.ifPresent(servicio::setUsuario);

        servicioRepository.save(servicio);
        redirectAttributes.addFlashAttribute("exito", true);

        return "redirect:/publicar";
    }

    @PostMapping("/servicios/contratar")
    public String contratarServicio(@RequestParam("servicioId") Long servicioId,
                                    @RequestParam("fechaDeseada") String fechaDeseada,
                                    @RequestParam("contactoCliente") String contactoCliente,
                                    @RequestParam("metodoPago") String metodoPago,
                                    @RequestParam("mensaje") String mensaje,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        Optional<Usuario> clienteOpt = usuarioRepository.findByNombre(principal.getName());
        Optional<Servicio> servicioOpt = servicioRepository.findById(servicioId);

        if (clienteOpt.isEmpty() || servicioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se encontró el servicio o el usuario.");
            return "redirect:/servicios";
        }

        Usuario cliente = clienteOpt.get();
        Servicio servicio = servicioOpt.get();

        // Evitar que un usuario contrate su propio servicio
        if (servicio.getUsuario() != null && servicio.getUsuario().getId().equals(cliente.getId())) {
            redirectAttributes.addFlashAttribute("error", "No puedes contratar un servicio publicado por ti mismo.");
            return "redirect:/perfil";
        }

        Contratacion contratacion = new Contratacion();
        contratacion.setServicio(servicio);
        contratacion.setCliente(cliente);
        contratacion.setFechaDeseada(fechaDeseada);
        contratacion.setContactoCliente(contactoCliente);
        contratacion.setMetodoPago(metodoPago);
        contratacion.setMontoPago(servicio.getPrecio());
        contratacion.setMensaje(mensaje);
        contratacion.setEstado("PENDIENTE");

        contratacionRepository.save(contratacion);
        redirectAttributes.addFlashAttribute("exito", "¡Solicitud enviada! Puedes ver el estado de tu contratación aquí en tu perfil.");

        return "redirect:/perfil";
    }

    @PostMapping("/contrataciones/estado")
    public String actualizarEstadoContratacion(@RequestParam("contratacionId") Long contratacionId,
                                               @RequestParam("nuevoEstado") String nuevoEstado,
                                               Principal principal,
                                               RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        Optional<Contratacion> contratacionOpt = contratacionRepository.findById(contratacionId);
        if (contratacionOpt.isPresent()) {
            Contratacion contratacion = contratacionOpt.get();
            Servicio servicio = contratacion.getServicio();

            if (servicio.getUsuario() != null && servicio.getUsuario().getNombre().equals(principal.getName())) {
                if ("ACEPTADA".equals(nuevoEstado) || "RECHAZADA".equals(nuevoEstado) || "COMPLETADA".equals(nuevoEstado)) {
                    contratacion.setEstado(nuevoEstado);
                    contratacionRepository.save(contratacion);
                    redirectAttributes.addFlashAttribute("exito", "Estado de la solicitud actualizado a: " + nuevoEstado);
                }
            }
        }

        return "redirect:/perfil";
    }

    @Transactional
    @PostMapping("/servicios/eliminar")
    public String eliminarServicio(@RequestParam("id") Long id,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        Optional<Servicio> servicioOpt = servicioRepository.findById(id);
        if (servicioOpt.isPresent()) {
            Servicio servicio = servicioOpt.get();
            if (servicio.getUsuario() == null || (principal != null && servicio.getUsuario().getNombre().equals(principal.getName()))) {
                contratacionRepository.deleteByServicio(servicio);
                servicioRepository.delete(servicio);
                redirectAttributes.addFlashAttribute("exito", "Servicio eliminado correctamente.");
            }
        }
        return "redirect:/perfil";
    }
}