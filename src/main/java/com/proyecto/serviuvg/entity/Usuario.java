package com.proyecto.serviuvg.entity;

import jakarta.persistence.*;
//Es la tabla de usuarios, donde se guardan los datos de los usuarios que se registran en el sistema
//Tiene todos los atributos del usuario, como nombre, contraseña y rol. Esto permite identificar a cada usuario de manera única y asociarlo con un rol específico para controlar el acceso a las funcionalidades del sistema
//Tiene relación de muchoa a uno con la tabla de roles, cada usuario tiene solo un rol, pero los roles pueden estar asignados a varios usarios
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id")
    private Rol rol;

    public Usuario() {}

    public Usuario(String nombre, String password, Rol rol) {
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}