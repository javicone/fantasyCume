package com.example.Liga_Del_Cume.data.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@IdClass(EstadisticasJugadorPartidoId.class) // <--- ¡AQUÍ ESTÁ!
// Le dice a JPA: "Usa OrderItemId para gestionar
// los campos @Id de esta clase".
public class EstadisticaJugadorPartido {



    @Id
    @ManyToOne
    @JoinColumn(name = "partido_id", insertable = true, updatable = false)
    private Partido partido;

    @Id
    @ManyToOne
    @JoinColumn(name = "jugador_id", insertable = true, updatable = false)
    private Jugador jugador;

    private int golesAnotados;
    private int golesRecibidos; // Solo para porteros
    private int asistencias;
    private int tarjetaAmarillas;
    private boolean tarjetaRojas;
    private boolean minMinutosJugados; // true si ha jugado más de 25 minutos, false en caso contrario
    private int puntosJornada;

    public EstadisticaJugadorPartido() {}

    public EstadisticaJugadorPartido(Jugador jugador, Partido partido) {
        this.jugador = jugador;
        this.partido = partido;
    }



    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }

    public Partido getPartido() {
        return partido;
    }

    public void setPartido(Partido partido) {
        this.partido = partido;
    }

    public int getGolesAnotados() {
        return golesAnotados;
    }

    public void setGolesAnotados(int golesAnotados) {
        this.golesAnotados = golesAnotados;
    }

    public int getGolesRecibidos() {
        return golesRecibidos;
    }

    public void setGolesRecibidos(int golesRecibidos) {
        this.golesRecibidos = golesRecibidos;
    }

    public int getAsistencias() {
        return asistencias;
    }

    public void setAsistencias(int asistencias) {
        this.asistencias = asistencias;
    }

    public int getTarjetaAmarillas() {
        return tarjetaAmarillas;
    }

    public void setTarjetaAmarillas(int tarjetaAmarillas) {
        this.tarjetaAmarillas = tarjetaAmarillas;
    }

    public boolean isTarjetaRojas() {
        return tarjetaRojas;
    }

    public void setTarjetaRojas(boolean tarjetaRojas) {
        this.tarjetaRojas = tarjetaRojas;
    }

    public boolean isMinMinutosJugados() {
        return minMinutosJugados;
    }

    public void setMinMinutosJugados(boolean minMinutosJugados) {
        this.minMinutosJugados = minMinutosJugados;
    }

    public int getPuntosJornada() {
        return puntosJornada;
    }

    public void setPuntosJornada(int puntosJornada) {
        this.puntosJornada = puntosJornada;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EstadisticaJugadorPartido that = (EstadisticaJugadorPartido) o;
        return golesAnotados == that.golesAnotados && golesRecibidos == that.golesRecibidos && asistencias == that.asistencias && tarjetaAmarillas == that.tarjetaAmarillas && tarjetaRojas == that.tarjetaRojas && minMinutosJugados == that.minMinutosJugados && puntosJornada == that.puntosJornada && Objects.equals(partido, that.partido) && Objects.equals(jugador, that.jugador);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partido, jugador, golesAnotados, golesRecibidos, asistencias, tarjetaAmarillas, tarjetaRojas, minMinutosJugados, puntosJornada);
    }
}
