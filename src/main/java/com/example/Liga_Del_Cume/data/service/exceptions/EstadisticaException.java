package com.example.Liga_Del_Cume.data.service.exceptions;

/**
 * Excepción personalizada para errores relacionados con estadísticas
 */
public class EstadisticaException extends RuntimeException {

    public EstadisticaException(String mensaje) {
        super(mensaje);
    }

    public EstadisticaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

