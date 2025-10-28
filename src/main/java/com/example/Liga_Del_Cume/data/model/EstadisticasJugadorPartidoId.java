package com.example.Liga_Del_Cume.data.model;

import java.io.Serializable;
import java.util.Objects;


public class EstadisticasJugadorPartidoId implements Serializable {
    private long jugador;
    private long partido;

    public EstadisticasJugadorPartidoId(){

    }

    public EstadisticasJugadorPartidoId(long jugador, long partido) {
        this.jugador = jugador;
        this.partido = partido;
    }

    public long getJugador() {
        return jugador;
    }

    public void setJugador(long jugador) {
        this.jugador = jugador;
    }

    public long getPartido() {
        return partido;
    }

    public void setPartido(long partido) {
        this.partido = partido;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EstadisticasJugadorPartidoId that = (EstadisticasJugadorPartidoId) o;
        return jugador == that.jugador && partido == that.partido;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jugador, partido);
    }
}
