package com.example.Liga_Del_Cume.data.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

// Entidad que representa un jugador dentro de un equipo
@Entity
public class Jugador {
    // Primary key para la entidad Jugador
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idJugador;

    // Relación N a 1: Muchos jugadores pertenecen a un equipo
    @ManyToOne
    @JoinColumn(name = "equipo_id")
    private Equipo equipo;

    @ManyToMany(mappedBy = "jugadores", fetch = FetchType.LAZY)
    private List<Alineacion> alineaciones = new ArrayList<>();

    @OneToMany(mappedBy = "jugador", cascade = CascadeType.ALL)
    private List<EstadisticaJugadorPartido> estadisticas = new ArrayList<>();

    private float precioMercado;
    private String nombreJugador; // Aquí irá nombre y apellidos
    private boolean esPortero; // true = portero, false = no portero

    public Jugador() {}

    public Jugador(String nombreJugador, boolean esPortero, Equipo equipo) {
        this.nombreJugador = nombreJugador;
        this.esPortero = esPortero;
        this.equipo = equipo;
    }

    // Getters y Setters
    public Long getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(Long idJugador) {
        this.idJugador = idJugador;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public List<Alineacion> getAlineaciones() {
        return alineaciones;
    }

    public void setAlineaciones(List<Alineacion> alineaciones) {
        this.alineaciones = alineaciones;
    }

    public List<EstadisticaJugadorPartido> getEstadisticas() {
        return estadisticas;
    }

    public void setEstadisticas(List<EstadisticaJugadorPartido> estadisticas) {
        this.estadisticas = estadisticas;
    }

    public float getPrecioMercado() {
        return precioMercado;
    }

    public void setPrecioMercado(float precioMercado) {
        this.precioMercado = precioMercado;
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    public boolean isEsPortero() {
        return esPortero;
    }

    public void setEsPortero(boolean esPortero) {
        this.esPortero = esPortero;
    }
}
