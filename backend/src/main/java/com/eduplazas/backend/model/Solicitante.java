package com.eduplazas.backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Solicitante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellidos;
    private String email;
    private double notaBase; // Nota sin ponderar

    @OneToMany(mappedBy = "solicitante", cascade = CascadeType.ALL)
    private List<NotaAsignatura> notas;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public double getNotaBase() { return notaBase; }
    public void setNotaBase(double notaBase) { this.notaBase = notaBase; }
    public List<NotaAsignatura> getNotas() { return notas; }
    public void setNotas(List<NotaAsignatura> notas) { this.notas = notas; }

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}