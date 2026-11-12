package com.proyecto.serviuvg.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Embeddable
public class FranjaDisponibilidad {

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 12)
    private DayOfWeek dia;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    protected FranjaDisponibilidad() {
    }

    public FranjaDisponibilidad(DayOfWeek dia, LocalTime horaInicio, LocalTime horaFin) {
        if (dia == null || horaInicio == null || horaFin == null || !horaInicio.isBefore(horaFin)) {
            throw new IllegalArgumentException("El horario debe tener un día y una hora de inicio anterior a la de fin.");
        }
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public DayOfWeek getDia() { return dia; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }

    public boolean incluye(LocalDate fecha, LocalTime hora) {
        return fecha != null && hora != null && fecha.getDayOfWeek() == dia
            && !hora.isBefore(horaInicio) && hora.isBefore(horaFin);
    }
}
