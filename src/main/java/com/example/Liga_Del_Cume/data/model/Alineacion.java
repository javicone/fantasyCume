package com.example.Liga_Del_Cume.data.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
public class Alineacion {
    // Primary key para la entidad Alineacion
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlineacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idJornada")
    private Jornada jornada;

    private int puntosTotalesJornada;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "alineacion_jugadores",
            joinColumns = @JoinColumn(name = "alineacion_id"),
            inverseJoinColumns = @JoinColumn(name = "jugador_id")
    )
    private List<Jugador> jugadores = new ArrayList<>();

    public Alineacion() {}

    // Getters y Setters
    public Long getIdAlineacion() {
        return idAlineacion;
    }

    public void setIdAlineacion(Long idAlineacion) {
        this.idAlineacion = idAlineacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        this.jornada = jornada;
    }

    public int getPuntosTotalesJornada() {
        return puntosTotalesJornada;
    }

    public void setPuntosTotalesJornada(int puntosJornada) {
        this.puntosTotalesJornada = puntosJornada;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Alineacion that = (Alineacion) o;
        return puntosTotalesJornada == that.puntosTotalesJornada && Objects.equals(idAlineacion, that.idAlineacion) && Objects.equals(usuario, that.usuario) && Objects.equals(jornada, that.jornada) && Objects.equals(jugadores, that.jugadores);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAlineacion, usuario, jornada, puntosTotalesJornada, jugadores);
    }
}
