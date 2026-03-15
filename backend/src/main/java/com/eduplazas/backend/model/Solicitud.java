package com.eduplazas.backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    private Solicitante solicitante;

    @ManyToOne
    @JoinColumn(name = "convocatoria_id")
    private Convocatoria convocatoria;

    private String estado; // ENVIADA, RESUELTA, RECLAMACION

    @ManyToMany
    @OrderColumn(name = "orden_preferencia")
    @JoinTable(name = "solicitud_preferencias",
        joinColumns = @JoinColumn(name = "solicitud_id"),
        inverseJoinColumns = @JoinColumn(name = "oferta_id"))
    private List<Oferta> preferencias;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Solicitante getSolicitante() { return solicitante; }
    public void setSolicitante(Solicitante solicitante) { this.solicitante = solicitante; }
    public Convocatoria getConvocatoria() { return convocatoria; }
    public void setConvocatoria(Convocatoria convocatoria) { this.convocatoria = convocatoria; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public List<Oferta> getPreferencias() { return preferencias; }
    public void setPreferencias(List<Oferta> preferencias) { this.preferencias = preferencias; }
}