package com.example.Liga_Del_Cume.data.model;

import java.io.Serializable;
import java.util.Objects;

public class EstadisticasJugadorPartidoId implements Serializable {
    private Long jugador;
    private Long partido;

    public EstadisticasJugadorPartidoId() {
    }

    public EstadisticasJugadorPartidoId(long idJugador, long Partido) {
        this.jugador = idJugador;
        this.partido = Partido;
    }

    public long getIdJugador() {
        return jugador;
    }

    public void setIdJugador(long idJugador) {
        this.jugador = idJugador;
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
