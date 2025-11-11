package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.EstadisticaJugadorPartido;
import com.example.Liga_Del_Cume.data.model.Jugador;
import com.example.Liga_Del_Cume.data.model.Partido;
import com.example.Liga_Del_Cume.data.repository.EstadisticaJugadorPartidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar estadísticas de jugadores por partido
 * Funcionalidades:
 * - 4.1 y 4.2: Añadir y modificar estadísticas de jugadores por partido
 * - 7.1: Consultar estadísticas generales de jugadores
 */
@Service
@Transactional
public class EstadisticaService {

    @Autowired
    private EstadisticaJugadorPartidoRepository estadisticaRepository;

    /**
     * Funcionalidad 4.1: Añadir estadísticas de un jugador en un partido
     */
    public EstadisticaJugadorPartido añadirEstadistica(Jugador jugador, Partido partido,
                                                       int goles, int asistencias,
                                                       int tarjetasAmarillas, boolean tarjetaRoja,
                                                       boolean minMinutosJugados, int golesRecibidos,
                                                       int puntosJornada) {
        EstadisticaJugadorPartido estadistica = new EstadisticaJugadorPartido();
        estadistica.setJugador(jugador);
        estadistica.setPartido(partido);
        estadistica.setGolesAnotados(goles);
        estadistica.setAsistencias(asistencias);
        estadistica.setTarjetaAmarillas(tarjetasAmarillas);
        estadistica.setTarjetaRojas(tarjetaRoja);
        estadistica.setMinMinutosJugados(minMinutosJugados);
        estadistica.setGolesRecibidos(golesRecibidos);
        estadistica.setPuntosJornada(puntosJornada);

        return estadisticaRepository.save(estadistica);
    }

    /**
     * Funcionalidad 4.2: Modificar estadísticas de un jugador en un partido
     */
    public EstadisticaJugadorPartido modificarEstadistica(Long jugadorId, Long partidoId,
                                                          Integer goles, Integer asistencias,
                                                          Integer tarjetasAmarillas, Boolean tarjetaRoja,
                                                          Boolean minMinutosJugados, Integer golesRecibidos,
                                                          Integer puntosJornada) {
        EstadisticaJugadorPartido estadistica = estadisticaRepository
                .findByJugadorIdJugadorAndPartidoIdPartido(jugadorId, partidoId)
                .orElseThrow(() -> new RuntimeException("Estadística no encontrada"));

        if (goles != null) estadistica.setGolesAnotados(goles);
        if (asistencias != null) estadistica.setAsistencias(asistencias);
        if (tarjetasAmarillas != null) estadistica.setTarjetaAmarillas(tarjetasAmarillas);
        if (tarjetaRoja != null) estadistica.setTarjetaRojas(tarjetaRoja);
        if (minMinutosJugados != null) estadistica.setMinMinutosJugados(minMinutosJugados);
        if (golesRecibidos != null) estadistica.setGolesRecibidos(golesRecibidos);
        if (puntosJornada != null) estadistica.setPuntosJornada(puntosJornada);

        return estadisticaRepository.save(estadistica);
    }

    /**
     * Funcionalidad 7.1: Consultar estadísticas de un jugador
     */
    public List<EstadisticaJugadorPartido> obtenerEstadisticasJugador(Long jugadorId) {
        return estadisticaRepository.findByJugadorIdJugador(jugadorId);
    }

    /**
     * Obtiene las estadísticas de un partido específico
     */
    public List<EstadisticaJugadorPartido> obtenerEstadisticasPartido(Long partidoId) {
        return estadisticaRepository.findByPartidoIdPartido(partidoId);
    }

    /**
     * Obtiene una estadística específica de un jugador en un partido
     */
    public EstadisticaJugadorPartido obtenerEstadistica(Long jugadorId, Long partidoId) {
        return estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(jugadorId, partidoId)
                .orElseThrow(() -> new RuntimeException("Estadística no encontrada"));
    }

    /**
     * Obtiene estadísticas de jugadores en una jornada específica
     */
    public List<EstadisticaJugadorPartido> obtenerEstadisticasJornada(Long jornadaId) {
        return estadisticaRepository.findByPartidoJornadaIdJornada(jornadaId);
    }

    /**
     * Elimina una estadística
     */
    public void eliminarEstadistica(Long jugadorId, Long partidoId) {
        EstadisticaJugadorPartido estadistica = obtenerEstadistica(jugadorId, partidoId);
        estadisticaRepository.delete(estadistica);
    }

    /**
     * Lista todas las estadísticas
     */
    public List<EstadisticaJugadorPartido> listarTodasLasEstadisticas() {
        return estadisticaRepository.findAll();
    }
}

