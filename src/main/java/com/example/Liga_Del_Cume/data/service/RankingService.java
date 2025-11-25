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
     *
     * @param ligaId ID de la liga
     * @return Lista de usuarios ordenados por puntos acumulados
     * @throws IllegalArgumentException si el ID es nulo
     * @throws RuntimeException si la liga no existe o no tiene usuarios
     */
    public List<Usuario> obtenerRankingGeneral(Long ligaId) {
        if (ligaId == null) {
            throw new IllegalArgumentException("El ID de la liga no puede ser nulo");
        }

        List<Usuario> ranking = usuarioService.obtenerRankingLiga(ligaId);

        if (ranking.isEmpty()) {
            throw new RuntimeException("No hay usuarios registrados en la liga con ID: " + ligaId);
        }

        return ranking;
    }

    /**
     * Funcionalidad 10: Obtener ranking de una jornada específica
     * Ordena los usuarios por los puntos obtenidos en esa jornada
     *
     * @param jornadaId ID de la jornada
     * @return Lista de mapas con información de usuarios y puntos
     * @throws IllegalArgumentException si el ID es nulo
     * @throws RuntimeException si no hay alineaciones en la jornada
     */
    public List<Map<String, Object>> obtenerRankingJornada(Long jornadaId) {
        if (jornadaId == null) {
            throw new IllegalArgumentException("El ID de la jornada no puede ser nulo");
        }

        List<Alineacion> alineaciones = alineacionService.listarAlineacionesPorJornada(jornadaId);

        if (alineaciones.isEmpty()) {
            throw new RuntimeException("No hay alineaciones para la jornada con ID: " + jornadaId);
        }

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
     *
     * @param usuarioId ID del usuario
     * @param jornadaId ID de la jornada
     * @return Mapa con detalles de la puntuación
     * @throws IllegalArgumentException si algún ID es nulo
     * @throws RuntimeException si no se encuentra la alineación
     */
    public Map<String, Object> obtenerDetallePuntuacionJornada(Long usuarioId, Long jornadaId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (jornadaId == null) {
            throw new IllegalArgumentException("El ID de la jornada no puede ser nulo");
        }

        Alineacion alineacion = alineacionService.consultarAlineacion(usuarioId, jornadaId);

        if (alineacion == null) {
            throw new RuntimeException("No se encontró alineación para el usuario " + usuarioId +
                    " en la jornada " + jornadaId);
        }

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
     *
     * @param ligaId ID de la liga
     * @throws IllegalArgumentException si el ID es nulo
     * @throws RuntimeException si la liga no tiene usuarios
     */
    public void actualizarPuntosAcumulados(Long ligaId) {
        if (ligaId == null) {
            throw new IllegalArgumentException("El ID de la liga no puede ser nulo");
        }

        List<Usuario> usuarios = usuarioService.listarUsuariosPorLiga(ligaId);

        if (usuarios.isEmpty()) {
            throw new RuntimeException("No hay usuarios en la liga con ID: " + ligaId);
        }

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
     *
     * @param ligaId ID de la liga
     * @param usuarioId ID del usuario
     * @return Posición del usuario (1-indexed), -1 si no se encuentra
     * @throws IllegalArgumentException si algún ID es nulo
     */
    public int obtenerPosicionUsuario(Long ligaId, Long usuarioId) {
        if (ligaId == null) {
            throw new IllegalArgumentException("El ID de la liga no puede ser nulo");
        }
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        List<Usuario> ranking = obtenerRankingGeneral(ligaId);

        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.get(i).getIdUsuario().equals(usuarioId)) {
                return i + 1;
            }
        }

        throw new RuntimeException("Usuario con ID " + usuarioId + " no encontrado en la liga " + ligaId);
    }

    /**
     * Obtiene el top N usuarios con más puntos en una liga
     *
     * @param ligaId ID de la liga
     * @param topN Número de usuarios a devolver
     * @return Lista de los N mejores usuarios
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public List<Usuario> obtenerTopUsuarios(Long ligaId, int topN) {
        if (ligaId == null) {
            throw new IllegalArgumentException("El ID de la liga no puede ser nulo");
        }
        if (topN <= 0) {
            throw new IllegalArgumentException("El número de usuarios debe ser positivo");
        }

        List<Usuario> ranking = obtenerRankingGeneral(ligaId);

        return ranking.stream()
                .limit(topN)
                .collect(Collectors.toList());
    }

    /**
     * Compara las puntuaciones de dos usuarios en una liga
     *
     * @param ligaId ID de la liga
     * @param usuarioId1 ID del primer usuario
     * @param usuarioId2 ID del segundo usuario
     * @return Mapa con información comparativa
     * @throws IllegalArgumentException si algún ID es nulo
     */
    public Map<String, Object> compararUsuarios(Long ligaId, Long usuarioId1, Long usuarioId2) {
        if (ligaId == null) {
            throw new IllegalArgumentException("El ID de la liga no puede ser nulo");
        }
        if (usuarioId1 == null || usuarioId2 == null) {
            throw new IllegalArgumentException("Los IDs de usuarios no pueden ser nulos");
        }
        if (usuarioId1.equals(usuarioId2)) {
            throw new IllegalArgumentException("No se puede comparar un usuario consigo mismo");
        }

        Usuario usuario1 = usuarioService.obtenerUsuario(usuarioId1);
        Usuario usuario2 = usuarioService.obtenerUsuario(usuarioId2);

        int posicion1 = obtenerPosicionUsuario(ligaId, usuarioId1);
        int posicion2 = obtenerPosicionUsuario(ligaId, usuarioId2);

        Map<String, Object> comparacion = new HashMap<>();
        comparacion.put("usuario1", Map.of(
                "nombre", usuario1.getNombreUsuario(),
                "puntos", usuario1.getPuntosAcumulados(),
                "posicion", posicion1
        ));
        comparacion.put("usuario2", Map.of(
                "nombre", usuario2.getNombreUsuario(),
                "puntos", usuario2.getPuntosAcumulados(),
                "posicion", posicion2
        ));
        comparacion.put("diferenciaPuntos", Math.abs(usuario1.getPuntosAcumulados() - usuario2.getPuntosAcumulados()));
        comparacion.put("diferenciaPosiciones", Math.abs(posicion1 - posicion2));

        return comparacion;
    }
}
