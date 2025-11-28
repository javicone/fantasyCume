package com.example.Liga_Del_Cume.data.exceptions;

/**
 * Excepción personalizada para errores relacionados con jugadores
 */
public class JugadorException extends RuntimeException {

    public JugadorException(String mensaje) {
        super(mensaje);
    }

    public JugadorException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

