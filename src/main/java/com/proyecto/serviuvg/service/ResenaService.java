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

@Service
public class ResenaService {
    private final ResenaRepository resenaRepository;
    private final ServicioRepository servicioRepository;
    private final UsuarioRepository usuarioRepository;

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
