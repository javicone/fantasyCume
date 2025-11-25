package com.example.Liga_Del_Cume.data.service.exceptions;

/**
 * Excepción personalizada para errores relacionados con usuarios
 */
public class UsuarioException extends RuntimeException {

    public UsuarioException(String mensaje) {
        super(mensaje);
    }

    public UsuarioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

