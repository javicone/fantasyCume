package com.example.Liga_Del_Cume.data.service.exceptions;

/**
 * Excepción personalizada para errores relacionados con alineaciones
 */
public class AlineacionException extends RuntimeException {

    public AlineacionException(String mensaje) {
        super(mensaje);
    }

    public AlineacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

