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

    // Número de la jornada (1, 2, 3, etc.)
    private Integer numeroJornada;

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

    public Integer getNumeroJornada() { return numeroJornada; }
    public void setNumeroJornada(Integer numeroJornada) { this.numeroJornada = numeroJornada; }

    public LigaCume getLiga() { return liga; }
    public void setLiga(LigaCume liga) { this.liga = liga; }

    public List<Partido> getPartidos() { return partidos; }
    public void setPartidos(List<Partido> partidos) { this.partidos = partidos; }
    public List<Alineacion> getAlineaciones() { return alineaciones; }
    public void setAlineaciones(List<Alineacion> alineaciones) { this.alineaciones = alineaciones; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Jornada jornada = (Jornada) o;
        return Objects.equals(idJornada, jornada.idJornada) && Objects.equals(liga, jornada.liga) && Objects.equals(partidos, jornada.partidos) && Objects.equals(alineaciones, jornada.alineaciones);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idJornada, liga, partidos, alineaciones);
    }
}
