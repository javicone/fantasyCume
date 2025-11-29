package com.example.Liga_Del_Cume.data.model;

/**
 * DTO (Data Transfer Object) para representar la clasificación de un equipo
 *
 * Esta clase se utiliza para transferir información de clasificación
 * de equipos desde el servicio hasta la vista, incluyendo:
 * - Datos del equipo (nombre, escudo)
 * - Estadísticas (victorias, empates, derrotas)
 * - Puntos totales calculados
 */
public class ClasificacionEquipo implements Comparable<ClasificacionEquipo> {

    private Long idEquipo;
    private String nombreEquipo;
    private String escudoURL;
    private int victorias;
    private int empates;
    private int derrotas;
    private int puntosTotales;
    private int golesAFavor;
    private int golesEnContra;

    public ClasificacionEquipo() {}

    public ClasificacionEquipo(Long idEquipo, String nombreEquipo, String escudoURL) {
        this.idEquipo = idEquipo;
        this.nombreEquipo = nombreEquipo;
        this.escudoURL = escudoURL;
        this.victorias = 0;
        this.empates = 0;
        this.derrotas = 0;
        this.puntosTotales = 0;
        this.golesAFavor = 0;
        this.golesEnContra = 0;
    }

    /**
     * Calcula los puntos totales según la fórmula:
     * Victoria = 3 puntos
     * Empate = 1 punto
     * Derrota = 0 puntos
     */
    public void calcularPuntos() {
        this.puntosTotales = (victorias * 3) + (empates * 1);
    }

    /**
     * Añade una victoria al equipo
     */
    public void addVictoria() {
        this.victorias++;
        calcularPuntos();
    }

    /**
     * Añade un empate al equipo
     */
    public void addEmpate() {
        this.empates++;
        calcularPuntos();
    }

    /**
     * Añade una derrota al equipo
     */
    public void addDerrota() {
        this.derrotas++;
        calcularPuntos();
    }

    /**
     * Añade goles a favor y en contra
     */
    public void addGoles(int aFavor, int enContra) {
        this.golesAFavor += aFavor;
        this.golesEnContra += enContra;
    }

    /**
     * Calcula la diferencia de goles
     */
    public int getDiferenciaGoles() {
        return golesAFavor - golesEnContra;
    }

    /**
     * Implementa Comparable para ordenar por:
     * 1. Puntos (descendente)
     * 2. Diferencia de goles (descendente)
     * 3. Goles a favor (descendente)
     */
    @Override
    public int compareTo(ClasificacionEquipo otro) {
        // Primero comparar por puntos (descendente)
        if (this.puntosTotales != otro.puntosTotales) {
            return Integer.compare(otro.puntosTotales, this.puntosTotales);
        }

        // Si tienen los mismos puntos, comparar por diferencia de goles
        int difGolesThis = this.getDiferenciaGoles();
        int difGolesOtro = otro.getDiferenciaGoles();
        if (difGolesThis != difGolesOtro) {
            return Integer.compare(difGolesOtro, difGolesThis);
        }

        // Si tienen la misma diferencia, comparar por goles a favor
        return Integer.compare(otro.golesAFavor, this.golesAFavor);
    }

    // Getters y Setters
    public Long getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Long idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public String getEscudoURL() {
        return escudoURL;
    }

    public void setEscudoURL(String escudoURL) {
        this.escudoURL = escudoURL;
    }

    public int getVictorias() {
        return victorias;
    }

    public void setVictorias(int victorias) {
        this.victorias = victorias;
    }

    public int getEmpates() {
        return empates;
    }

    public void setEmpates(int empates) {
        this.empates = empates;
    }

    public int getDerrotas() {
        return derrotas;
    }

    public void setDerrotas(int derrotas) {
        this.derrotas = derrotas;
    }

    public int getPuntosTotales() {
        return puntosTotales;
    }

    public void setPuntosTotales(int puntosTotales) {
        this.puntosTotales = puntosTotales;
    }

    public int getGolesAFavor() {
        return golesAFavor;
    }

    public void setGolesAFavor(int golesAFavor) {
        this.golesAFavor = golesAFavor;
    }

    public int getGolesEnContra() {
        return golesEnContra;
    }

    public void setGolesEnContra(int golesEnContra) {
        this.golesEnContra = golesEnContra;
    }

    public int getPartidosJugados() {
        return victorias + empates + derrotas;
    }
}

