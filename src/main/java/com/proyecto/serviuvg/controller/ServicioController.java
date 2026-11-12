package com.proyecto.serviuvg.controller;

import com.proyecto.serviuvg.dto.ServicioEdicionFormulario;
import com.proyecto.serviuvg.dto.HorarioSemanalFormulario;
import com.proyecto.serviuvg.entity.Contratacion;
import com.proyecto.serviuvg.entity.FranjaDisponibilidad;
import com.proyecto.serviuvg.entity.Servicio;
import com.proyecto.serviuvg.entity.Usuario;
import com.proyecto.serviuvg.repository.ContratacionRepository;
import com.proyecto.serviuvg.repository.ServicioRepository;
import com.proyecto.serviuvg.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class ServicioController {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContratacionRepository contratacionRepository;

    private final List<String> palabrasProhibidas = List.of(
        "puta", "puto", "putas", "putos", "mierda", "mierdas",
        "maldito", "maldita", "malditos", "malditas",
        "jodido", "jodida", "joder", "jodete",
        "pendejo", "pendeja", "pendejos", "pendejas",
        "estupido", "estupida", "estupidos", "estupidas",
        "idiota", "idiotas", "imbecil", "imbeciles",
        "tarado", "tarada", "retrasado", "retrasada",
        "inutil", "inutiles", "baboso", "babosa", "bobo", "boba",
        "zoquete", "payaso", "payasa", "culero", "culera",
        "ojete", "cabron", "cabrona", "cabrones",
        "mamon", "mamona", "maricon", "coño", "carajo", "verga",
        "vergazo", "chingada", "chingado", "chingar", "chingadera",
        "chinguen", "culiado", "culiada", "hijueputa", "hijueputas",
        "hijo de puta", "hija de puta", "hijo de perra",
        "malparido", "malparida", "pene", "picha", "pija",
        "vergota", "vagina", "vulva", "culo", "culazo",
        "nalgas", "nalga", "tetas", "tetotas", "coger", "follar",
        "fueputa", "cogerme", "mamadas", "mamada", "mamar",
        "masturbar", "masturbacion", "porno", "pornografia",
        "pornografico", "desnudo", "desnuda", "encuerado",
        "encuerada", "putear", "prostituta", "prostituto",
        "escort", "puta barata", "cerote", "cerotes", "hueco", "hueca",
        "huevon", "huevona", "huevos", "pizado", "pizada",
        "pizados", "pizadas", "pisado", "pisada", "pisar",
        "mierdero", "shuco", "shuca", "chucho", "mula", "mulas",
        "burro", "burra", "babosada", "a la gran puta", "ala gran",
        "comemierda", "careverga", "carepicha", "careculo",
        "hijueputazo", "negro de mierda", "indio de mierda",
        "indio bruto", "raza inferior", "raza asquerosa", "puto gay",
        "lesbiana de mierda", "homofobico", "mongolico",
        "enano de mierda", "gordo de mierda", "pobre asqueroso",
        "naco", "sudaca", "extranjero de mierda",
        "te voy a matar", "te voy a golpear", "te voy a violar",
        "te voy a hacer daño", "te voy a encontrar",
        "te voy a partir la cara", "te voy a destruir",
        "ojala te mueras", "matate", "suicidate", "vete a morir",
        "nadie te quiere", "das asco", "eres una basura",
        "no vales nada", "te odio", "voy a abusar de ti",
        "pvt4", "p.u.t.a", "p u t a", "p3nd3j0", "pendej0",
        "mi3rd4", "m1erd4", "c3r0t3", "cer0te", "v3rg4",
        "v3rga", "j0d3r", "j0der", "kbron",
        "mar1c0n", "webon", "weon", "huev0n",
        "hdp", "h1jueputa", "prostitución", "prostitucion", "hijoputa", "hijaputa", "hijos de puta", "hijas de puta",
        "hija de perra", "hijos de perra", "hijas de perra", "hijueperra", "hijueperras",
        "gonorrea", "gonorreas", "malnacido", "malnacida", "malnacidos", "malnacidas",
        "desgraciado", "desgraciada", "desgraciados", "desgraciadas", "bastardo", "bastarda",
        "bastardos", "bastardas", "cabronas", "cabronazo", "cabronazos", "cabrón",
        "cabrones de mierda", "mamón", "mamones", "maricón", "maricones", "mariconazo", "estúpido",
        "estúpida", "estúpidos", "estúpidas", "imbécil", "imbéciles", "inútil", "inútiles",
        "huevón", "huevones", "huevonas", "pelotudo", "pelotuda", "pelotudos", "pelotudas",
        "boludo", "boluda", "boludos", "boludas", "gilipollas", "gilipollez", "gilipolleces",
        "capullo", "capulla", "soplapollas", "chingas", "chingados", "chingadas", "chingatumadre",
        "chinga tu madre", "chingue su madre", "chinga tu puta madre", "vete a la mierda",
        "andate a la mierda", "vete al carajo", "andate al carajo", "vete a la verga",
        "andate a la verga", "me cago en ti", "me cago en vos", "me cago en tu madre", "comepinga",
        "comepollas", "chupapijas", "chupaverga", "chupapolla", "chupapollas", "chupamela",
        "chúpamela", "pajero", "pajera", "pajeros", "pajeras", "puñeta", "puñetas", "puñetero",
        "puñetera", "cagada", "cagadas", "cagado", "cagados", "mierdoso", "mierdosa", "mierdosos",
        "mierdosas", "cerotazo", "cerotazos", "pizadin", "pizadito", "pizadita", "pizarrin",
        "pizarrín", "culeros", "culeras", "culerazo", "culerazos", "caremierda", "cara de mierda",
        "cara de verga", "cara de culo", "cara de picha", "pedazo de mierda", "pedazo de idiota",
        "pedazo de imbecil", "pedazo de imbécil", "vales mierda", "vales verga", "sos una mierda",
        "sos una basura", "sos un idiota", "sos una idiota", "jódete", "jodanse", "jódanse",
        "que te jodan", "que te den por culo", "metetelo por el culo", "métetelo por el culo",
        "masturbación", "pornografía", "pornográfico", "ojalá te mueras", "mátate", "suicídate",
        "homofóbico", "mongólico"
    );

    private final Pattern patronProhibidas = Pattern.compile(
        "\\b(?:" + palabrasProhibidas.stream()
            .map(Pattern::quote)
            .collect(Collectors.joining("|")) + ")\\b",
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS
    );

    private boolean contienePalabrasProhibidas(String texto) {
        if (texto == null || texto.isBlank()) {
            return false;
        }

        return patronProhibidas.matcher(texto).find();
    }

    private static final Set<String> SUBCATEGORIAS_TUTORIA = Set.of(
    "calculo", "algebra", "fisica", "quimica", "programacion", "estadistica", "logica-digital", "ingles", "otra"
    );

    @InitBinder({"servicio", "formulario"})
    public void configurarFormulario(WebDataBinder binder) {
        binder.setAllowedFields(
            "titulo",
            "categoria",
            "subcategoria",
            "descripcion",
            "detalle",
            "precio",
            "unidad",
            "contactoProveedor"
        );

        binder.registerCustomEditor(
            String.class,
            new StringTrimmerEditor(true)
        );
    }

    @InitBinder("horario")
    public void configurarHorario(WebDataBinder binder) {
        binder.setAllowedFields("dias[*].activo", "dias[*].horaInicio", "dias[*].horaFin");
        binder.setAutoGrowCollectionLimit(7);
    }

    @PostMapping("/publicar")
    public String guardarServicio(
            @ModelAttribute("servicio") Servicio servicio,
            @ModelAttribute("horario") HorarioSemanalFormulario horario,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        if (contienePalabrasProhibidas(servicio.getTitulo())
                || contienePalabrasProhibidas(servicio.getDescripcion())
                || contienePalabrasProhibidas(servicio.getDetalle())) {

            redirectAttributes.addFlashAttribute(
                "error",
                "Tu publicación contiene palabras no permitidas o lenguaje inapropiado"
            );

            return "redirect:/publicar";
        }

        if (!"tutorias".equals(servicio.getCategoria()) || !SUBCATEGORIAS_TUTORIA.contains(servicio.getSubcategoria())) {
            servicio.setSubcategoria(null);
        }

        try {
            servicio.setDisponibilidad(horario.convertir());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "publicar";
        }

        Optional<Usuario> usuarioOpt =
            usuarioRepository.findByNombre(principal.getName());

        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        servicio.setUsuario(usuarioOpt.get());
        servicioRepository.save(servicio);

        redirectAttributes.addFlashAttribute("exito", true);

        return "redirect:/publicar";
    }

    @Transactional(readOnly = true)
    @GetMapping("/servicios/{id}/editar")
    public String mostrarEditarServicio(
            @PathVariable("id") Long id,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        Optional<Servicio> servicioOpt = servicioRepository.findById(id);

        if (servicioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                "error",
                "El servicio ya no existe."
            );

            return "redirect:/perfil";
        }

        Servicio servicio = servicioOpt.get();

        if (!esPropietario(servicio, principal)) {
            redirectAttributes.addFlashAttribute(
                "error",
                "Solo puedes editar tus propios servicios."
            );

            return "redirect:/perfil";
        }

        ServicioEdicionFormulario formulario =
            new ServicioEdicionFormulario();

        formulario.setTitulo(servicio.getTitulo());
        formulario.setCategoria(servicio.getCategoria());
        formulario.setSubcategoria(servicio.getSubcategoria());
        formulario.setDescripcion(servicio.getDescripcion());
        formulario.setDetalle(servicio.getDetalle());
        formulario.setPrecio(BigDecimal.valueOf(servicio.getPrecio()));
        formulario.setUnidad(servicio.getUnidad());
        formulario.setContactoProveedor(servicio.getContactoProveedor());

        model.addAttribute("formulario", formulario);
        model.addAttribute("servicioId", servicio.getId());
        model.addAttribute("horario", HorarioSemanalFormulario.desdeServicio(servicio));

        return "editar-servicio";
    }

    @Transactional
    @PostMapping("/servicios/{id}/editar")
    public String actualizarServicio(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("formulario")
            ServicioEdicionFormulario formulario,
            BindingResult bindingResult,
            @ModelAttribute("horario") HorarioSemanalFormulario horario,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        Optional<Servicio> servicioOpt = servicioRepository.findById(id);

        if (servicioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                "error",
                "El servicio ya no existe."
            );

            return "redirect:/perfil";
        }

        Servicio servicio = servicioOpt.get();

        if (!esPropietario(servicio, principal)) {
            redirectAttributes.addFlashAttribute(
                "error",
                "Solo puedes editar tus propios servicios."
            );

            return "redirect:/perfil";
        }

        if (contienePalabrasProhibidas(formulario.getTitulo())) {
            bindingResult.rejectValue(
                "titulo",
                "palabrasProhibidas",
                "El título contiene palabras no permitidas."
            );
        }

        if (contienePalabrasProhibidas(formulario.getDescripcion())) {
            bindingResult.rejectValue(
                "descripcion",
                "palabrasProhibidas",
                "La descripción contiene palabras no permitidas."
            );
        }

        if (contienePalabrasProhibidas(formulario.getDetalle())) {
            bindingResult.rejectValue(
                "detalle",
                "palabrasProhibidas",
                "El detalle contiene palabras no permitidas."
            );
        }

        List<FranjaDisponibilidad> franjas = null;
        try {
            franjas = horario.convertir();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorHorario", e.getMessage());
        }

        if (bindingResult.hasErrors() || franjas == null) {
            model.addAttribute("servicioId", servicio.getId());
            return "editar-servicio";
        }

        servicio.setTitulo(formulario.getTitulo());
        servicio.setCategoria(formulario.getCategoria());
        servicio.setSubcategoria(formulario.getSubcategoria());
        servicio.setDescripcion(formulario.getDescripcion());
        servicio.setDetalle(formulario.getDetalle());
        servicio.setSubcategoria(
            "tutorias".equals(formulario.getCategoria()) ? formulario.getSubcategoria() : null);
        servicio.setUnidad(formulario.getUnidad());
        servicio.setContactoProveedor(formulario.getContactoProveedor());
        servicio.setDisponibilidad(franjas);

        servicioRepository.save(servicio);

        redirectAttributes.addFlashAttribute(
            "exito",
            "Servicio actualizado correctamente."
        );

        return "redirect:/perfil";
    }

    private boolean esPropietario(Servicio servicio, Principal principal) {
        return principal != null
                && servicio.getUsuario() != null
                && principal.getName()
                    .equals(servicio.getUsuario().getNombre());
    }

    @Transactional
    @PostMapping("/servicios/contratar")
    public String contratarServicio(
            @RequestParam("servicioId") Long servicioId,
            @RequestParam("fechaDeseada") String fechaDeseada,
            @RequestParam("horaDeseada") String horaDeseada,
            @RequestParam("contactoCliente") String contactoCliente,
            @RequestParam("metodoPago") String metodoPago,
            @RequestParam("mensaje") String mensaje,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        Optional<Usuario> clienteOpt =
            usuarioRepository.findByNombre(principal.getName());

        Optional<Servicio> servicioOpt =
            servicioRepository.findById(servicioId);

        if (clienteOpt.isEmpty() || servicioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                "error",
                "No se encontró el servicio o el usuario."
            );

            return "redirect:/servicios";
        }

        Usuario cliente = clienteOpt.get();
        Servicio servicio = servicioOpt.get();

        LocalDate fecha;
        LocalTime hora;
        try {
            if (!horaDeseada.matches("(?:[01][0-9]|2[0-3]):[0-5][0-9]")) {
                throw new DateTimeParseException("Hora inválida", horaDeseada, 0);
            }
            fecha = LocalDate.parse(fechaDeseada);
            hora = LocalTime.parse(horaDeseada);
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error", "Selecciona una fecha y una hora válidas.");
            return "redirect:/servicios";
        }

        if (fecha.isBefore(LocalDate.now(ZoneId.of("America/Guatemala")))) {
            redirectAttributes.addFlashAttribute("error", "La fecha del servicio no puede haber pasado.");
            return "redirect:/servicios";
        }

        if (!servicio.getDisponibilidad().isEmpty() && servicio.getDisponibilidad().stream()
                .noneMatch(franja -> franja.incluye(fecha, hora))) {
            redirectAttributes.addFlashAttribute("error", "Elige una fecha y hora dentro de la disponibilidad publicada.");
            return "redirect:/servicios";
        }

        if (servicio.getUsuario() != null
                && servicio.getUsuario().getId().equals(cliente.getId())) {

            redirectAttributes.addFlashAttribute(
                "error",
                "No puedes contratar un servicio publicado por ti mismo."
            );

            return "redirect:/perfil";
        }

        Contratacion contratacion = new Contratacion();

        contratacion.setServicio(servicio);
        contratacion.setCliente(cliente);
        contratacion.setFechaDeseada(fechaDeseada);
        contratacion.setHoraDeseada(horaDeseada);
        contratacion.setContactoCliente(contactoCliente);
        contratacion.setMetodoPago(metodoPago);
        contratacion.setMontoPago(servicio.getPrecio());
        contratacion.setMensaje(mensaje);
        contratacion.setEstado("PENDIENTE");

        contratacionRepository.save(contratacion);

        redirectAttributes.addFlashAttribute(
            "exito",
            "¡Solicitud enviada! Puedes ver el estado de tu contratación aquí en tu perfil."
        );

        return "redirect:/perfil";
    }

    @PostMapping("/contrataciones/estado")
    public String actualizarEstadoContratacion(
            @RequestParam("contratacionId") Long contratacionId,
            @RequestParam("nuevoEstado") String nuevoEstado,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        Optional<Contratacion> contratacionOpt =
            contratacionRepository.findById(contratacionId);

        if (contratacionOpt.isPresent()) {
            Contratacion contratacion = contratacionOpt.get();
            Servicio servicio = contratacion.getServicio();

            if (servicio.getUsuario() != null
                    && servicio.getUsuario().getNombre()
                        .equals(principal.getName())) {

                if ("ACEPTADA".equals(nuevoEstado)
                        || "RECHAZADA".equals(nuevoEstado)
                        || "COMPLETADA".equals(nuevoEstado)) {

                    contratacion.setEstado(nuevoEstado);
                    contratacionRepository.save(contratacion);

                    redirectAttributes.addFlashAttribute(
                        "exito",
                        "Estado de la solicitud actualizado a: " + nuevoEstado
                    );
                }
            }
        }

        return "redirect:/perfil";
    }

    @Transactional
    @PostMapping("/servicios/eliminar")
    public String eliminarServicio(
            @RequestParam("id") Long id,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Optional<Servicio> servicioOpt = servicioRepository.findById(id);

        if (servicioOpt.isPresent()) {
            Servicio servicio = servicioOpt.get();

            if (servicio.getUsuario() == null
                    || (principal != null
                        && servicio.getUsuario().getNombre()
                            .equals(principal.getName()))) {

                contratacionRepository.deleteByServicio(servicio);
                servicioRepository.delete(servicio);

                redirectAttributes.addFlashAttribute(
                    "exito",
                    "Servicio eliminado correctamente."
                );
            }
        }

        return "redirect:/perfil";
    }
}
