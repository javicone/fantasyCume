package com.example.Liga_Del_Cume.data.model;

import java.io.Serializable;

public class EstadisticasJugadorPartidoId implements Serializable {
    private long idJugador;
    private long idPartido;

    public EstadisticasJugadorPartidoId(){

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
}
