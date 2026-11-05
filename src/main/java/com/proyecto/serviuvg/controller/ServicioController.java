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
import java.util.List;
import java.util.Optional;

@Controller
public class ServicioController {

    @Autowired
    private ServicioRepository servicioRepository;
    private final List<String> palabrasProhibidas = List.of("puta", "puto", "putas", "putos", "mierda", "mierdas","maldito", "maldita", "malditos", "malditas","jodido", "jodida", "joder", "jodete", "pendejo", "pendeja", "pendejos", "pendejas","estupido", "estupida", "estupidos", "estupidas",
        "idiota", "idiotas", "imbecil", "imbeciles", "tarado", "tarada","retrasado", "retrasada", "inutil", "inutiles", "baboso", "babosa", "bobo", "boba", "zoquete", "payaso", "payasa", "culero", "culera", "ojete", "cabron", "cabrona", "cabrones", "mamon", "mamona", "maricon", "coño", "carajo", "verga", 
        "vergazo", "chingada", "chingado", "chingar", "chingadera", "chinguen", "culiado", "culiada", "hijueputa", "hijueputas", "hijo de puta", "hija de puta", "hijo de perra", "malparido", "malparida", "pene", "picha", "pija", "vergota", "vagina", "vulva", "culo", "culazo", "nalgas", "nalga", "tetas", 
        "tetotas", "coger", "follar", "fueputa", "cogerme", "mamadas", "mamada", "mamar", "masturbar", "masturbacion", "porno", "pornografia", "pornografico", "desnudo", "desnuda", "encuerado", "encuerada", "putear", "prostituta", "prostituto", "escort", "puta barata", "cerote", "cerotes", "hueco", "hueca",
        "huevon", "huevona", "huevos", "pizado", "pizada", "pizados", "pizadas", "pisado", "pisada", "pisar", "mierdero", "shuco", "shuca", "chucho", "mula", "mulas", "burro", "burra", "babosada", "a la gran puta", "ala gran", "comemierda", "careverga", "carepicha", "careculo", "hijueputazo", "negro de mierda", 
        "indio de mierda", "indio bruto", "raza inferior", "raza asquerosa", "puto gay", "lesbiana de mierda", "homofobico", "mongolico", "enano de mierda", "gordo de mierda", "pobre asqueroso", "naco", "sudaca", "extranjero de mierda", "te voy a matar", "te voy a golpear", "te voy a violar", "te voy a hacer daño",
        "te voy a encontrar", "te voy a partir la cara", "te voy a destruir", "ojala te mueras", "matate", "suicidate", "vete a morir", "nadie te quiere", "das asco", "eres una basura", "no vales nada", "te odio", "voy a abusar de ti", "pvt4", "p.u.t.a", "p u t a", "p3nd3j0", "pendej0", "mi3rd4", "m1erd4",
        "c3r0t3", "cer0te", "v3rg4", "v3rga", "j0d3r", "j0der", "kbron", "cabron", "mar1c0n", "maricon", "webon", "weon", "huev0n", "hdp", "h1jueputa"
);

    private boolean contienePalabrasProhibidas(String texto) {
        if (texto == null || texto.isBlank()) return false;
        String textoLower = texto.toLowerCase();
        for (String palabra : palabrasProhibidas) {
            if (textoLower.contains(palabra)) {
                return true;
            }
        }
        return false;
    }

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

        if (contienePalabrasProhibidas(servicio.getTitulo()) || contienePalabrasProhibidas(servicio.getDescripcion()) || contienePalabrasProhibidas(servicio.getDetalle())) {
            redirectAttributes.addFlashAttribute("error", "Tu publicación contiene palabras no permitidas o lenguaje inapropiado");
            return "redirect:/publicar";
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