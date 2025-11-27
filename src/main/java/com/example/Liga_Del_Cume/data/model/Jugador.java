package com.example.Liga_Del_Cume.data.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Entidad que representa un jugador dentro de un equipo
@Entity
public class Jugador {
    // Primary key para la entidad Jugador
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idJugador;

    private String avatarUrl;
    // Relación N a 1: Muchos jugadores pertenecen a un equipo
    @ManyToOne
    @JoinColumn(name = "equipo_id")
    private Equipo equipo;

    @ManyToMany(mappedBy = "jugadores")
    private List<Alineacion> alineaciones = new ArrayList<>();

    @OneToMany(mappedBy = "jugador", fetch = FetchType.EAGER)
    private List<EstadisticaJugadorPartido> estadisticas = new ArrayList<>();

    private float precioMercado;
    private String nombreJugador; // Aquí irá nombre y apellidos
    private boolean esPortero; // true = portero, false = no portero

    public Jugador() {}

    public Jugador(String nombreJugador, boolean esPortero, Equipo equipo, float precioMercado, String url) {
        this.precioMercado = precioMercado;
        this.nombreJugador = nombreJugador;
        this.esPortero = esPortero;
        this.equipo = equipo;
        this.avatarUrl= url;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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

    @Override
    public String toString() {
        return "Jugador{" +
                "idJugador=" + idJugador +
                ", equipo=" + equipo +
                ", alineaciones=" + alineaciones.size() +
                ", estadisticas=" + estadisticas.size() +
                ", precioMercado=" + precioMercado +
                ", nombreJugador='" + nombreJugador + '\'' +
                ", esPortero=" + esPortero +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Jugador jugador = (Jugador) o;
        return Float.compare(precioMercado, jugador.precioMercado) == 0 && esPortero == jugador.esPortero && Objects.equals(idJugador, jugador.idJugador) && Objects.equals(equipo, jugador.equipo) && Objects.equals(alineaciones, jugador.alineaciones) && Objects.equals(estadisticas, jugador.estadisticas) && Objects.equals(nombreJugador, jugador.nombreJugador);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idJugador, equipo, alineaciones, estadisticas, precioMercado, nombreJugador, esPortero);
    }
}
