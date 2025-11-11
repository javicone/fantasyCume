package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.Alineacion;
import com.example.Liga_Del_Cume.data.model.EstadisticaJugadorPartido;
import com.example.Liga_Del_Cume.data.model.Jornada;
import com.example.Liga_Del_Cume.data.model.Jugador;
import com.example.Liga_Del_Cume.data.model.Usuario;
import com.example.Liga_Del_Cume.data.repository.AlineacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar alineaciones de usuarios
 * Funcionalidades:
 * - 6.1, 6.2 y 6.3: Crear alineación para la jornada (listar, seleccionar, consultar)
 * - 10: Puntuaciones de usuarios por jornada
 */
@Service
@Transactional
public class AlineacionService {

    @Autowired
    private AlineacionRepository alineacionRepository;

    @Autowired
    private EstadisticaService estadisticaService;

    /**
     * Funcionalidad 6.2: Crear una alineación para un usuario en una jornada
     */
    public Alineacion crearAlineacion(Usuario usuario, Jornada jornada, List<Jugador> jugadores) {
        Alineacion alineacion = new Alineacion();
        alineacion.setUsuario(usuario);
        alineacion.setJornada(jornada);
        alineacion.setJugadores(jugadores);
        alineacion.setPuntosTotalesJornada(0); // Se calculará después
        return alineacionRepository.save(alineacion);
    }

    /**
     * Funcionalidad 6.3: Consultar equipo alineado de un usuario en una jornada
     */
    public Alineacion consultarAlineacion(Long usuarioId, Long jornadaId) {
        return alineacionRepository.findByUsuarioIdUsuarioAndJornadaIdJornada(usuarioId, jornadaId)
                .orElseThrow(() -> new RuntimeException("Alineación no encontrada para el usuario y jornada especificados"));
    }

    /**
     * Funcionalidad 10: Calcular y actualizar puntos totales de una alineación
     * Suma los puntos de las estadísticas de cada jugador en la jornada
     */
    public Alineacion calcularPuntosAlineacion(Long alineacionId) {
        Alineacion alineacion = alineacionRepository.findById(alineacionId)
                .orElseThrow(() -> new RuntimeException("Alineación no encontrada con ID: " + alineacionId));

        int puntosTotales = 0;
        Long jornadaId = alineacion.getJornada().getIdJornada();

        // Para cada jugador de la alineación, buscar sus estadísticas en esta jornada
        for (Jugador jugador : alineacion.getJugadores()) {
            List<EstadisticaJugadorPartido> estadisticas =
                    estadisticaService.obtenerEstadisticasJugador(jugador.getIdJugador());

            // Filtrar por jornada y sumar puntos
            for (EstadisticaJugadorPartido est : estadisticas) {
                if (est.getPartido().getJornada().getIdJornada().equals(jornadaId)) {
                    puntosTotales += est.getPuntosJornada();
                }
            }
        }

        alineacion.setPuntosTotalesJornada(puntosTotales);
        return alineacionRepository.save(alineacion);
    }

    /**
     * Modifica la alineación de un usuario (cambia los jugadores seleccionados)
     */
    public Alineacion modificarAlineacion(Long alineacionId, List<Jugador> nuevosJugadores) {
        Alineacion alineacion = alineacionRepository.findById(alineacionId)
                .orElseThrow(() -> new RuntimeException("Alineación no encontrada con ID: " + alineacionId));

        alineacion.getJugadores().clear();
        alineacion.getJugadores().addAll(nuevosJugadores);

        return alineacionRepository.save(alineacion);
    }

    /**
     * Lista todas las alineaciones de un usuario
     */
    public List<Alineacion> listarAlineacionesPorUsuario(Long usuarioId) {
        return alineacionRepository.findByUsuarioIdUsuario(usuarioId);
    }

    /**
     * Funcionalidad 10: Obtener todas las alineaciones de una jornada
     */
    public List<Alineacion> listarAlineacionesPorJornada(Long jornadaId) {
        return alineacionRepository.findByJornadaIdJornada(jornadaId);
    }

    /**
     * Obtiene una alineación por su ID
     */
    public Alineacion obtenerAlineacion(Long id) {
        return alineacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alineación no encontrada con ID: " + id));
    }

    /**
     * Elimina una alineación
     */
    public void eliminarAlineacion(Long id) {
        alineacionRepository.deleteById(id);
    }

    /**
     * Elimina todas las alineaciones de un usuario
     */
    public void eliminarAlineacionesPorUsuario(Long usuarioId) {
        List<Alineacion> alineaciones = alineacionRepository.findByUsuarioIdUsuario(usuarioId);
        alineacionRepository.deleteAll(alineaciones);
    }

    /**
     * Lista todas las alineaciones
     */
    public List<Alineacion> listarTodasLasAlineaciones() {
        return alineacionRepository.findAll();
    }
}

