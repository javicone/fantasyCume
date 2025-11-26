package com.example.Liga_Del_Cume.data.exceptions;

/**
 * Excepción personalizada para errores relacionados con jornadas
 */
public class JornadaException extends RuntimeException {

    public JornadaException(String mensaje) {
        super(mensaje);
    }

    public JornadaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

