package com.example.Liga_Del_Cume.data.service.exceptions;

/**
 * Excepción personalizada para errores relacionados con equipos
 */
public class EquipoException extends RuntimeException {

    public EquipoException(String mensaje) {
        super(mensaje);
    }

    public EquipoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

