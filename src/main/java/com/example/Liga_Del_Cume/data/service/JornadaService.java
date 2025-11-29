package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.Jornada;
import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.model.Partido;
import com.example.Liga_Del_Cume.data.repository.JornadaRepository;
import com.example.Liga_Del_Cume.data.repository.LigaCumeRepository;
import com.example.Liga_Del_Cume.data.exceptions.JornadaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar jornadas de una liga
 *
 * Funcionalidades principales:
 * - 3.1: Crear jornadas de una liga
 * - 3.2: Asignar partidos a jornadas
 * - 3.3: Consultar jornadas de una liga
 *
 * Una jornada representa una fecha o ronda de partidos dentro de una liga.
 * Cada jornada contiene múltiples partidos que se juegan en ese período.
 *
 * Este servicio maneja toda la lógica de negocio relacionada con las jornadas,
 * incluyendo validaciones exhaustivas para garantizar la integridad de los datos.
 */
@Service
@Transactional
public class JornadaService {

    @Autowired
    private JornadaRepository jornadaRepository;

    @Autowired
    private LigaCumeRepository ligaCumeRepository;

    /**
     * Funcionalidad 3.1: Crea una nueva jornada para una liga
     *
     * Este método crea una nueva jornada vacía asociada a una liga específica.
     * Los partidos se asignarán posteriormente a través de otros métodos.
     *
     * Validaciones:
     * 1. Verifica que la liga no sea nula
     * 2. Verifica que la liga tenga un ID válido
     * 3. Verifica que la liga exista en la base de datos
     *
     * @param liga Liga a la que pertenecerá la jornada
     * @return Jornada creada y guardada en la base de datos
     * @throws JornadaException Si alguna validación falla
     */
    public Jornada crearJornada(LigaCume liga) {
        // Validación 1: Verificar que la liga no sea nula
        if (liga == null) {
            throw new JornadaException("La liga no puede ser nula. La jornada debe pertenecer a una liga.");
        }

        // Validación 2: Verificar que la liga tenga un ID válido
        if (liga.getIdLigaCume() == null) {
            throw new JornadaException("La liga debe tener un ID válido. Asegúrese de que la liga existe en la base de datos.");
        }

        if (liga.getIdLigaCume() <= 0) {
            throw new JornadaException("El ID de la liga debe ser un valor positivo: " + liga.getIdLigaCume());
        }

        // Validación 3: Verificar que la liga exista en la base de datos
        LigaCume ligaExistente = ligaCumeRepository.findById(liga.getIdLigaCume())
                .orElseThrow(() -> new JornadaException(
                    "No existe ninguna liga con ID: " + liga.getIdLigaCume()
                ));

        // Si todas las validaciones pasan, crear la jornada
        Jornada jornada = new Jornada();
        jornada.setLiga(ligaExistente);

        // Calcular automáticamente el número de jornada
        List<Jornada> jornadasExistentes = jornadaRepository.findByLigaIdLigaCume(liga.getIdLigaCume());
        int numeroJornada = jornadasExistentes.size() + 1;
        jornada.setNumeroJornada(numeroJornada);

        // Guardar y retornar la jornada creada
        return jornadaRepository.save(jornada);
    }

    /**
     * Obtiene una jornada por su ID
     *
     * Este método recupera una jornada específica usando su identificador único.
     * Es un método básico de consulta útil para operaciones individuales.
     *
     * Validaciones:
     * 1. Verifica que el ID no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que la jornada exista en la base de datos
     *
     * @param id ID de la jornada que se desea obtener
     * @return Jornada encontrada con todos sus datos (partidos, alineaciones, etc.)
     * @throws JornadaException Si alguna validación falla
     */
    public Jornada obtenerJornada(Long id) {
        // Validación 1: Verificar que el ID no sea nulo
        if (id == null) {
            throw new JornadaException("El ID de la jornada no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (id <= 0) {
            throw new JornadaException("El ID de la jornada debe ser un valor positivo: " + id);
        }

        // Validación 3: Buscar y verificar que la jornada exista
        return jornadaRepository.findById(id)
                .orElseThrow(() -> new JornadaException(
                    "No existe ninguna jornada con ID: " + id
                ));
    }

    /**
     * Funcionalidad 3.3: Lista todas las jornadas de una liga
     *
     * Este método recupera todas las jornadas asociadas a una liga específica,
     * ordenadas por su ID (generalmente cronológicamente). Es útil para:
     * - Mostrar el calendario completo de la liga
     * - Navegar entre jornadas
     * - Generar estadísticas de la liga
     * - Ver el historial de jornadas
     *
     * Validaciones:
     * 1. Verifica que el ID de la liga no sea nulo
     * 2. Verifica que el ID de la liga sea un valor positivo
     * 3. Verifica que la liga exista en la base de datos
     *
     * @param ligaId ID de la liga de la cual se quieren listar las jornadas
     * @return Lista de jornadas de la liga ordenadas por ID (puede estar vacía si no hay jornadas)
     * @throws JornadaException Si alguna validación falla
     */
    public List<Jornada> listarJornadasPorLiga(Long ligaId) {
        // Validación 1: Verificar que el ID de la liga no sea nulo
        if (ligaId == null) {
            throw new JornadaException("El ID de la liga no puede ser nulo");
        }

        // Validación 2: Verificar que el ID de la liga sea un valor positivo
        if (ligaId <= 0) {
            throw new JornadaException(
                "El ID de la liga debe ser un valor positivo: " + ligaId
            );
        }

        // Validación 3: Verificar que la liga exista
        LigaCume liga = ligaCumeRepository.findById(ligaId)
                .orElseThrow(() -> new JornadaException(
                    "No existe ninguna liga con ID: " + ligaId
                ));

        // Buscar y retornar todas las jornadas de la liga ordenadas
        return jornadaRepository.findByLigaIdLigaCumeOrderByIdJornadaAsc(ligaId);
    }

    /**
     * Elimina una jornada del sistema
     *
     * Este método elimina una jornada específica. Es importante tener en cuenta que:
     * - Si la jornada tiene partidos asociados, estos pueden eliminarse en cascada
     *   dependiendo de la configuración de la entidad
     * - Si hay alineaciones asociadas, también pueden verse afectadas
     * - Se recomienda solo eliminar jornadas que no han sido jugadas aún
     *
     * Validaciones:
     * 1. Verifica que el ID no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que la jornada exista en la base de datos
     * 4. Verifica si la jornada tiene partidos o alineaciones (advertencia)
     *
     * @param id ID de la jornada a eliminar
     * @throws JornadaException Si alguna validación falla
     */
    public void eliminarJornada(Long id) {
        // Validación 1: Verificar que el ID no sea nulo
        if (id == null) {
            throw new JornadaException("El ID de la jornada no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (id <= 0) {
            throw new JornadaException("El ID de la jornada debe ser un valor positivo: " + id);
        }

        // Validación 3: Verificar que la jornada exista antes de intentar eliminarla
        Jornada jornada = jornadaRepository.findById(id)
                .orElseThrow(() -> new JornadaException(
                    "No existe ninguna jornada con ID: " + id + ". No se puede eliminar."
                ));

        // Validación 4 (advertencia): Verificar si la jornada tiene partidos asociados
        if (jornada.getPartidos() != null && !jornada.getPartidos().isEmpty()) {
            int cantidadPartidos = jornada.getPartidos().size();
            // Nota: Dependiendo de la configuración de cascada, los partidos
            // pueden eliminarse automáticamente o puede generarse un error.
            // Se podría lanzar una excepción aquí si no se desea permitir
            // la eliminación de jornadas con partidos.

            // Verificar si algún partido tiene estadísticas (jornada jugada)
            boolean tieneEstadisticas = jornada.getPartidos().stream()
                    .anyMatch(partido -> partido.getEstadisticas() != null &&
                                       !partido.getEstadisticas().isEmpty());

            if (tieneEstadisticas) {
                throw new JornadaException(
                    "No se puede eliminar la jornada ID: " + id +
                    " porque tiene partidos con estadísticas registradas. " +
                    "Eliminar esta jornada afectaría el historial de la liga."
                );
            }
        }

        // Validación 5 (advertencia): Verificar si la jornada tiene alineaciones asociadas
        if (jornada.getAlineaciones() != null && !jornada.getAlineaciones().isEmpty()) {
            throw new JornadaException(
                "No se puede eliminar la jornada ID: " + id +
                " porque tiene " + jornada.getAlineaciones().size() +
                " alineaciones asociadas. Elimine primero las alineaciones."
            );
        }

        // Si todas las validaciones pasan, eliminar la jornada
        jornadaRepository.deleteById(id);
    }

    /**
     * Lista todas las jornadas del sistema
     *
     * Este método retorna todas las jornadas existentes en la base de datos,
     * independientemente de la liga a la que pertenezcan. Es útil para:
     * - Administración y supervisión del sistema
     * - Estadísticas globales de todas las ligas
     * - Reportes generales del sistema
     * - Análisis de datos masivos
     *
     * Nota: Este método puede retornar grandes cantidades de datos si hay muchas ligas.
     * Para consultas específicas de una liga, es preferible usar listarJornadasPorLiga().
     *
     * @return Lista con todas las jornadas del sistema (puede estar vacía)
     */
    public List<Jornada> listarTodasLasJornadas() {
        // Obtener todas las jornadas de la base de datos
        return jornadaRepository.findAll();
    }

    /**
     * Verifica si una jornada tiene partidos asignados
     *
     * Este método auxiliar verifica si una jornada ya tiene partidos asociados.
     * Es útil para validaciones y verificar el estado de una jornada.
     *
     * Validaciones:
     * 1. Verifica que el ID no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que la jornada exista
     *
     * @param jornadaId ID de la jornada a verificar
     * @return true si la jornada tiene al menos un partido, false en caso contrario
     * @throws JornadaException Si alguna validación falla
     */
    public boolean tienePartidos(Long jornadaId) {
        // Validación 1: Verificar que el ID no sea nulo
        if (jornadaId == null) {
            throw new JornadaException("El ID de la jornada no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (jornadaId <= 0) {
            throw new JornadaException("El ID de la jornada debe ser un valor positivo: " + jornadaId);
        }

        // Validación 3: Obtener la jornada (valida existencia)
        Jornada jornada = obtenerJornada(jornadaId);

        // Verificar si tiene partidos
        return jornada.getPartidos() != null && !jornada.getPartidos().isEmpty();
    }

    /**
     * Obtiene el número de partidos de una jornada
     *
     * Este método retorna la cantidad de partidos asociados a una jornada específica.
     * Es útil para estadísticas y validaciones.
     *
     * Validaciones:
     * 1. Verifica que el ID no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que la jornada exista
     *
     * @param jornadaId ID de la jornada
     * @return Número de partidos de la jornada (0 si no tiene partidos)
     * @throws JornadaException Si alguna validación falla
     */
    public int contarPartidos(Long jornadaId) {
        // Validación 1: Verificar que el ID no sea nulo
        if (jornadaId == null) {
            throw new JornadaException("El ID de la jornada no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (jornadaId <= 0) {
            throw new JornadaException("El ID de la jornada debe ser un valor positivo: " + jornadaId);
        }

        // Validación 3: Obtener la jornada (valida existencia)
        Jornada jornada = obtenerJornada(jornadaId);

        // Contar y retornar el número de partidos
        if (jornada.getPartidos() == null) {
            return 0;
        }

        return jornada.getPartidos().size();
    }

    /**
     * Verifica si una jornada ha sido completada (todos sus partidos jugados)
     *
     * Este método verifica si una jornada está completada revisando si todos
     * sus partidos tienen estadísticas registradas. Una jornada se considera
     * completada cuando todos sus partidos han sido jugados y evaluados.
     *
     * Validaciones:
     * 1. Verifica que el ID no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que la jornada exista
     * 4. Verifica que la jornada tenga partidos asignados
     *
     * @param jornadaId ID de la jornada a verificar
     * @return true si todos los partidos tienen estadísticas, false en caso contrario
     * @throws JornadaException Si alguna validación falla
     */
    public boolean esJornadaCompletada(Long jornadaId) {
        // Validación 1: Verificar que el ID no sea nulo
        if (jornadaId == null) {
            throw new JornadaException("El ID de la jornada no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (jornadaId <= 0) {
            throw new JornadaException("El ID de la jornada debe ser un valor positivo: " + jornadaId);
        }

        // Validación 3: Obtener la jornada (valida existencia)
        Jornada jornada = obtenerJornada(jornadaId);

        // Validación 4: Verificar que la jornada tenga partidos
        if (jornada.getPartidos() == null || jornada.getPartidos().isEmpty()) {
            // Una jornada sin partidos no está completada
            return false;
        }

        // Verificar si todos los partidos tienen estadísticas registradas
        for (Partido partido : jornada.getPartidos()) {
            if (partido.getEstadisticas() == null || partido.getEstadisticas().isEmpty()) {
                // Si al menos un partido no tiene estadísticas, la jornada no está completada
                return false;
            }
        }

        // Si todos los partidos tienen estadísticas, la jornada está completada
        return true;
    }

    /**
     * Obtiene el número de jornadas de una liga
     *
     * Este método retorna la cantidad total de jornadas asociadas a una liga.
     * Es útil para mostrar información de la liga y generar estadísticas.
     *
     * Validaciones:
     * 1. Verifica que el ID de la liga no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que la liga exista
     *
     * @param ligaId ID de la liga
     * @return Número de jornadas de la liga (0 si no tiene jornadas)
     * @throws JornadaException Si alguna validación falla
     */
    public int contarJornadasPorLiga(Long ligaId) {
        // Validación 1: Verificar que el ID no sea nulo
        if (ligaId == null) {
            throw new JornadaException("El ID de la liga no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (ligaId <= 0) {
            throw new JornadaException("El ID de la liga debe ser un valor positivo: " + ligaId);
        }

        // Validación 3: Verificar que la liga exista
        ligaCumeRepository.findById(ligaId)
                .orElseThrow(() -> new JornadaException(
                    "No existe ninguna liga con ID: " + ligaId
                ));

        // Contar y retornar las jornadas de la liga
        List<Jornada> jornadas = jornadaRepository.findByLigaIdLigaCume(ligaId);
        return jornadas.size();
    }
}

