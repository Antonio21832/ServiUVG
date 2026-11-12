package com.proyecto.serviuvg.dto;

import com.proyecto.serviuvg.entity.FranjaDisponibilidad;
import com.proyecto.serviuvg.entity.Servicio;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Un tramo horario por día, repetido cada semana. */
public class HorarioSemanalFormulario {

    private final List<Dia> dias = Arrays.stream(DayOfWeek.values())
        .map(Dia::new).collect(java.util.stream.Collectors.toCollection(ArrayList::new));

    public List<Dia> getDias() { return dias; }

    public static HorarioSemanalFormulario desdeServicio(Servicio servicio) {
        HorarioSemanalFormulario formulario = new HorarioSemanalFormulario();
        for (FranjaDisponibilidad franja : servicio.getDisponibilidad()) {
            Dia dia = formulario.dias.get(franja.getDia().ordinal());
            dia.setActivo(true);
            dia.setHoraInicio(franja.getHoraInicio().toString());
            dia.setHoraFin(franja.getHoraFin().toString());
        }
        return formulario;
    }

    public List<FranjaDisponibilidad> convertir() {
        List<FranjaDisponibilidad> franjas = new ArrayList<>();
        for (Dia dia : dias) {
            if (!dia.isActivo()) continue;
            if (!esHoraValida(dia.getHoraInicio()) || !esHoraValida(dia.getHoraFin())) {
                throw new IllegalArgumentException("Indica las horas de inicio y fin para " + dia.getEtiqueta() + ".");
            }
            LocalTime inicio = LocalTime.parse(dia.getHoraInicio());
            LocalTime fin = LocalTime.parse(dia.getHoraFin());
            if (!inicio.isBefore(fin)) {
                throw new IllegalArgumentException("La hora de fin debe ser posterior al inicio el " + dia.getEtiqueta() + ".");
            }
            franjas.add(new FranjaDisponibilidad(dia.getDia(), inicio, fin));
        }
        return franjas;
    }

    private static boolean esHoraValida(String hora) {
        return hora != null && hora.matches("(?:[01][0-9]|2[0-3]):[0-5][0-9]");
    }

    public static class Dia {
        private final DayOfWeek dia;
        private boolean activo;
        private String horaInicio;
        private String horaFin;

        public Dia(DayOfWeek dia) { this.dia = dia; }
        public DayOfWeek getDia() { return dia; }
        public boolean isActivo() { return activo; }
        public void setActivo(boolean activo) { this.activo = activo; }
        public String getHoraInicio() { return horaInicio; }
        public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }
        public String getHoraFin() { return horaFin; }
        public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

        public String getEtiqueta() {
            return switch (dia) {
                case MONDAY -> "lunes";
                case TUESDAY -> "martes";
                case WEDNESDAY -> "miércoles";
                case THURSDAY -> "jueves";
                case FRIDAY -> "viernes";
                case SATURDAY -> "sábado";
                case SUNDAY -> "domingo";
            };
        }
    }
}
