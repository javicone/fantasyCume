package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.Equipo;
import com.example.Liga_Del_Cume.data.model.Jornada;
import com.example.Liga_Del_Cume.data.model.Partido;
import com.example.Liga_Del_Cume.data.repository.EquipoRepository;
import com.example.Liga_Del_Cume.data.repository.JornadaRepository;
import com.example.Liga_Del_Cume.data.repository.PartidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar partidos
 * Funcionalidades:
 * - 3.1 y 3.2: Agregar y modificar resultados de partidos por jornada
 * - 5.1: Generar cuadro de enfrentamientos por jornada
 * - 9: Ver resultados de partidos disputados
 */
@Service
@Transactional
public class PartidoService {

    @Autowired
    private PartidoRepository partidoRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private JornadaRepository jornadaRepository;

    /**
     * Funcionalidad 3.1: Agregar resultado de un partido en una jornada
     *
     * @param equipoLocal Equipo que juega como local
     * @param equipoVisitante Equipo que juega como visitante
     * @param golesLocal Goles marcados por el equipo local
     * @param golesVisitante Goles marcados por el equipo visitante
     * @param jornada Jornada en la que se disputa el partido
     * @return Partido creado
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public Partido agregarPartido(Equipo equipoLocal, Equipo equipoVisitante,
                                  int golesLocal, int golesVisitante, Jornada jornada) {
        // Validar que los equipos no sean nulos
        if (equipoLocal == null) {
            throw new IllegalArgumentException("El equipo local no puede ser nulo");
        }
        if (equipoVisitante == null) {
            throw new IllegalArgumentException("El equipo visitante no puede ser nulo");
        }

        // Validar que sean equipos diferentes
        if (equipoLocal.getIdEquipo().equals(equipoVisitante.getIdEquipo())) {
            throw new IllegalArgumentException("Un equipo no puede jugar contra sí mismo");
        }

        // Validar que los equipos existan
        if (!equipoRepository.existsById(equipoLocal.getIdEquipo())) {
            throw new RuntimeException("El equipo local no existe en la base de datos");
        }
        if (!equipoRepository.existsById(equipoVisitante.getIdEquipo())) {
            throw new RuntimeException("El equipo visitante no existe en la base de datos");
        }

        // Validar que la jornada no sea nula y exista
        if (jornada == null) {
            throw new IllegalArgumentException("La jornada no puede ser nula");
        }
        if (!jornadaRepository.existsById(jornada.getIdJornada())) {
            throw new RuntimeException("La jornada no existe en la base de datos");
        }

        // Validar que los goles no sean negativos
        if (golesLocal < 0) {
            throw new IllegalArgumentException("Los goles del equipo local no pueden ser negativos");
        }
        if (golesVisitante < 0) {
            throw new IllegalArgumentException("Los goles del equipo visitante no pueden ser negativos");
        }

        // Validar que no exista ya un partido entre estos equipos en esta jornada
        List<Partido> partidosExistentes = partidoRepository.findByJornadaIdJornada(jornada.getIdJornada());
        for (Partido p : partidosExistentes) {
            if ((p.getEquipoLocal().getIdEquipo().equals(equipoLocal.getIdEquipo()) &&
                    p.getEquipoVisitante().getIdEquipo().equals(equipoVisitante.getIdEquipo())) ||
                    (p.getEquipoLocal().getIdEquipo().equals(equipoVisitante.getIdEquipo()) &&
                            p.getEquipoVisitante().getIdEquipo().equals(equipoLocal.getIdEquipo()))) {
                throw new IllegalArgumentException("Ya existe un partido entre estos equipos en esta jornada");
            }
        }

        Partido partido = new Partido(equipoLocal, equipoVisitante, golesLocal, golesVisitante, jornada);
        return partidoRepository.save(partido);
    }

    /**
     * Funcionalidad 3.2: Modificar resultado de un partido
     *
     * @param partidoId ID del partido a modificar
     * @param nuevosGolesLocal Nuevos goles del equipo local (null para no modificar)
     * @param nuevosGolesVisitante Nuevos goles del equipo visitante (null para no modificar)
     * @return Partido actualizado
     * @throws RuntimeException si el partido no existe
     * @throws IllegalArgumentException si los goles son negativos
     */
    public Partido modificarResultado(Long partidoId, Integer nuevosGolesLocal, Integer nuevosGolesVisitante) {
        if (partidoId == null) {
            throw new IllegalArgumentException("El ID del partido no puede ser nulo");
        }

        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado con ID: " + partidoId));

        if (nuevosGolesLocal != null) {
            if (nuevosGolesLocal < 0) {
                throw new IllegalArgumentException("Los goles del equipo local no pueden ser negativos");
            }
            partido.setGolesLocal(nuevosGolesLocal);
        }

        if (nuevosGolesVisitante != null) {
            if (nuevosGolesVisitante < 0) {
                throw new IllegalArgumentException("Los goles del equipo visitante no pueden ser negativos");
            }
            partido.setGolesVisitante(nuevosGolesVisitante);
        }

        return partidoRepository.save(partido);
    }

    /**
     * Funcionalidad 5.1: Generar/Obtener cuadro de enfrentamientos por jornada
     *
     * @param jornadaId ID de la jornada
     * @return Lista de partidos de la jornada
     * @throws IllegalArgumentException si el ID es nulo
     * @throws RuntimeException si la jornada no existe
     */
    public List<Partido> obtenerPartidosPorJornada(Long jornadaId) {
        if (jornadaId == null) {
            throw new IllegalArgumentException("El ID de la jornada no puede ser nulo");
        }

        if (!jornadaRepository.existsById(jornadaId)) {
            throw new RuntimeException("Jornada no encontrada con ID: " + jornadaId);
        }

        return partidoRepository.findByJornadaIdJornada(jornadaId);
    }

    /**
     * Funcionalidad 9: Ver resultados de partidos disputados
     *
     * @param jornadaId ID de la jornada
     * @return Lista de partidos con resultados
     * @throws IllegalArgumentException si el ID es nulo
     * @throws RuntimeException si la jornada no existe
     */
    public List<Partido> verResultadosJornada(Long jornadaId) {
        if (jornadaId == null) {
            throw new IllegalArgumentException("El ID de la jornada no puede ser nulo");
        }

        if (!jornadaRepository.existsById(jornadaId)) {
            throw new RuntimeException("Jornada no encontrada con ID: " + jornadaId);
        }

        return partidoRepository.findByJornadaIdJornada(jornadaId);
    }

    /**
     * Obtiene un partido por su ID
     *
     * @param id ID del partido
     * @return Partido encontrado
     * @throws IllegalArgumentException si el ID es nulo
     * @throws RuntimeException si el partido no existe
     */
    public Partido obtenerPartido(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del partido no puede ser nulo");
        }

        return partidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado con ID: " + id));
    }

    /**
     * Lista todos los partidos
     *
     * @return Lista de todos los partidos
     */
    public List<Partido> listarTodosLosPartidos() {
        return partidoRepository.findAll();
    }

    /**
     * Obtiene partidos de un equipo (como local o visitante)
     *
     * @param nombreEquipo ID del equipo
     * @return Lista de partidos del equipo
     * @throws IllegalArgumentException si el ID es nulo
     * @throws RuntimeException si el equipo no existe
     */
    public List<Partido> obtenerPartidosDeEquipo(String nombreEquipo) {
        if (nombreEquipo == null) {
            throw new IllegalArgumentException("El ID del equipo no puede ser nulo");
        }

        if (equipoRepository.findByNombreEquipo(nombreEquipo)==null) {
            throw new RuntimeException("Equipo no encontrado con ID: " + nombreEquipo);
        }

        return partidoRepository.findByEquipoLocalNombreEquipoOrEquipoVisitanteNombreEquipo(nombreEquipo, nombreEquipo);
    }

    /**
     * Elimina un partido
     *
     * @param id ID del partido a eliminar
     * @throws IllegalArgumentException si el ID es nulo
     * @throws RuntimeException si el partido no existe
     */
    public void eliminarPartido(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del partido no puede ser nulo");
        }

        if (!partidoRepository.existsById(id)) {
            throw new RuntimeException("Partido no encontrado con ID: " + id);
        }

        partidoRepository.deleteById(id);
    }
}
