package com.example.Liga_Del_Cume.data.exceptions;

/**
 * Excepción personalizada para errores relacionados con las operaciones de liga.
 *
 * Se lanza cuando ocurren errores durante la gestión de ligas, como:
 * - Liga no encontrada
 * - Nombre de liga duplicado
 * - Presupuesto inválido
 * - Validaciones de datos fallidas
 */
public class LigaException extends RuntimeException {

    /**
     * Constructor con mensaje de error
     *
     * @param mensaje Descripción del error
     */
    public LigaException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa
     *
     * @param mensaje Descripción del error
     * @param causa Causa raíz del error
     */
    public LigaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

