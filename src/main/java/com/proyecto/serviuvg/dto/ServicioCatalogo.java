package com.proyecto.serviuvg.dto;

import java.util.List;

// Solo datos públicos: nunca enviamos entidades de usuario ni contraseñas al navegador.
public record ServicioCatalogo(
    Long id, String titulo, String categoria, String subcategoria, String descripcion, String detalle,
    Double precio, String unidad, String contactoProveedor, String vendedor,
    double promedio, int cantidadResenas, boolean propietario, Integer miCalificacion,
    List<ResenaVista> resenas
) {
    // Getters JavaBean para el serializador JavaScript de Thymeleaf,
    // incluso si se utiliza sin Jackson 2 en Spring Boot 4.
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getCategoria() { return categoria; }
    public String getDescripcion() { return descripcion; }
    public String getDetalle() { return detalle; }
    public Double getPrecio() { return precio; }
    public String getUnidad() { return unidad; }
    public String getContactoProveedor() { return contactoProveedor; }
    public String getVendedor() { return vendedor; }
    public double getPromedio() { return promedio; }
    public int getCantidadResenas() { return cantidadResenas; }
    public boolean isPropietario() { return propietario; }
    public Integer getMiCalificacion() { return miCalificacion; }
    public List<ResenaVista> getResenas() { return resenas; }
    public String getSubcategoria() { return subcategoria; }

    public record ResenaVista(String autor, int estrellas, String comentario, String fecha) {
        public String getAutor() { return autor; }
        public int getEstrellas() { return estrellas; }
        public String getComentario() { return comentario; }
        public String getFecha() { return fecha; }
    }
}