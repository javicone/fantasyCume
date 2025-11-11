package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.Alineacion;
import com.example.Liga_Del_Cume.data.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar rankings y clasificaciones
 * Funcionalidades:
 * - 8: Ver clasificación general de usuarios
 * - 10: Consultar puntuación total por jornada
 */
@Service
@Transactional
public class RankingService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AlineacionService alineacionService;

    /**
     * Funcionalidad 8: Consultar ranking de todos los usuarios de una liga
     * Ordena por puntos acumulados de forma descendente
     */
    public List<Usuario> obtenerRankingGeneral(Long ligaId) {
        return usuarioService.obtenerRankingLiga(ligaId);
    }

    /**
     * Funcionalidad 10: Obtener ranking de una jornada específica
     * Ordena los usuarios por los puntos obtenidos en esa jornada
     */
    public List<Map<String, Object>> obtenerRankingJornada(Long jornadaId) {
        List<Alineacion> alineaciones = alineacionService.listarAlineacionesPorJornada(jornadaId);

        return alineaciones.stream()
                .sorted((a1, a2) -> Integer.compare(a2.getPuntosTotalesJornada(), a1.getPuntosTotalesJornada()))
                .map(alineacion -> {
                    Map<String, Object> resultado = new HashMap<>();
                    resultado.put("usuario", alineacion.getUsuario().getNombreUsuario());
                    resultado.put("puntos", alineacion.getPuntosTotalesJornada());
                    resultado.put("jugadores", alineacion.getJugadores());
                    return resultado;
                })
                .collect(Collectors.toList());
    }

    /**
     * Funcionalidad 10: Mostrar detalle de puntuación de un usuario en una jornada
     * Incluye jugadores seleccionados y sus respectivas puntuaciones
     */
    public Map<String, Object> obtenerDetallePuntuacionJornada(Long usuarioId, Long jornadaId) {
        Alineacion alineacion = alineacionService.consultarAlineacion(usuarioId, jornadaId);

        Map<String, Object> detalle = new HashMap<>();
        detalle.put("usuario", alineacion.getUsuario().getNombreUsuario());
        detalle.put("jornada", alineacion.getJornada().getIdJornada());
        detalle.put("puntosTotales", alineacion.getPuntosTotalesJornada());
        detalle.put("jugadores", alineacion.getJugadores());

        return detalle;
    }

    /**
     * Actualiza los puntos acumulados de todos los usuarios de una liga
     * basándose en sus alineaciones
     */
    public void actualizarPuntosAcumulados(Long ligaId) {
        List<Usuario> usuarios = usuarioService.listarUsuariosPorLiga(ligaId);

        for (Usuario usuario : usuarios) {
            int puntosAcumulados = alineacionService.listarAlineacionesPorUsuario(usuario.getIdUsuario())
                    .stream()
                    .mapToInt(Alineacion::getPuntosTotalesJornada)
                    .sum();

            usuario.setPuntosAcumulados(puntosAcumulados);
            usuarioService.modificarUsuario(usuario.getIdUsuario(), null, puntosAcumulados);
        }
    }

    /**
     * Obtiene la posición de un usuario en el ranking general
     */
    public int obtenerPosicionUsuario(Long ligaId, Long usuarioId) {
        List<Usuario> ranking = obtenerRankingGeneral(ligaId);

        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.get(i).getIdUsuario().equals(usuarioId)) {
                return i + 1; // Posición comienza en 1
            }
        }

        return -1; // No encontrado
    }
}

