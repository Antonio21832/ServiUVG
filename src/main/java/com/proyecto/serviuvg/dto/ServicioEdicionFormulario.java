package com.proyecto.serviuvg.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ServicioEdicionFormulario {

    @NotBlank(message = "Escribe el título del servicio.")
    @Size(max = 255, message = "El título admite hasta 255 caracteres.")
    private String titulo;

    @NotBlank(message = "Selecciona una categoría.")
    @Pattern(regexp = "tutorias|diseno|programacion|otros",
             message = "Selecciona una categoría válida.")
    private String categoria;

    @NotBlank(message = "Escribe una descripción corta.")
    @Size(max = 100, message = "La descripción admite hasta 100 caracteres.")
    private String descripcion;

    @NotBlank(message = "Escribe el detalle del servicio.")
    @Size(max = 10000, message = "El detalle admite hasta 10000 caracteres.")
    private String detalle;

    @NotNull(message = "Ingresa el precio del servicio.")
    @DecimalMin(value = "0.00", message = "El precio debe ser mayor o igual a cero.")
    @Digits(integer = 7, fraction = 2,
            message = "El precio admite hasta 7 enteros y 2 decimales.")
    private BigDecimal precio;

    @NotBlank(message = "Selecciona una unidad de cobro.")
    @Pattern(regexp = "Por hora|Por clase|Por proyecto|Tarifa fija|Otro",
             message = "Selecciona una unidad de cobro válida.")
    private String unidad;

    @NotBlank(message = "Escribe tu medio de contacto.")
    @Size(max = 120, message = "El contacto admite hasta 120 caracteres.")
    private String contactoProveedor;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getContactoProveedor() {
        return contactoProveedor;
    }

    public void setContactoProveedor(String contactoProveedor) {
        this.contactoProveedor = contactoProveedor;
    }
}