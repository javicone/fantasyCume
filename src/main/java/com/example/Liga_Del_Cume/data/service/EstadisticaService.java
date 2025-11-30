package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.EstadisticaJugadorPartido;
import com.example.Liga_Del_Cume.data.model.Jugador;
import com.example.Liga_Del_Cume.data.model.Partido;
import com.example.Liga_Del_Cume.data.repository.EstadisticaJugadorPartidoRepository;
import com.example.Liga_Del_Cume.data.repository.JugadorRepository;
import com.example.Liga_Del_Cume.data.repository.PartidoRepository;
import com.example.Liga_Del_Cume.data.repository.JornadaRepository;
import com.example.Liga_Del_Cume.data.repository.UsuarioRepository;
import com.example.Liga_Del_Cume.data.exceptions.EstadisticaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar estadísticas de jugadores por partido
 *
 * Funcionalidades principales:
 * - 4.1: Añadir estadísticas de jugadores por partido
 * - 4.2: Modificar estadísticas de jugadores por partido
 * - 7.1: Consultar estadísticas generales de jugadores
 *
 * Este servicio maneja toda la lógica de negocio relacionada con las estadísticas,
 * incluyendo validaciones exhaustivas para garantizar la integridad de los datos.
 */
@Service
@Transactional
public class EstadisticaService {

    @Autowired
    private EstadisticaJugadorPartidoRepository estadisticaRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private PartidoRepository partidoRepository;

    @Autowired
    private JornadaRepository jornadaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Funcionalidad 4.1: Añadir estadísticas de un jugador en un partido
     *
     * Este método registra las estadísticas de rendimiento de un jugador en un partido específico.
     * Las estadísticas incluyen goles, asistencias, tarjetas, minutos jugados y puntos obtenidos.
     *
     * Validaciones:
     * 1. Verifica que el jugador no sea nulo
     * 2. Verifica que el partido no sea nulo
     * 3. Verifica que el jugador exista en la base de datos
     * 4. Verifica que el partido exista en la base de datos
     * 5. Verifica que los valores numéricos no sean negativos (goles, asistencias, etc.)
     * 6. Verifica que las tarjetas amarillas no superen un máximo razonable (2)
     * 7. Verifica que no exista ya una estadística para este jugador en este partido (evita duplicados)
     *
     * @param jugador Jugador del cual se registran las estadísticas
     * @param partido Partido en el que se registran las estadísticas
     * @param goles Número de goles anotados
     * @param asistencias Número de asistencias realizadas
     * @param tarjetasAmarillas Número de tarjetas amarillas recibidas
     * @param tarjetaRoja Si recibió tarjeta roja (true/false)
     * @param minMinutosJugados Si jugó al menos los minutos mínimos requeridos
     * @param golesRecibidos Goles recibidos (relevante para porteros)
     * @param puntosJornada Puntos de fantasy obtenidos en la jornada
     * @return Estadística creada y guardada
     * @throws EstadisticaException Si alguna validación falla
     */
    public EstadisticaJugadorPartido añadirEstadistica(Jugador jugador, Partido partido,
                                                       int goles, int asistencias,
                                                       int tarjetasAmarillas, boolean tarjetaRoja,
                                                       boolean minMinutosJugados, int golesRecibidos,
                                                       int puntosJornada) {
        // Validación 1: Verificar que el jugador no sea nulo
        if (jugador == null) {
            throw new EstadisticaException("El jugador no puede ser nulo");
        }

        // Validación 2: Verificar que el partido no sea nulo
        if (partido == null) {
            throw new EstadisticaException("El partido no puede ser nulo");
        }

        // Validación 3: Verificar que el jugador exista en la base de datos
        if (jugador.getIdJugador() != null) {
            jugadorRepository.findById(jugador.getIdJugador())
                    .orElseThrow(() -> new EstadisticaException(
                        "No existe ningún jugador con ID: " + jugador.getIdJugador()
                    ));
        }

        // Validación 4: Verificar que el partido exista en la base de datos
        if (partido.getIdPartido() != null) {
            partidoRepository.findById(partido.getIdPartido())
                    .orElseThrow(() -> new EstadisticaException(
                        "No existe ningún partido con ID: " + partido.getIdPartido()
                    ));
        }

        // Validación 5: Verificar que los valores numéricos no sean negativos
        if (goles < 0) {
            throw new EstadisticaException("El número de goles no puede ser negativo: " + goles);
        }

        if (asistencias < 0) {
            throw new EstadisticaException("El número de asistencias no puede ser negativo: " + asistencias);
        }

        if (tarjetasAmarillas < 0) {
            throw new EstadisticaException("El número de tarjetas amarillas no puede ser negativo: " + tarjetasAmarillas);
        }

        if (golesRecibidos < 0) {
            throw new EstadisticaException("El número de goles recibidos no puede ser negativo: " + golesRecibidos);
        }

        // Validación 6: Verificar que las tarjetas amarillas no superen el máximo permitido
        if (tarjetasAmarillas > 2) {
            throw new EstadisticaException(
                "Un jugador no puede recibir más de 2 tarjetas amarillas en un partido. Valor recibido: " + tarjetasAmarillas
            );
        }

        // Validación 7: Verificar que no exista ya una estadística para este jugador en este partido
        EstadisticaJugadorPartido estadisticaExistente = estadisticaRepository
                .findByJugadorIdJugadorAndPartidoIdPartido(jugador.getIdJugador(), partido.getIdPartido());

        if (estadisticaExistente != null) {
            throw new EstadisticaException(
                "Ya existe una estadística para el jugador '" + jugador.getNombreJugador() +
                "' en el partido ID: " + partido.getIdPartido() +
                ". Use el método modificarEstadistica() para actualizar."
            );
        }

        // Si todas las validaciones pasan, crear la estadística
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

        // Guardar y retornar la estadística creada
        return estadisticaRepository.save(estadistica);
    }

    /**
     * Funcionalidad 4.2: Modificar estadísticas de un jugador en un partido
     *
     * Este método permite actualizar las estadísticas de un jugador en un partido específico.
     * Es flexible: puede modificar solo algunos campos, pasando null en los que no se desea cambiar.
     *
     * Validaciones:
     * 1. Verifica que los IDs no sean nulos
     * 2. Verifica que los IDs sean valores positivos
     * 3. Verifica que el jugador exista en la base de datos
     * 4. Verifica que el partido exista en la base de datos
     * 5. Verifica que la estadística exista para ese jugador y partido
     * 6. Verifica que los valores numéricos no sean negativos (si se proporcionan)
     * 7. Verifica que las tarjetas amarillas no superen el máximo (si se proporciona)
     * 8. Verifica que al menos un valor sea proporcionado para modificar
     *
     * @param jugadorId ID del jugador
     * @param partidoId ID del partido
     * @param goles Nuevo número de goles (null si no se desea cambiar)
     * @param asistencias Nuevo número de asistencias (null si no se desea cambiar)
     * @param tarjetasAmarillas Nuevo número de tarjetas amarillas (null si no se desea cambiar)
     * @param tarjetaRoja Nueva tarjeta roja (null si no se desea cambiar)
     * @param minMinutosJugados Nuevos minutos jugados (null si no se desea cambiar)
     * @param golesRecibidos Nuevos goles recibidos (null si no se desea cambiar)
     * @param puntosJornada Nuevos puntos de jornada (null si no se desea cambiar)
     * @return Estadística modificada y guardada
     * @throws EstadisticaException Si alguna validación falla
     */
    public EstadisticaJugadorPartido modificarEstadistica(Long jugadorId, Long partidoId,
                                                          Integer goles, Integer asistencias,
                                                          Integer tarjetasAmarillas, Boolean tarjetaRoja,
                                                          Boolean minMinutosJugados, Integer golesRecibidos,
                                                          Integer puntosJornada) {
        // Validación 1: Verificar que los IDs no sean nulos
        if (jugadorId == null) {
            throw new EstadisticaException("El ID del jugador no puede ser nulo");
        }

        if (partidoId == null) {
            throw new EstadisticaException("El ID del partido no puede ser nulo");
        }

        // Validación 2: Verificar que los IDs sean valores positivos
        if (jugadorId <= 0) {
            throw new EstadisticaException("El ID del jugador debe ser un valor positivo: " + jugadorId);
        }

        if (partidoId <= 0) {
            throw new EstadisticaException("El ID del partido debe ser un valor positivo: " + partidoId);
        }

        // Validación 3: Verificar que el jugador exista
        Jugador jugador = jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new EstadisticaException(
                    "No existe ningún jugador con ID: " + jugadorId
                ));

        // Validación 4: Verificar que el partido exista
        partidoRepository.findById(partidoId)
                .orElseThrow(() -> new EstadisticaException(
                    "No existe ningún partido con ID: " + partidoId
                ));

        // Validación 5: Verificar que la estadística exista
        EstadisticaJugadorPartido estadistica = estadisticaRepository
                .findByJugadorIdJugadorAndPartidoIdPartido(jugadorId, partidoId);

        if (estadistica == null) {
            throw new EstadisticaException(
                "No existe una estadística para el jugador '" + jugador.getNombreJugador() +
                "' (ID: " + jugadorId + ") en el partido ID: " + partidoId +
                ". Use el método añadirEstadistica() para crear una nueva."
            );
        }

        // Variable para rastrear si se realizó algún cambio
        boolean cambiosRealizados = false;

        // Validación 6: Si se proporciona goles, verificar que no sea negativo y actualizar
        if (goles != null) {
            if (goles < 0) {
                throw new EstadisticaException("El número de goles no puede ser negativo: " + goles);
            }
            estadistica.setGolesAnotados(goles);
            cambiosRealizados = true;
        }

        // Si se proporciona asistencias, verificar y actualizar
        if (asistencias != null) {
            if (asistencias < 0) {
                throw new EstadisticaException("El número de asistencias no puede ser negativo: " + asistencias);
            }
            estadistica.setAsistencias(asistencias);
            cambiosRealizados = true;
        }

        // Validación 7: Si se proporciona tarjetas amarillas, verificar límite y actualizar
        if (tarjetasAmarillas != null) {
            if (tarjetasAmarillas < 0) {
                throw new EstadisticaException("El número de tarjetas amarillas no puede ser negativo: " + tarjetasAmarillas);
            }
            if (tarjetasAmarillas > 2) {
                throw new EstadisticaException(
                    "Un jugador no puede recibir más de 2 tarjetas amarillas en un partido. Valor recibido: " + tarjetasAmarillas
                );
            }
            estadistica.setTarjetaAmarillas(tarjetasAmarillas);
            cambiosRealizados = true;
        }

        // Si se proporciona tarjeta roja, actualizar
        if (tarjetaRoja != null) {
            estadistica.setTarjetaRojas(tarjetaRoja);
            cambiosRealizados = true;
        }

        // Si se proporciona minutos jugados, actualizar
        if (minMinutosJugados != null) {
            estadistica.setMinMinutosJugados(minMinutosJugados);
            cambiosRealizados = true;
        }

        // Si se proporciona goles recibidos, verificar y actualizar
        if (golesRecibidos != null) {
            if (golesRecibidos < 0) {
                throw new EstadisticaException("El número de goles recibidos no puede ser negativo: " + golesRecibidos);
            }
            estadistica.setGolesRecibidos(golesRecibidos);
            cambiosRealizados = true;
        }

        // Si se proporciona puntos de jornada, actualizar
        if (puntosJornada != null) {
            estadistica.setPuntosJornada(puntosJornada);
            cambiosRealizados = true;
        }

        // Validación 8: Verificar que se haya realizado al menos un cambio
        if (!cambiosRealizados) {
            throw new EstadisticaException(
                "Debe proporcionar al menos un valor válido para modificar. " +
                "Todos los parámetros recibidos son null."
            );
        }

        // Guardar y retornar la estadística modificada
        return estadisticaRepository.save(estadistica);
    }

    /**
     * Funcionalidad 7.1: Consultar estadísticas de un jugador
     *
     * Este método recupera todas las estadísticas de un jugador a lo largo
     * de todos los partidos en los que ha participado. Útil para:
     * - Ver el historial de rendimiento del jugador
     * - Calcular estadísticas totales (goles, asistencias, etc.)
     * - Generar reportes individuales del jugador
     *
     * Validaciones:
     * 1. Verifica que el ID del jugador no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que el jugador exista en la base de datos
     *
     * @param jugadorId ID del jugador
     * @return Lista de estadísticas del jugador (puede estar vacía si no ha jugado partidos)
     * @throws EstadisticaException Si alguna validación falla
     */
    public List<EstadisticaJugadorPartido> obtenerEstadisticasJugador(Long jugadorId) {
        // Validación 1: Verificar que el ID no sea nulo
        if (jugadorId == null) {
            throw new EstadisticaException("El ID del jugador no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (jugadorId <= 0) {
            throw new EstadisticaException("El ID del jugador debe ser un valor positivo: " + jugadorId);
        }

        // Validación 3: Verificar que el jugador exista
        jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new EstadisticaException(
                    "No existe ningún jugador con ID: " + jugadorId
                ));

        // Buscar y retornar todas las estadísticas del jugador
        return estadisticaRepository.findByJugadorIdJugador(jugadorId);
    }

    /**
     * Obtiene las estadísticas de un partido específico
     *
     * Este método recupera las estadísticas de todos los jugadores que participaron
     * en un partido específico. Útil para:
     * - Ver el rendimiento de todos los jugadores en un partido
     * - Generar reportes del partido
     * - Calcular estadísticas del partido
     *
     * Validaciones:
     * 1. Verifica que el ID del partido no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que el partido exista en la base de datos
     *
     * @param partidoId ID del partido
     * @return Lista de estadísticas del partido (puede estar vacía si no hay estadísticas registradas)
     * @throws EstadisticaException Si alguna validación falla
     */
    public List<EstadisticaJugadorPartido> obtenerEstadisticasPartido(Long partidoId) {
        // Validación 1: Verificar que el ID no sea nulo
        if (partidoId == null) {
            throw new EstadisticaException("El ID del partido no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (partidoId <= 0) {
            throw new EstadisticaException("El ID del partido debe ser un valor positivo: " + partidoId);
        }

        // Validación 3: Verificar que el partido exista
        partidoRepository.findById(partidoId)
                .orElseThrow(() -> new EstadisticaException(
                    "No existe ningún partido con ID: " + partidoId
                ));

        // Buscar y retornar todas las estadísticas del partido
        return estadisticaRepository.findByPartidoIdPartido(partidoId);
    }

    /**
     * Obtiene una estadística específica de un jugador en un partido
     *
     * Este método recupera la estadística exacta de un jugador en un partido determinado.
     * Es útil para consultas específicas y operaciones de modificación o eliminación.
     *
     * Validaciones:
     * 1. Verifica que los IDs no sean nulos
     * 2. Verifica que los IDs sean valores positivos
     * 3. Verifica que el jugador exista en la base de datos
     * 4. Verifica que el partido exista en la base de datos
     * 5. Verifica que exista la estadística para ese jugador y partido
     *
     * @param jugadorId ID del jugador
     * @param partidoId ID del partido
     * @return Estadística encontrada
     * @throws EstadisticaException Si alguna validación falla o no existe la estadística
     */
    public EstadisticaJugadorPartido obtenerEstadistica(Long jugadorId, Long partidoId) {
        // Validación 1: Verificar que los IDs no sean nulos
        if (jugadorId == null) {
            throw new EstadisticaException("El ID del jugador no puede ser nulo");
        }

        if (partidoId == null) {
            throw new EstadisticaException("El ID del partido no puede ser nulo");
        }

        // Validación 2: Verificar que los IDs sean valores positivos
        if (jugadorId <= 0) {
            throw new EstadisticaException("El ID del jugador debe ser un valor positivo: " + jugadorId);
        }

        if (partidoId <= 0) {
            throw new EstadisticaException("El ID del partido debe ser un valor positivo: " + partidoId);
        }

        // Validación 3: Verificar que el jugador exista
        Jugador jugador = jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new EstadisticaException(
                    "No existe ningún jugador con ID: " + jugadorId
                ));

        // Validación 4: Verificar que el partido exista
        partidoRepository.findById(partidoId)
                .orElseThrow(() -> new EstadisticaException(
                    "No existe ningún partido con ID: " + partidoId
                ));

        // Validación 5: Buscar la estadística y verificar que exista
        EstadisticaJugadorPartido estadistica = estadisticaRepository
                .findByJugadorIdJugadorAndPartidoIdPartido(jugadorId, partidoId);

        if (estadistica == null) {
            throw new EstadisticaException(
                "No existe una estadística para el jugador '" + jugador.getNombreJugador() +
                "' (ID: " + jugadorId + ") en el partido ID: " + partidoId
            );
        }

        return estadistica;
    }

    /**
     * Obtiene estadísticas de jugadores en una jornada específica
     *
     * Este método recupera todas las estadísticas de todos los jugadores que participaron
     * en los partidos de una jornada específica. Útil para:
     * - Generar reportes de la jornada
     * - Calcular puntos de alineaciones
     * - Ver el rendimiento general de la jornada
     *
     * Validaciones:
     * 1. Verifica que el ID de la jornada no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que la jornada exista en la base de datos
     *
     * @param jornadaId ID de la jornada
     * @return Lista de estadísticas de la jornada (puede estar vacía)
     * @throws EstadisticaException Si alguna validación falla
     */
    public List<EstadisticaJugadorPartido> obtenerEstadisticasJornada(Long jornadaId) {
        // Validación 1: Verificar que el ID no sea nulo
        if (jornadaId == null) {
            throw new EstadisticaException("El ID de la jornada no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (jornadaId <= 0) {
            throw new EstadisticaException("El ID de la jornada debe ser un valor positivo: " + jornadaId);
        }

        // Validación 3: Verificar que la jornada exista
        jornadaRepository.findById(jornadaId)
                .orElseThrow(() -> new EstadisticaException(
                    "No existe ninguna jornada con ID: " + jornadaId
                ));

        // Buscar y retornar todas las estadísticas de la jornada
        return estadisticaRepository.findByPartidoJornadaIdJornada(jornadaId);
    }

    /**
     * Elimina una estadística específica
     *
     * Este método elimina la estadística de un jugador en un partido específico.
     * Útil para corregir errores de registro o eliminar datos incorrectos.
     *
     * Validaciones:
     * 1. Verifica que los IDs no sean nulos
     * 2. Verifica que los IDs sean valores positivos
     * 3. Verifica que la estadística exista (usa obtenerEstadistica con sus validaciones)
     *
     * @param jugadorId ID del jugador
     * @param partidoId ID del partido
     * @throws EstadisticaException Si alguna validación falla o no existe la estadística
     */
    public void eliminarEstadistica(Long jugadorId, Long partidoId) {
        // Validación 1: Verificar que los IDs no sean nulos
        if (jugadorId == null) {
            throw new EstadisticaException("El ID del jugador no puede ser nulo");
        }

        if (partidoId == null) {
            throw new EstadisticaException("El ID del partido no puede ser nulo");
        }

        // Validación 2: Verificar que los IDs sean valores positivos
        if (jugadorId <= 0) {
            throw new EstadisticaException("El ID del jugador debe ser un valor positivo: " + jugadorId);
        }

        if (partidoId <= 0) {
            throw new EstadisticaException("El ID del partido debe ser un valor positivo: " + partidoId);
        }

        // Validación 3: Obtener la estadística (incluye validación de existencia)
        EstadisticaJugadorPartido estadistica = obtenerEstadistica(jugadorId, partidoId);

        // Si todas las validaciones pasan, eliminar la estadística
        estadisticaRepository.delete(estadistica);
    }

    /**
     * Lista todas las estadísticas del sistema
     *
     * Este método retorna todas las estadísticas existentes en la base de datos,
     * independientemente del jugador, partido o jornada. Es útil para:
     * - Administración y supervisión del sistema
     * - Estadísticas globales de la liga
     * - Reportes generales
     * - Análisis de datos masivos
     *
     * Nota: Este método puede retornar grandes cantidades de datos si hay muchos partidos.
     * Para consultas específicas, es preferible usar:
     * - obtenerEstadisticasJugador() para estadísticas de un jugador
     * - obtenerEstadisticasPartido() para estadísticas de un partido
     * - obtenerEstadisticasJornada() para estadísticas de una jornada
     *
     * @return Lista con todas las estadísticas del sistema (puede estar vacía)
     */
    public List<EstadisticaJugadorPartido> listarTodasLasEstadisticas() {
        // Obtener todas las estadísticas de la base de datos
        return estadisticaRepository.findAll();
    }

    /**
     * Resetea todas las estadísticas de los jugadores y los resultados de los partidos
     * pertenecientes a una liga concreta. No elimina equipos ni jugadores.
     *
     * Proceso:
     * - Obtener todas las jornadas de la liga
     * - Para cada jornada obtener sus partidos
     * - Eliminar todas las estadísticas asociadas a cada partido
     * - Poner los goles de cada partido a 0 (resultado reseteado)
     *
     * @param ligaId ID de la liga
     */
    public void resetEstadisticasYResultadosDeLiga(Long ligaId) {
        if (ligaId == null) {
            throw new EstadisticaException("El ID de la liga no puede ser nulo");
        }

        // Obtener todas las jornadas de la liga
        List<com.example.Liga_Del_Cume.data.model.Jornada> jornadas = jornadaRepository.findByLigaIdLigaCume(ligaId);

        for (com.example.Liga_Del_Cume.data.model.Jornada jornada : jornadas) {
            List<Partido> partidos = partidoRepository.findByJornadaIdJornada(jornada.getIdJornada());
            for (Partido partido : partidos) {
                // Eliminar estadísticas asociadas al partido
                List<EstadisticaJugadorPartido> stats = estadisticaRepository.findByPartidoIdPartido(partido.getIdPartido());
                if (stats != null && !stats.isEmpty()) {
                    estadisticaRepository.deleteAll(stats);
                }

                // Resetear resultado del partido a 0-0
                partido.setGolesLocal(0);
                partido.setGolesVisitante(0);
                partidoRepository.save(partido);
            }
        }

        // Resetear puntos acumulados de todos los usuarios de la liga a 0
        List<com.example.Liga_Del_Cume.data.model.Usuario> usuarios = usuarioRepository.findByLigaIdLigaCume(ligaId);
        for (com.example.Liga_Del_Cume.data.model.Usuario usuario : usuarios) {
            usuario.setPuntosAcumulados(0);
            usuarioRepository.save(usuario);
        }
    }
}
