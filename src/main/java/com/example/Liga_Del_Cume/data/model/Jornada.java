package com.example.Liga_Del_Cume.data.model;
import jakarta.persistence.*;
import java.util.*;

// Entidad que representa una jornada dentro de una liga
@Entity
public class Jornada {
    // Primary key para la entidad Jornada
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idJornada;

    // Relación N a 1: Muchas jornadas pertenecen a una liga
    @ManyToOne
    @JoinColumn(name = "liga_id")
    private LigaCume liga;

    // Relación 1 a N: Una jornada tiene muchos partidos
    @OneToMany(mappedBy = "jornada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Partido> partidos = new ArrayList<>();

    @OneToMany(mappedBy = "jornada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Alineacion> alineaciones = new ArrayList<>();

    public Jornada() {}

    public Jornada( List<Partido> partidos, LigaCume liga) {
        this.partidos = partidos;
        this.liga = liga;
    }

    // Getters y Setters
    public Long getIdJornada() { return idJornada; }
    public void setIdJornada(Long idJornada) { this.idJornada = idJornada; }
    public LigaCume getLiga() { return liga; }
    public void setLiga(LigaCume liga) { this.liga = liga; }

    public List<Partido> getPartidos() { return partidos; }
    public void setPartidos(List<Partido> partidos) { this.partidos = partidos; }
    public List<Alineacion> getAlineaciones() { return alineaciones; }
    public void setAlineaciones(List<Alineacion> alineaciones) { this.alineaciones = alineaciones; }
}
