package com.proyecto.serviuvg;

import com.proyecto.serviuvg.dto.ServicioCatalogo;
import com.proyecto.serviuvg.entity.Resena;
import com.proyecto.serviuvg.entity.Servicio;
import com.proyecto.serviuvg.entity.Usuario;
import com.proyecto.serviuvg.repository.ResenaRepository;
import com.proyecto.serviuvg.repository.ServicioRepository;
import com.proyecto.serviuvg.repository.UsuarioRepository;
import com.proyecto.serviuvg.service.ResenaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ResenasIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ResenaService resenaService;
    @Autowired ResenaRepository resenas;
    @Autowired ServicioRepository servicios;
    @Autowired UsuarioRepository usuarios;

    private Usuario autor;
    private Usuario ana;
    private Usuario luis;
    private Servicio servicio;

    @BeforeEach
    void preparar() {
        autor = usuarios.save(new Usuario("autor", "hash-no-publico", null));
        ana = usuarios.save(new Usuario("ana", "hash-no-publico", null));
        luis = usuarios.save(new Usuario("luis", "hash-no-publico", null));
        servicio = crearServicio("Tutoría Java");
    }

    private Servicio crearServicio(String titulo) {
        Servicio s = new Servicio();
        s.setTitulo(titulo);
        s.setCategoria("programacion");
        s.setDescripcion("Apoyo de programación");
        s.setDetalle("Tutoría de programación orientada a objetos.");
        s.setPrecio(50.0);
        s.setUnidad("por hora");
        s.setUsuario(autor);
        return servicios.saveAndFlush(s);
    }

    @Test
    void catalogoPublicoRenderizaResenasSinExponerContrasenas() throws Exception {
        resenaService.calificar(servicio.getId(), "ana", 4, "Muy buena tutoría");
        mvc.perform(get("/servicios"))
            .andExpect(status().isOk())
            .andExpect(view().name("servicios"))
            .andExpect(content().string(containsString("Muy buena tutor")))
            .andExpect(content().string(not(containsString("hash-no-publico"))));
    }

    @Test
    void permiteUnaResenaPorCuentaYCalculaPromedioPublico() throws Exception {
        mvc.perform(post("/servicios/resenar").with(user("ana")).with(csrf())
                .param("servicioId", servicio.getId().toString())
                .param("estrellas", "5").param("comentario", "Excelente"))
            .andExpect(redirectedUrl("/servicios?resenas=" + servicio.getId()))
            .andExpect(flash().attributeExists("exitoResena"));
        mvc.perform(post("/servicios/resenar").with(user("luis")).with(csrf())
                .param("servicioId", servicio.getId().toString()).param("estrellas", "4"))
            .andExpect(flash().attributeExists("exitoResena"));

        ServicioCatalogo publico = resenaService.listarCatalogo(null).getFirst();
        assertEquals(2, publico.cantidadResenas());
        assertEquals(4.5, publico.promedio(), 0.00001);
        assertEquals(2, publico.resenas().size());
        assertNull(publico.miCalificacion());
        assertEquals(5, resenaService.listarCatalogo("ana").getFirst().miCalificacion());
        assertTrue(resenaService.listarCatalogo("autor").getFirst().propietario());

        mvc.perform(post("/servicios/resenar").with(user("ana")).with(csrf())
                .param("servicioId", servicio.getId().toString()).param("estrellas", "1"))
            .andExpect(flash().attributeExists("errorResena"));
        assertEquals(2, resenas.count());
        assertEquals(4.5, resenaService.listarCatalogo(null).getFirst().promedio(), 0.00001);
    }

    @Test
    void autorNoPuedeCalificarseAunqueManipuleElFormulario() throws Exception {
        mvc.perform(post("/servicios/resenar").with(user("autor")).with(csrf())
                .param("servicioId", servicio.getId().toString()).param("estrellas", "5")
                .param("usuarioId", ana.getId().toString()))
            .andExpect(flash().attributeExists("errorResena"));
        assertEquals(0, resenas.count());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "6", "-1", "1.5", "abc", ""})
    void rechazaEstrellasInvalidas(String estrellas) throws Exception {
        mvc.perform(post("/servicios/resenar").with(user("ana")).with(csrf())
                .param("servicioId", servicio.getId().toString()).param("estrellas", estrellas))
            .andExpect(flash().attributeExists("errorResena"));
        assertEquals(0, resenas.count());
    }

    @Test
    void rechazaEstrellasAusentesYComentarioDemasiadoLargo() throws Exception {
        mvc.perform(post("/servicios/resenar").with(user("ana")).with(csrf())
                .param("servicioId", servicio.getId().toString()))
            .andExpect(flash().attributeExists("errorResena"));
        mvc.perform(post("/servicios/resenar").with(user("ana")).with(csrf())
                .param("servicioId", servicio.getId().toString())
                .param("estrellas", "5").param("comentario", "x".repeat(501)))
            .andExpect(flash().attributeExists("errorResena"));
        assertEquals(0, resenas.count());
    }

    @Test
    void exigeSesionYTokenCsrfParaPublicar() throws Exception {
        mvc.perform(post("/servicios/resenar").with(csrf())
                .param("servicioId", servicio.getId().toString()).param("estrellas", "5"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
        mvc.perform(post("/servicios/resenar").with(user("ana"))
                .param("servicioId", servicio.getId().toString()).param("estrellas", "5"))
            .andExpect(status().isForbidden());
        assertEquals(0, resenas.count());
    }

    @Test
    void cuentaPuedeCalificarServiciosDistintosYSinHaberContratado() {
        Servicio otro = crearServicio("Otra tutoría");
        resenaService.calificar(servicio.getId(), "ana", 1, null);
        resenaService.calificar(otro.getId(), "ana", 5, "");
        assertEquals(2, resenas.count());
    }

    @Test
    void servicioSinResenasNoRecibeCalificacionesInventadas() {
        ServicioCatalogo catalogo = resenaService.listarCatalogo(null).getFirst();
        assertEquals(0, catalogo.promedio());
        assertEquals(0, catalogo.cantidadResenas());
        assertTrue(catalogo.resenas().isEmpty());
    }

    @Test
    void manejaServicioEliminadoYCuentaInexistente() throws Exception {
        mvc.perform(post("/servicios/resenar").with(user("ana")).with(csrf())
                .param("servicioId", "9223372036854775807").param("estrellas", "5"))
            .andExpect(flash().attributeExists("errorResena"));
        mvc.perform(post("/servicios/resenar").with(user("desconocido")).with(csrf())
                .param("servicioId", servicio.getId().toString()).param("estrellas", "5"))
            .andExpect(flash().attributeExists("errorResena"));
        assertEquals(0, resenas.count());
    }

    @Test
    void eliminarServicioEliminaTambienSusResenas() throws Exception {
        resenaService.calificar(servicio.getId(), "ana", 5, "Muy bueno");
        mvc.perform(post("/servicios/eliminar").with(user("autor")).with(csrf())
                .param("id", servicio.getId().toString()))
            .andExpect(redirectedUrl("/perfil"));
        servicios.flush();
        assertFalse(servicios.existsById(servicio.getId()));
        assertEquals(0, resenas.count());
    }

    @Test
    void baseDeDatosImpideDuplicadosAunqueSeOmitaLaValidacionDelServicio() {
        resenas.saveAndFlush(new Resena(servicio, ana, 5, "Primera"));
        assertThrows(DataIntegrityViolationException.class,
            () -> resenas.saveAndFlush(new Resena(servicio, ana, 1, "Repetida")));
    }
}