package com.proyecto.serviuvg.entity;

import jakarta.persistence.*;
//Ayuda a estrucutar la base de datos, definiendo los roles que existen en el sistema, como "ADMIN" y "USER". Esto permite asignar permisos y controlar el acceso a diferentes funcionalidades de la aplicación según el rol del usuario
@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombre;

    public Rol() {}

    public Rol(String nombre) {
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}