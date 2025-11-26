package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.Alineacion;
import com.example.Liga_Del_Cume.data.model.EstadisticaJugadorPartido;
import com.example.Liga_Del_Cume.data.model.Jornada;
import com.example.Liga_Del_Cume.data.model.Jugador;
import com.example.Liga_Del_Cume.data.model.Partido;
import com.example.Liga_Del_Cume.data.model.Usuario;
import com.example.Liga_Del_Cume.data.repository.AlineacionRepository;
import com.example.Liga_Del_Cume.data.repository.UsuarioRepository;
import com.example.Liga_Del_Cume.data.repository.JornadaRepository;
import com.example.Liga_Del_Cume.data.exceptions.AlineacionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Servicio para gestionar alineaciones de usuarios
 *
 * Funcionalidades principales:
 * - 6.1, 6.2 y 6.3: Crear alineación para la jornada (listar, seleccionar, consultar)
 * - 10: Puntuaciones de usuarios por jornada
 */
@Service
@Transactional
public class AlineacionService {

    @Autowired
    private AlineacionRepository alineacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JornadaRepository jornadaRepository;

    @Autowired
    private EstadisticaService estadisticaService;

    /**
     * Funcionalidad 6.2: Crear una alineación para un usuario en una jornada
     *
     * Validaciones:
     * 1. Verifica que el usuario no sea nulo
     * 2. Verifica que la jornada no sea nula
     * 3. Verifica que la lista de jugadores no sea nula o vacía
     * 4. Verifica que no haya jugadores duplicados en la lista
     * 5. Verifica que la jornada no haya sido ya evaluada (con puntos calculados)
     * 6. Verifica que el usuario no tenga ya una alineación para esta jornada
     *
     * @param usuario Usuario que crea la alineación
     * @param jornada Jornada para la cual se crea la alineación
     * @param jugadores Lista de jugadores seleccionados
     * @return Alineación creada y guardada
     * @throws AlineacionException Si alguna validación falla
     */
    public Alineacion crearAlineacion(Usuario usuario, Jornada jornada, List<Jugador> jugadores) {
        // Validación 1: Comprobar que el usuario no sea nulo
        if (usuario == null) {
            throw new AlineacionException("El usuario no puede ser nulo");
        }

        // Validación 2: Comprobar que la jornada no sea nula
        if (jornada == null) {
            throw new AlineacionException("La jornada no puede ser nula");
        }

        // Validación 3: Comprobar que la lista de jugadores no sea nula o vacía
        if (jugadores == null || jugadores.isEmpty()) {
            throw new AlineacionException("Debe seleccionar al menos un jugador para la alineación");
        }

        // Validación 4: Comprobar que no haya jugadores duplicados en la lista
        Set<Long> idsJugadores = new HashSet<>();
        for (Jugador jugador : jugadores) {
            if (jugador == null) {
                throw new AlineacionException("No se pueden incluir jugadores nulos en la alineación");
            }
            if (!idsJugadores.add(jugador.getIdJugador())) {
                throw new AlineacionException(
                    "El jugador '" + jugador.getNombreJugador() +
                    "' está duplicado en la alineación. Cada jugador solo puede aparecer una vez."
                );
            }
        }

        // Validación 5: Comprobar que la jornada no haya sido ya evaluada
        // (verificamos si tiene partidos con estadísticas calculadas)
        if (esJornadaEvaluada(jornada)) {
            throw new AlineacionException(
                "No se puede crear una alineación para la jornada " + jornada.getIdJornada() +
                " porque ya ha sido evaluada y sus puntos han sido calculados."
            );
        }

        // Validación 6: Comprobar que el usuario no tenga ya una alineación para esta jornada
        if (alineacionRepository.existsByUsuarioIdUsuarioAndJornadaIdJornada(
                usuario.getIdUsuario(), jornada.getIdJornada())) {
            throw new AlineacionException(
                "El usuario '" + usuario.getNombreUsuario() +
                "' ya tiene una alineación creada para la jornada " + jornada.getIdJornada() +
                ". No se permiten alineaciones duplicadas."
            );
        }

        // Si todas las validaciones pasan, crear la alineación
        Alineacion alineacion = new Alineacion();
        alineacion.setUsuario(usuario);
        alineacion.setJornada(jornada);
        alineacion.setJugadores(jugadores);
        alineacion.setPuntosTotalesJornada(0); // Se calculará después
        return alineacionRepository.save(alineacion);
    }

    /**
     * Método auxiliar para verificar si una jornada ya ha sido evaluada
     * Una jornada se considera evaluada si alguno de sus partidos tiene estadísticas con puntos calculados
     */
    private boolean esJornadaEvaluada(Jornada jornada) {
        if (jornada.getPartidos() == null || jornada.getPartidos().isEmpty()) {
            return false; // Si no hay partidos, la jornada no está evaluada
        }

        // Verificar si algún partido tiene estadísticas con puntos calculados
        for (Partido partido : jornada.getPartidos()) {
            if (partido.getEstadisticas() != null && !partido.getEstadisticas().isEmpty()) {
                for (EstadisticaJugadorPartido estadistica : partido.getEstadisticas()) {
                    if (estadistica.getPuntosJornada() > 0) {
                        return true; // La jornada ya fue evaluada
                    }
                }
            }
        }

        return false; // La jornada no ha sido evaluada aún
    }

    /**
     * Funcionalidad 6.3: Consultar equipo alineado de un usuario en una jornada
     *
     * Validaciones:
     * 1. Verifica que los IDs no sean nulos o inválidos
     * 2. Verifica que el usuario exista en la base de datos
     * 3. Verifica que la jornada exista en la base de datos
     * 4. Verifica que exista una alineación para ese usuario y jornada
     *
     * @param usuarioId ID del usuario
     * @param jornadaId ID de la jornada
     * @return Alineación encontrada
     * @throws AlineacionException Si alguna validación falla
     */
    public Alineacion consultarAlineacion(Long usuarioId, Long jornadaId) {
        // Validación 1: Verificar que los IDs no sean nulos
        if (usuarioId == null) {
            throw new AlineacionException("El ID del usuario no puede ser nulo");
        }

        if (jornadaId == null) {
            throw new AlineacionException("El ID de la jornada no puede ser nulo");
        }

        // Validación 2: Verificar que los IDs sean valores positivos
        if (usuarioId <= 0) {
            throw new AlineacionException("El ID del usuario debe ser un valor positivo: " + usuarioId);
        }

        if (jornadaId <= 0) {
            throw new AlineacionException("El ID de la jornada debe ser un valor positivo: " + jornadaId);
        }

        // Validación 3: Verificar que el usuario exista
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new AlineacionException(
                    "No existe ningún usuario con ID: " + usuarioId
                ));

        // Validación 4: Verificar que la jornada exista
        Jornada jornada = jornadaRepository.findById(jornadaId)
                .orElseThrow(() -> new AlineacionException(
                    "No existe ninguna jornada con ID: " + jornadaId
                ));

        // Validación 5: Verificar que la jornada tenga partidos asignados (sea válida)
        if (jornada.getPartidos() == null || jornada.getPartidos().isEmpty()) {
            throw new AlineacionException(
                "La jornada " + jornadaId + " no es válida porque no tiene partidos asignados"
            );
        }

        // Validación 6: Buscar la alineación y verificar que exista
        return alineacionRepository.findByUsuarioIdUsuarioAndJornadaIdJornada(usuarioId, jornadaId)
                .orElseThrow(() -> new AlineacionException(
                    "El usuario '" + usuario.getNombreUsuario() +
                    "' (ID: " + usuarioId + ") no tiene una alineación creada para la jornada " +
                    jornadaId + ". Debe crear una alineación primero."
                ));
    }

    /**
     * Funcionalidad 10: Calcular y actualizar puntos totales de una alineación
     *
     * Este método suma los puntos de las estadísticas de cada jugador en la jornada
     * y actualiza el total en la alineación. Solo se debe ejecutar después de que
     * la jornada haya sido evaluada (partidos jugados y estadísticas registradas).
     *
     * Validaciones:
     * 1. Verifica que el ID no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que la alineación exista
     * 4. Verifica que la jornada haya sido evaluada (debe tener estadísticas con puntos)
     *
     * @param alineacionId ID de la alineación
     * @return Alineación con puntos actualizados
     * @throws AlineacionException Si alguna validación falla
     */
    public Alineacion calcularPuntosAlineacion(Long alineacionId) {
        // Validación 1: Verificar que el ID no sea nulo
        if (alineacionId == null) {
            throw new AlineacionException("El ID de la alineación no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (alineacionId <= 0) {
            throw new AlineacionException("El ID de la alineación debe ser un valor positivo: " + alineacionId);
        }

        // Validación 3: Verificar que la alineación exista
        Alineacion alineacion = alineacionRepository.findById(alineacionId)
                .orElseThrow(() -> new AlineacionException(
                    "No existe ninguna alineación con ID: " + alineacionId
                ));

        // Validación 4: Verificar que la jornada haya sido evaluada
        // No tiene sentido calcular puntos si los partidos no se han jugado
        if (!esJornadaEvaluada(alineacion.getJornada())) {
            throw new AlineacionException(
                "No se pueden calcular los puntos porque la jornada " +
                alineacion.getJornada().getIdJornada() +
                " aún no ha sido evaluada. Debe registrar las estadísticas de los partidos primero."
            );
        }

        // Inicializar contador de puntos
        int puntosTotales = 0;
        Long jornadaId = alineacion.getJornada().getIdJornada();

        // Para cada jugador de la alineación, buscar sus estadísticas en esta jornada
        for (Jugador jugador : alineacion.getJugadores()) {
            // Obtener todas las estadísticas del jugador
            List<EstadisticaJugadorPartido> estadisticas =
                    estadisticaService.obtenerEstadisticasJugador(jugador.getIdJugador());

            // Filtrar por jornada y sumar puntos
            for (EstadisticaJugadorPartido est : estadisticas) {
                // Solo sumar puntos de partidos de esta jornada específica
                if (est.getPartido().getJornada().getIdJornada().equals(jornadaId)) {
                    puntosTotales += est.getPuntosJornada();
                }
            }
        }

        // Actualizar los puntos totales de la alineación
        alineacion.setPuntosTotalesJornada(puntosTotales);

        // Guardar y retornar la alineación actualizada
        return alineacionRepository.save(alineacion);
    }

    /**
     * Modifica la alineación de un usuario (cambia los jugadores seleccionados)
     *
     * Este método permite cambiar los jugadores de una alineación existente.
     * Solo se puede modificar antes de que la jornada sea evaluada, protegiendo
     * así la integridad del historial de jornadas completadas.
     *
     * Validaciones:
     * 1. Verifica que el ID no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que la alineación exista
     * 4. Verifica que la lista de jugadores no sea nula o vacía
     * 5. Verifica que no haya jugadores duplicados
     * 6. Verifica que la jornada no haya sido evaluada
     *
     * @param alineacionId ID de la alineación a modificar
     * @param nuevosJugadores Nueva lista de jugadores
     * @return Alineación modificada
     * @throws AlineacionException Si alguna validación falla
     */
    public Alineacion modificarAlineacion(Long alineacionId, List<Jugador> nuevosJugadores) {
        // Validación 1: Verificar que el ID no sea nulo
        if (alineacionId == null) {
            throw new AlineacionException("El ID de la alineación no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (alineacionId <= 0) {
            throw new AlineacionException("El ID de la alineación debe ser un valor positivo: " + alineacionId);
        }

        // Validación 3: Verificar que la alineación exista
        Alineacion alineacion = alineacionRepository.findById(alineacionId)
                .orElseThrow(() -> new AlineacionException(
                    "No existe ninguna alineación con ID: " + alineacionId
                ));

        // Validación 4: Verificar que la lista de jugadores no sea nula o vacía
        if (nuevosJugadores == null || nuevosJugadores.isEmpty()) {
            throw new AlineacionException(
                "Debe proporcionar al menos un jugador para modificar la alineación"
            );
        }

        // Validación 5: Verificar que no haya jugadores duplicados en la nueva lista
        Set<Long> idsJugadores = new HashSet<>();
        for (Jugador jugador : nuevosJugadores) {
            if (jugador == null) {
                throw new AlineacionException("No se pueden incluir jugadores nulos en la alineación");
            }
            // Si add() retorna false, el ID ya existía
            if (!idsJugadores.add(jugador.getIdJugador())) {
                throw new AlineacionException(
                    "El jugador '" + jugador.getNombreJugador() +
                    "' está duplicado. Cada jugador solo puede aparecer una vez."
                );
            }
        }

        // Validación 6: Verificar que la jornada no haya sido evaluada
        // No se puede modificar una alineación después de que los partidos se jugaron
        if (esJornadaEvaluada(alineacion.getJornada())) {
            throw new AlineacionException(
                "No se puede modificar la alineación porque la jornada " +
                alineacion.getJornada().getIdJornada() +
                " ya ha sido evaluada y sus puntos han sido calculados."
            );
        }

        // Si todas las validaciones pasan, modificar la alineación
        // Limpiar la lista actual de jugadores
        alineacion.getJugadores().clear();
        // Agregar los nuevos jugadores
        alineacion.getJugadores().addAll(nuevosJugadores);

        // Guardar y retornar la alineación modificada
        return alineacionRepository.save(alineacion);
    }

    /**
     * Lista todas las alineaciones de un usuario
     *
     * Este método recupera el historial completo de alineaciones de un usuario
     * a lo largo de todas las jornadas en las que ha participado.
     *
     * Validaciones:
     * 1. Verifica que el ID del usuario no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que el usuario exista en la base de datos
     *
     * @param usuarioId ID del usuario
     * @return Lista de alineaciones del usuario (puede estar vacía si no tiene alineaciones)
     * @throws AlineacionException Si alguna validación falla
     */
    public List<Alineacion> listarAlineacionesPorUsuario(Long usuarioId) {
        // Validación 1: Verificar que el ID no sea nulo
        if (usuarioId == null) {
            throw new AlineacionException("El ID del usuario no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (usuarioId <= 0) {
            throw new AlineacionException(
                "El ID del usuario debe ser un valor positivo: " + usuarioId
            );
        }

        // Validación 3: Verificar que el usuario exista
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new AlineacionException(
                    "No existe ningún usuario con ID: " + usuarioId
                ));

        // Buscar y retornar todas las alineaciones del usuario
        return alineacionRepository.findByUsuarioIdUsuario(usuarioId);
    }

    /**
     * Funcionalidad 10: Obtener todas las alineaciones de una jornada
     *
     * Este método recupera todas las alineaciones creadas para una jornada específica.
     * Es útil para generar rankings, estadísticas y comparativas entre usuarios.
     *
     * Validaciones:
     * 1. Verifica que el ID de la jornada no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que la jornada exista en la base de datos
     * 4. Verifica que la jornada sea válida (tenga partidos asignados)
     *
     * @param jornadaId ID de la jornada
     * @return Lista de alineaciones de la jornada (puede estar vacía si nadie ha creado alineaciones)
     * @throws AlineacionException Si alguna validación falla
     */
    public List<Alineacion> listarAlineacionesPorJornada(Long jornadaId) {
        // Validación 1: Verificar que el ID no sea nulo
        if (jornadaId == null) {
            throw new AlineacionException("El ID de la jornada no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (jornadaId <= 0) {
            throw new AlineacionException(
                "El ID de la jornada debe ser un valor positivo: " + jornadaId
            );
        }

        // Validación 3: Verificar que la jornada exista
        Jornada jornada = jornadaRepository.findById(jornadaId)
                .orElseThrow(() -> new AlineacionException(
                    "No existe ninguna jornada con ID: " + jornadaId
                ));

        // Validación 4: Verificar que la jornada tenga partidos asignados (sea válida)
        if (jornada.getPartidos() == null || jornada.getPartidos().isEmpty()) {
            throw new AlineacionException(
                "La jornada " + jornadaId + " no es válida porque no tiene partidos asignados"
            );
        }

        // Buscar y retornar todas las alineaciones de la jornada
        return alineacionRepository.findByJornadaIdJornada(jornadaId);
    }

    /**
     * Obtiene una alineación por su ID
     *
     * Este método recupera una alineación específica usando su identificador único.
     * Es un método básico de consulta útil para operaciones individuales.
     *
     * Validaciones:
     * 1. Verifica que el ID no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que la alineación exista en la base de datos
     *
     * @param id ID de la alineación
     * @return Alineación encontrada
     * @throws AlineacionException Si alguna validación falla
     */
    public Alineacion obtenerAlineacion(Long id) {
        // Validación 1: Verificar que el ID no sea nulo
        if (id == null) {
            throw new AlineacionException("El ID de la alineación no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (id <= 0) {
            throw new AlineacionException("El ID de la alineación debe ser un valor positivo: " + id);
        }

        // Validación 3: Buscar y verificar que exista
        return alineacionRepository.findById(id)
                .orElseThrow(() -> new AlineacionException(
                    "No existe ninguna alineación con ID: " + id
                ));
    }

    /**
     * Elimina una alineación
     *
     * Este método elimina una alineación individual del sistema.
     * Solo se pueden eliminar alineaciones de jornadas no evaluadas,
     * protegiendo así el historial de jornadas completadas.
     *
     * Validaciones:
     * 1. Verifica que el ID no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que la alineación exista en la base de datos
     * 4. Verifica que la jornada no haya sido evaluada (protección del historial)
     *
     * @param id ID de la alineación a eliminar
     * @throws AlineacionException Si alguna validación falla
     */
    public void eliminarAlineacion(Long id) {
        // Validación 1: Verificar que el ID no sea nulo
        if (id == null) {
            throw new AlineacionException("El ID de la alineación no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (id <= 0) {
            throw new AlineacionException("El ID de la alineación debe ser un valor positivo: " + id);
        }

        // Validación 3: Verificar que la alineación exista
        Alineacion alineacion = alineacionRepository.findById(id)
                .orElseThrow(() -> new AlineacionException(
                    "No existe ninguna alineación con ID: " + id
                ));

        // Validación 4: Verificar que la jornada no haya sido evaluada
        // No se puede eliminar el historial de jornadas completadas
        if (esJornadaEvaluada(alineacion.getJornada())) {
            throw new AlineacionException(
                "No se puede eliminar la alineación porque la jornada " +
                alineacion.getJornada().getIdJornada() +
                " ya ha sido evaluada y sus puntos han sido calculados."
            );
        }

        // Si todas las validaciones pasan, eliminar la alineación
        alineacionRepository.deleteById(id);
    }

    /**
     * Elimina todas las alineaciones de un usuario
     *
     * Este método elimina todas las alineaciones de un usuario, pero solo aquellas
     * de jornadas que no han sido evaluadas. Las alineaciones de jornadas completadas
     * se mantienen para preservar el historial.
     *
     * Validaciones:
     * 1. Verifica que el ID del usuario no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que el usuario exista en la base de datos
     * 4. Solo elimina alineaciones de jornadas no evaluadas
     *
     * @param usuarioId ID del usuario
     * @throws AlineacionException Si alguna validación falla o si hay jornadas evaluadas
     */
    public void eliminarAlineacionesPorUsuario(Long usuarioId) {
        // Validación 1: Verificar que el ID no sea nulo
        if (usuarioId == null) {
            throw new AlineacionException("El ID del usuario no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (usuarioId <= 0) {
            throw new AlineacionException("El ID del usuario debe ser un valor positivo: " + usuarioId);
        }

        // Validación 3: Verificar que el usuario exista
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new AlineacionException(
                    "No existe ningún usuario con ID: " + usuarioId
                ));

        // Obtener todas las alineaciones del usuario
        List<Alineacion> alineaciones = alineacionRepository.findByUsuarioIdUsuario(usuarioId);

        // Validación 4: Separar alineaciones eliminables de las que no lo son
        List<Alineacion> alineacionesEliminar = new java.util.ArrayList<>();
        int jornadasEvaluadas = 0;

        // Clasificar alineaciones según si su jornada fue evaluada o no
        for (Alineacion alineacion : alineaciones) {
            if (esJornadaEvaluada(alineacion.getJornada())) {
                // Jornada evaluada: no se puede eliminar (preservar historial)
                jornadasEvaluadas++;
            } else {
                // Jornada no evaluada: se puede eliminar
                alineacionesEliminar.add(alineacion);
            }
        }

        // Eliminar las alineaciones permitidas
        if (!alineacionesEliminar.isEmpty()) {
            alineacionRepository.deleteAll(alineacionesEliminar);
        }

        // Si había jornadas evaluadas, informar al usuario
        if (jornadasEvaluadas > 0) {
            throw new AlineacionException(
                "Se eliminaron " + alineacionesEliminar.size() + " alineaciones del usuario '" +
                usuario.getNombreUsuario() + "', pero " + jornadasEvaluadas +
                " alineaciones no se pudieron eliminar porque sus jornadas ya han sido evaluadas."
            );
        }
    }

    /**
     * Lista todas las alineaciones del sistema
     *
     * Este método retorna todas las alineaciones existentes en la base de datos,
     * independientemente del usuario o jornada. Es útil para:
     * - Administración y supervisión del sistema
     * - Estadísticas globales de participación
     * - Reportes generales de la liga
     * - Auditoría y análisis de datos
     *
     * Nota: Este método puede retornar grandes cantidades de datos.
     * Para consultas específicas, usar listarAlineacionesPorUsuario() o listarAlineacionesPorJornada().
     *
     * @return Lista con todas las alineaciones del sistema (puede estar vacía)
     */
    public List<Alineacion> listarTodasLasAlineaciones() {
        // Obtener todas las alineaciones de la base de datos
        return alineacionRepository.findAll();
    }
}

