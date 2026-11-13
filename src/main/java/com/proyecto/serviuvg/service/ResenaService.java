package com.proyecto.serviuvg.service;

import com.proyecto.serviuvg.dto.ServicioCatalogo;
import com.proyecto.serviuvg.dto.ServicioCatalogo.ResenaVista;
import com.proyecto.serviuvg.dto.ServicioCatalogo.FranjaVista;
import com.proyecto.serviuvg.dto.HorarioSemanalFormulario;
import com.proyecto.serviuvg.entity.Resena;
import com.proyecto.serviuvg.entity.Servicio;
import com.proyecto.serviuvg.entity.Usuario;
import com.proyecto.serviuvg.repository.ResenaRepository;
import com.proyecto.serviuvg.repository.ServicioRepository;
import com.proyecto.serviuvg.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;

import java.util.regex.Pattern;

@Service
public class ResenaService {
    private final ResenaRepository resenaRepository;
    private final ServicioRepository servicioRepository;
    private final UsuarioRepository usuarioRepository;
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
        "homofóbico", "mongólico",         "pendejazo", "pendejaza", "pendejazos", "pendejazas",
        "pendejada", "pendejadas", "pendejito", "pendejita",
        "babosos", "babosas", "babosadas", "babosazo",
        "bobos", "bobas", "bobazo", "bobaza",
        "idiotazo", "idiotaza", "idiotez", "idioteces",
        "imbecilazo", "imbécilazo", "estupidazo", "estupidaza",
        "estupidez", "estupideces", "tarados", "taradas",
        "zoquetes", "cretino", "cretina", "cretinos", "cretinas",
        "imbéciles de mierda", "idiotas de mierda",
        "pendejo de mierda", "pendeja de mierda",
        "inservible de mierda", "basura humana",
        "pedazo de basura", "pedazo de porqueria", "pedazo de porquería",
        "malditasea", "maldita sea", "maldita mierda",
        "jodidos", "jodidas", "jodiendo", "jodás", "jodas",
        "no me jodas", "no me jodás", "no jodas", "no jodás",
        "chingate", "chíngate", "chingense", "chínguense",
        "chingadera de mierda", "chingaderas", "chingazo", "chingazos",
        "chingon de mierda", "chingón de mierda",
        "chingues", "no chingues", "chingando", "chingarte",
        "chingarme", "chingarnos", "chingatelos", "chíngatelos",
        "cerotada", "cerotadas", "cerotito", "cerotitos",
        "cerotona", "ceroton", "cerotón", "cerotones",
        "pizadazo", "pizadazos", "pizadota", "pizadote",
        "huevonazo", "huevonazos", "huevazon", "huevazón",
        "huevoncito", "huevoncita", "huevones de mierda",
        "culerada", "culeradas", "culerazo de mierda",
        "cabroncete", "cabroncita", "cabroncito", "cabroncitos",
        "cabronada", "cabronadas", "cabronazo de mierda",
        "mamonazo", "mamonazos", "mamoneria", "mamonería",
        "mamona de mierda", "mamon de mierda", "mamón de mierda",
        "mierdita", "mierditas", "mierderos", "mierdera",
        "mierderas", "mierdota", "mierdotas",
        "comemierdas", "comemierdita", "tragamierda", "tragamierdas",
        "vales pura mierda", "no vales ni mierda",
        "sos una porqueria", "sos una porquería",
        "eres una porqueria", "eres una porquería",
        "sos un cerote", "sos un pendejo", "sos una pendeja",
        "sos un imbecil", "sos un imbécil",
        "andate al demonio", "andate al diablo", "vete al demonio",
        "anda a la mierda", "andá a la mierda",
        "anda a cagar", "andá a cagar", "vete a cagar",
        "anda a chingar", "andá a chingar",
        "callate idiota", "cállate idiota",
        "callate pendejo", "cállate pendejo",
        "callate cerote", "calláte cerote",
        "cer0t3", "c3rote", "c3r0te", "p3ndejo", "p3ndej0",
        "pend3jo", "pend3ja", "p3nd3ja", "p3nd3j4",
        "put4", "put0", "pvta", "pvto", "m13rd4",
        "mierd4", "m1erda", "mierd@", "v3rg@", "verg4",
        "c4bron", "cabr0n", "c4br0n", "cul3ro", "culer0",
        "m4mon", "mam0n", "hijueput4", "h1jueput4",
        "hijueput0", "hij0 de puta", "hij4 de puta"
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

    public ResenaService(ResenaRepository resenaRepository, ServicioRepository servicioRepository,
                         UsuarioRepository usuarioRepository) {
        this.resenaRepository = resenaRepository;
        this.servicioRepository = servicioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    
    public void calificar(Long servicioId, String nombreUsuario, Integer estrellas, String comentario) {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new IllegalArgumentException("Iniciá sesión para calificar un servicio.");
        }
        if (estrellas == null || estrellas < 1 || estrellas > 5) {
            throw new IllegalArgumentException("Elegí una calificación de 1 a 5 estrellas.");
        }
        if (comentario != null && comentario.length() > 500) {
            throw new IllegalArgumentException("El comentario admite hasta 500 caracteres.");
        }
        if (contienePalabrasProhibidas(comentario)) {
    throw new IllegalArgumentException(
        "Tu comentario contiene palabras no permitidas o lenguaje inapropiado."
            );
        }
        Servicio servicio = servicioRepository.findById(servicioId)
            .orElseThrow(() -> new IllegalArgumentException("El servicio ya no existe."));
        Usuario usuario = usuarioRepository.findByNombre(nombreUsuario)
            .orElseThrow(() -> new IllegalArgumentException("No se encontró tu cuenta."));

        if (servicio.getUsuario() != null
                && Objects.equals(servicio.getUsuario().getId(), usuario.getId())) {
            throw new IllegalArgumentException("No podés calificar tus propios servicios.");
        }
        if (resenaRepository.existsByServicioIdAndUsuarioId(servicioId, usuario.getId())) {
            throw new IllegalArgumentException("Ya calificaste este servicio. Solo se permite una reseña por cuenta.");
        }

        // La restricción UNIQUE también evita duplicados si llegan dos solicitudes simultáneas.
        resenaRepository.saveAndFlush(new Resena(servicio, usuario, estrellas, comentario));
    }

    @Transactional(readOnly = true)
    public List<ServicioCatalogo> listarCatalogo(
            String nombreUsuario,
            String orden) {

        Long usuarioId = nombreUsuario == null
            ? null
            : usuarioRepository.findByNombre(nombreUsuario)
                .map(Usuario::getId)
                .orElse(null);

        List<Servicio> servicios;

        if ("asc".equals(orden)) {
            servicios = servicioRepository.findAll(
                Sort.by("precio").ascending()
            );
        } else if ("desc".equals(orden)) {
            servicios = servicioRepository.findAll(
                Sort.by("precio").descending()
            );
        } else {
            servicios = servicioRepository.findAll();
        }

        if (servicios.isEmpty()) return List.of();
        // Una sola consulta de reseñas para todo el catálogo.
        Map<Long, List<Resena>> porServicio = resenaRepository
            .findByServicioIdInOrderByFechaDescIdDesc(servicios.stream().map(Servicio::getId).toList())
            .stream().collect(Collectors.groupingBy(r -> r.getServicio().getId()));

        return servicios.stream().map(s -> {
            List<Resena> resenas = porServicio.getOrDefault(s.getId(), List.of());
            double promedio = resenas.stream().mapToInt(Resena::getEstrellas).average().orElse(0);
            Integer miCalificacion = resenas.stream()
                .filter(r -> usuarioId != null && usuarioId.equals(r.getUsuario().getId()))
                .map(Resena::getEstrellas).findFirst().orElse(null);
            boolean propietario = s.getUsuario() != null && usuarioId != null
                && usuarioId.equals(s.getUsuario().getId());
            List<ResenaVista> vistas = resenas.stream().map(r -> new ResenaVista(
                r.getUsuario().getNombre(), r.getEstrellas(), r.getComentario(),
                r.getFecha().toLocalDate().toString()
            )).toList();
            List<FranjaVista> horario = s.getDisponibilidad().stream()
                .sorted(Comparator.comparing(f -> f.getDia().getValue()))
                .map(f -> new FranjaVista(f.getDia().getValue(),
                    new HorarioSemanalFormulario.Dia(f.getDia()).getEtiqueta(),
                    f.getHoraInicio().toString(), f.getHoraFin().toString()))
                .toList();
            return new ServicioCatalogo(s.getId(), s.getTitulo(), s.getCategoria(), s.getSubcategoria(), s.getDescripcion(),
                s.getDetalle(), s.getPrecio(), s.getUnidad(), s.getContactoProveedor(),
                s.getUsuario() == null ? "Estudiante UVG" : s.getUsuario().getNombre(),
                promedio, resenas.size(), propietario, miCalificacion, horario, vistas);
        }).toList();
    }
}
