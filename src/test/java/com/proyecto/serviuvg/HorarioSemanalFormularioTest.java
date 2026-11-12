package com.proyecto.serviuvg;

import com.proyecto.serviuvg.dto.HorarioSemanalFormulario;
import com.proyecto.serviuvg.entity.FranjaDisponibilidad;
import com.proyecto.serviuvg.entity.Servicio;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HorarioSemanalFormularioTest {

    @Test
    void guardaSoloLosDiasSeleccionadosYPermiteEditarElHorario() {
        HorarioSemanalFormulario formulario = new HorarioSemanalFormulario();
        formulario.getDias().get(0).setActivo(true);
        formulario.getDias().get(0).setHoraInicio("09:00");
        formulario.getDias().get(0).setHoraFin("12:00");
        formulario.getDias().get(1).setHoraInicio("18:00"); // Martes sin marcar.

        Servicio servicio = new Servicio();
        servicio.setDisponibilidad(formulario.convertir());
        HorarioSemanalFormulario edicion = HorarioSemanalFormulario.desdeServicio(servicio);

        assertEquals(1, servicio.getDisponibilidad().size());
        assertTrue(edicion.getDias().get(0).isActivo());
        assertEquals("09:00", edicion.getDias().get(0).getHoraInicio());
        assertFalse(edicion.getDias().get(1).isActivo());
    }

    @Test
    void impideHorasIncompletasOInvertidas() {
        HorarioSemanalFormulario formulario = new HorarioSemanalFormulario();
        formulario.getDias().get(2).setActivo(true);
        assertThrows(IllegalArgumentException.class, formulario::convertir);

        formulario.getDias().get(2).setHoraInicio("17:00");
        formulario.getDias().get(2).setHoraFin("09:00");
        assertThrows(IllegalArgumentException.class, formulario::convertir);

        formulario.getDias().get(2).setHoraFin("19:00");
        assertEquals(DayOfWeek.WEDNESDAY, formulario.convertir().getFirst().getDia());
    }

    @Test
    void laSolicitudSoloCoincideConSuDiaYSuRango() {
        FranjaDisponibilidad franja = new FranjaDisponibilidad(DayOfWeek.MONDAY,
            LocalTime.of(9, 0), LocalTime.of(12, 0));
        LocalDate lunes = LocalDate.of(2026, 11, 16);

        assertTrue(franja.incluye(lunes, LocalTime.of(9, 0)));
        assertTrue(franja.incluye(lunes, LocalTime.of(11, 59)));
        assertFalse(franja.incluye(lunes, LocalTime.of(12, 0)));
        assertFalse(franja.incluye(lunes.plusDays(1), LocalTime.of(10, 0)));
        assertFalse(franja.incluye(lunes, LocalTime.of(8, 59)));
    }

    @Test
    void unServicioAntiguoPuedeSeguirSinHorario() {
        Servicio servicio = new Servicio();
        assertEquals(List.of(), HorarioSemanalFormulario.desdeServicio(servicio).convertir());
    }
}
