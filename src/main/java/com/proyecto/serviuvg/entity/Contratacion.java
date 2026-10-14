package com.proyecto.serviuvg.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "contrataciones")
public class Contratacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @Column(nullable = false)
    private String fechaDeseada;

    @Column(nullable = false, length = 120)
    private String contactoCliente;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(nullable = false, length = 60)
    private String metodoPago;

    @Column(nullable = false)
    private Double montoPago;

    @Column(nullable = false, length = 30)
    private String estado;

    public Contratacion() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }

    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }

    public String getFechaDeseada() { return fechaDeseada; }
    public void setFechaDeseada(String fechaDeseada) { this.fechaDeseada = fechaDeseada; }

    public String getContactoCliente() { return contactoCliente; }
    public void setContactoCliente(String contactoCliente) { this.contactoCliente = contactoCliente; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public Double getMontoPago() { return montoPago; }
    public void setMontoPago(Double montoPago) { this.montoPago = montoPago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}