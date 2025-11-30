package com.example.Liga_Del_Cume.data.model;
import jakarta.persistence.*;
import java.util.*;

// Entidad que representa un equipo dentro de una liga
@Entity
public class Equipo {
    // Primary key para la entidad Equipo
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEquipo;

    // Relación N a 1: Muchos equipos pertenecen a una liga
    @ManyToOne (fetch =  FetchType.EAGER)
    @JoinColumn(name = "liga_id")
    private LigaCume liga;

    @Column(columnDefinition = "TEXT")
    private String escudoURL;

    private String nombreEquipo;

    // Relación 1 a N: Un equipo tiene muchos jugadores
    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Jugador> jugadores = new ArrayList<>();

    @OneToMany(mappedBy="equipoLocal", fetch = FetchType.EAGER)
    private List<Partido> partidosLocal;

    @OneToMany(mappedBy="equipoVisitante", fetch = FetchType.EAGER)
    private List<Partido> partidosVisitante;
    public Equipo() {}

    public Equipo(String nombreEquipo, List<Jugador> jugadores, LigaCume liga) {
        this.nombreEquipo = nombreEquipo;
        this.jugadores = jugadores;
        this.liga = liga;
    }

    // Getters y Setters
    public Long getIdEquipo() { return idEquipo; }
    public void setIdEquipo(Long idEquipo) { this.idEquipo = idEquipo; }
    public LigaCume getLiga() { return liga; }
    public void setLiga(LigaCume liga) { this.liga = liga; }
    public String getNombreEquipo() { return nombreEquipo; }
    public void setNombreEquipo(String nombreEquipo) { this.nombreEquipo = nombreEquipo; }
    public List<Jugador> getJugadores() { return jugadores; }
    public void setJugadores(List<Jugador> jugadores) { this.jugadores = jugadores; }

    public String getEscudoURL() {
        return escudoURL;
    }

    public void setEscudoURL(String escudoURL) {
        this.escudoURL = escudoURL;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Equipo equipo = (Equipo) o;
        return Objects.equals(idEquipo, equipo.idEquipo) && Objects.equals(liga, equipo.liga) && Objects.equals(escudoURL, equipo.escudoURL) && Objects.equals(nombreEquipo, equipo.nombreEquipo) && Objects.equals(jugadores, equipo.jugadores) && Objects.equals(partidosLocal, equipo.partidosLocal) && Objects.equals(partidosVisitante, equipo.partidosVisitante);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEquipo, liga, escudoURL, nombreEquipo, jugadores, partidosLocal, partidosVisitante);
    }
}
