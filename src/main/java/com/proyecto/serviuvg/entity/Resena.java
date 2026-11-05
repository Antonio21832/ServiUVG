package com.proyecto.serviuvg.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;

@Entity
@Table(name = "resenas", uniqueConstraints = @UniqueConstraint(
    name = "uk_resena_servicio_usuario", columnNames = {"servicio_id", "usuario_id"}
))
@Check(constraints = "estrellas between 1 and 5")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private Integer estrellas;

    @Column(nullable = false, length = 500)
    private String comentario = "";

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    protected Resena() {}

    public Resena(Servicio servicio, Usuario usuario, Integer estrellas, String comentario) {
        this.servicio = servicio;
        this.usuario = usuario;
        this.estrellas = estrellas;
        this.comentario = comentario == null ? "" : comentario.strip();
    }

    public Long getId() { return id; }
    public Servicio getServicio() { return servicio; }
    public Usuario getUsuario() { return usuario; }
    public Integer getEstrellas() { return estrellas; }
    public String getComentario() { return comentario; }
    public LocalDateTime getFecha() { return fecha; }
}