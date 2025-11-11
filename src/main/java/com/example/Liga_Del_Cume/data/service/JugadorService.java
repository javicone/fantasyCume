package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.Equipo;
import com.example.Liga_Del_Cume.data.model.Jugador;
import com.example.Liga_Del_Cume.data.repository.JugadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar jugadores
 * Funcionalidades:
 * - 2.1 a 2.4: Agregar, actualizar, eliminar y listar jugadores
 * - 6.1: Listar jugadores disponibles por posición
 * - 7.1: Buscar jugadores (filtrar por nombre, puntos o equipo)
 */
@Service
@Transactional
public class JugadorService {

    @Autowired
    private JugadorRepository jugadorRepository;

    /**
     * Funcionalidad 2.1: Agregar un jugador a un equipo
     */
    public Jugador agregarJugador(String nombre, boolean esPortero, Equipo equipo, float precio) {
        Jugador jugador = new Jugador(nombre, esPortero, equipo);
        jugador.setPrecioMercado(precio);
        return jugadorRepository.save(jugador);
    }

    /**
     * Funcionalidad 2.2: Actualizar información de un jugador
     */
    public Jugador actualizarJugador(Long id, String nuevoNombre, Float nuevoPrecio, Boolean esPortero) {
        Jugador jugador = jugadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + id));

        if (nuevoNombre != null) {
            jugador.setNombreJugador(nuevoNombre);
        }
        if (nuevoPrecio != null) {
            jugador.setPrecioMercado(nuevoPrecio);
        }
        if (esPortero != null) {
            jugador.setEsPortero(esPortero);
        }

        return jugadorRepository.save(jugador);
    }

    /**
     * Funcionalidad 2.3: Eliminar un jugador de un equipo
     */
    public void eliminarJugador(Long id) {
        jugadorRepository.deleteById(id);
    }

    /**
     * Funcionalidad 2.4: Listar todos los jugadores
     */
    public List<Jugador> listarTodosLosJugadores() {
        return jugadorRepository.findAll();
    }

    /**
     * Lista jugadores de un equipo específico
     */
    public List<Jugador> listarJugadoresPorEquipo(Long equipoId) {
        return jugadorRepository.findByEquipoIdEquipo(equipoId);
    }

    /**
     * Funcionalidad 6.1: Listar jugadores disponibles por posición
     */
    public List<Jugador> listarPorteros() {
        return jugadorRepository.findByEsPortero(true);
    }

    /**
     * Funcionalidad 6.1: Listar jugadores de campo
     */
    public List<Jugador> listarJugadoresDeCampo() {
        return jugadorRepository.findByEsPortero(false);
    }

    /**
     * Funcionalidad 7.1: Buscar jugador por nombre (parcial)
     */
    public List<Jugador> buscarPorNombre(String nombre) {
        return jugadorRepository.findByNombreJugadorContainingIgnoreCase(nombre);
    }

    /**
     * Funcionalidad 7.1: Buscar jugadores por equipo
     */
    public List<Jugador> buscarPorEquipo(Long equipoId) {
        return jugadorRepository.findByEquipoIdEquipo(equipoId);
    }

    /**
     * Obtiene un jugador por su ID
     */
    public Jugador obtenerJugador(Long id) {
        return jugadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + id));
    }

    /**
     * Funcionalidad 7.1: Buscar jugadores ordenados por puntos totales
     */
    public List<Jugador> buscarJugadoresOrdenadosPorPuntos() {
        // Este método debería ordenar por la suma total de puntos en estadísticas
        // Por ahora devuelve todos los jugadores
        return jugadorRepository.findAll();
    }
}

