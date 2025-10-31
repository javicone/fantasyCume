package com.example.Liga_Del_Cume.data.model;

import java.io.Serializable;
import java.util.Objects;

public class EstadisticasJugadorPartidoId implements Serializable {
    private long idJugador;
    private long idPartido;

    public EstadisticasJugadorPartidoId() {
    }

    public EstadisticasJugadorPartidoId(long idJugador, long idPartido) {
        this.idJugador = idJugador;
        this.idPartido = idPartido;
    }

    public long getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(long idJugador) {
        this.idJugador = idJugador;
    }

    public long getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(long idPartido) {
        this.idPartido = idPartido;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EstadisticasJugadorPartidoId that = (EstadisticasJugadorPartidoId) o;
        return idJugador == that.idJugador && idPartido == that.idPartido;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idJugador, idPartido);
    }
}
