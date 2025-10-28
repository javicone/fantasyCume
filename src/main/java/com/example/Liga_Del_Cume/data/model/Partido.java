package com.example.Liga_Del_Cume.data.model;

import jakarta.persistence.*;
import java.util.*;
// Entidad que representa un partido dentro de una jornada
@Entity
public class Partido {
    // Primary key para la entidad Partido
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPartido;

    // Relación N a 1: Muchos partidos pertenecen a una jornada
    @ManyToOne
    @JoinColumn(name = "jornada_id")
    private Jornada jornada;

    // Relación N a 1: Un partido tiene un equipo local
    @ManyToOne
    @JoinColumn(name = "equipo_local_id")
    private Equipo equipoLocal;

    // Relación N a 1: Un partido tiene un equipo visitante
    @ManyToOne
    @JoinColumn(name = "equipo_visitante_id")
    private Equipo equipoVisitante;

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL)
    private List<EstadisticaJugadorPartido> estadisticas;

    private int golesLocal;
    private int golesVisitante;

    public Partido() {}

    public Partido(Equipo equipoLocal, Equipo equipoVisitante, int golesLocal, int golesVisitante, Jornada jornada) {
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        this.jornada = jornada;
    }

    // Getters y Setters
    public Long getIdPartido() { return idPartido; }
    public void setIdPartido(Long idPartido) { this.idPartido = idPartido; }
    public Jornada getJornada() { return jornada; }
    public void setJornada(Jornada jornada) { this.jornada = jornada; }
    public Equipo getEquipoLocal() { return equipoLocal; }
    public void setEquipoLocal(Equipo equipoLocal) { this.equipoLocal = equipoLocal; }
    public Equipo getEquipoVisitante() { return equipoVisitante; }
    public void setEquipoVisitante(Equipo equipoVisitante) { this.equipoVisitante = equipoVisitante; }
    public int getGolesLocal() { return golesLocal; }
    public void setGolesLocal(int golesLocal) { this.golesLocal = golesLocal; }
    public int getGolesVisitante() { return golesVisitante; }
    public void setGolesVisitante(int golesVisitante) { this.golesVisitante = golesVisitante; }
    public List<EstadisticaJugadorPartido> getEstadisticas() { return estadisticas; }
    public void setEstadisticas(List<EstadisticaJugadorPartido> estadisticas) { this.estadisticas = estadisticas; }
}
