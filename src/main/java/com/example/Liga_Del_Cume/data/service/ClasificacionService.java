package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.ClasificacionEquipo;
import com.example.Liga_Del_Cume.data.model.Equipo;
import com.example.Liga_Del_Cume.data.model.Partido;
import com.example.Liga_Del_Cume.data.repository.EquipoRepository;
import com.example.Liga_Del_Cume.data.repository.PartidoRepository;
import com.example.Liga_Del_Cume.data.exceptions.EquipoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar la clasificación de equipos en una liga
 *
 * Este servicio calcula la clasificación de equipos basándose en los resultados
 * de los partidos jugados. Los puntos se asignan según:
 * - Victoria: 3 puntos
 * - Empate: 1 punto
 * - Derrota: 0 puntos
 */
@Service
@Transactional
public class ClasificacionService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private PartidoRepository partidoRepository;

    /**
     * Obtiene la clasificación completa de una liga
     *
     * Este método calcula los puntos de cada equipo basándose en sus partidos
     * jugados y los ordena de mayor a menor puntuación.
     *
     * @param ligaId ID de la liga
     * @return Lista de ClasificacionEquipo ordenada por puntos (descendente)
     * @throws EquipoException Si la liga no existe o no tiene equipos
     */
    public List<ClasificacionEquipo> obtenerClasificacionLiga(Long ligaId) {
        // Validar ID de liga
        if (ligaId == null || ligaId <= 0) {
            throw new EquipoException("El ID de la liga debe ser válido");
        }

        // Obtener todos los equipos de la liga
        List<Equipo> equipos = equipoRepository.findByLigaIdLigaCume(ligaId);

        if (equipos == null || equipos.isEmpty()) {
            return new ArrayList<>(); // Retornar lista vacía si no hay equipos
        }

        // Crear mapa de clasificación para cada equipo
        Map<Long, ClasificacionEquipo> clasificacionMap = new HashMap<>();

        // Inicializar clasificación para cada equipo
        for (Equipo equipo : equipos) {
            ClasificacionEquipo clasificacion = new ClasificacionEquipo(
                equipo.getIdEquipo(),
                equipo.getNombreEquipo(),
                equipo.getEscudoURL()
            );
            clasificacionMap.put(equipo.getIdEquipo(), clasificacion);
        }

        // Obtener todos los partidos de los equipos de la liga (sin duplicados)
        Set<Long> partidosProcesados = new HashSet<>();

        for (Equipo equipo : equipos) {
            List<Partido> partidos = partidoRepository.findByEquipoLocalNombreEquipoOrEquipoVisitanteNombreEquipo(
                equipo.getNombreEquipo(),
                equipo.getNombreEquipo()
            );

            // Procesar cada partido solo una vez
            for (Partido partido : partidos) {
                if (!partidosProcesados.contains(partido.getIdPartido())) {
                    procesarPartido(partido, clasificacionMap);
                    partidosProcesados.add(partido.getIdPartido());
                }
            }
        }

        // Convertir el mapa a lista y ordenar
        List<ClasificacionEquipo> clasificacion = new ArrayList<>(clasificacionMap.values());
        Collections.sort(clasificacion);

        return clasificacion;
    }

    /**
     * Procesa un partido y actualiza las estadísticas de los equipos involucrados
     *
     * @param partido El partido a procesar
     * @param clasificacionMap Mapa de clasificaciones a actualizar
     */
    private void procesarPartido(Partido partido, Map<Long, ClasificacionEquipo> clasificacionMap) {
        if (partido == null || partido.getEquipoLocal() == null || partido.getEquipoVisitante() == null) {
            return;
        }

        Long idLocal = partido.getEquipoLocal().getIdEquipo();
        Long idVisitante = partido.getEquipoVisitante().getIdEquipo();

        ClasificacionEquipo clasificacionLocal = clasificacionMap.get(idLocal);
        ClasificacionEquipo clasificacionVisitante = clasificacionMap.get(idVisitante);

        // Si alguno de los equipos no está en el mapa, no procesar el partido
        if (clasificacionLocal == null || clasificacionVisitante == null) {
            return;
        }

        int golesLocal = partido.getGolesLocal();
        int golesVisitante = partido.getGolesVisitante();

        // Actualizar goles
        clasificacionLocal.addGoles(golesLocal, golesVisitante);
        clasificacionVisitante.addGoles(golesVisitante, golesLocal);

        // Determinar resultado y actualizar estadísticas
        if (golesLocal > golesVisitante) {
            // Victoria local, derrota visitante
            clasificacionLocal.addVictoria();
            clasificacionVisitante.addDerrota();
        } else if (golesLocal < golesVisitante) {
            // Derrota local, victoria visitante
            clasificacionLocal.addDerrota();
            clasificacionVisitante.addVictoria();
        } else {
            // Empate
            clasificacionLocal.addEmpate();
            clasificacionVisitante.addEmpate();
        }
    }

    /**
     * Obtiene la clasificación de un equipo específico
     *
     * @param equipoId ID del equipo
     * @return ClasificacionEquipo con las estadísticas del equipo
     * @throws EquipoException Si el equipo no existe
     */
    public ClasificacionEquipo obtenerClasificacionEquipo(Long equipoId) {
        if (equipoId == null || equipoId <= 0) {
            throw new EquipoException("El ID del equipo debe ser válido");
        }

        Equipo equipo = equipoRepository.findById(equipoId)
            .orElseThrow(() -> new EquipoException("No existe el equipo con ID: " + equipoId));

        ClasificacionEquipo clasificacion = new ClasificacionEquipo(
            equipo.getIdEquipo(),
            equipo.getNombreEquipo(),
            equipo.getEscudoURL()
        );

        // Obtener partidos del equipo
        List<Partido> partidos = partidoRepository.findByEquipoLocalNombreEquipoOrEquipoVisitanteNombreEquipo(
            equipo.getNombreEquipo(),
            equipo.getNombreEquipo()
        );

        // Procesar cada partido
        for (Partido partido : partidos) {
            if (partido.getEquipoLocal().getIdEquipo().equals(equipoId)) {
                // Este equipo es local
                int golesLocal = partido.getGolesLocal();
                int golesVisitante = partido.getGolesVisitante();

                clasificacion.addGoles(golesLocal, golesVisitante);

                if (golesLocal > golesVisitante) {
                    clasificacion.addVictoria();
                } else if (golesLocal < golesVisitante) {
                    clasificacion.addDerrota();
                } else {
                    clasificacion.addEmpate();
                }
            } else if (partido.getEquipoVisitante().getIdEquipo().equals(equipoId)) {
                // Este equipo es visitante
                int golesLocal = partido.getGolesLocal();
                int golesVisitante = partido.getGolesVisitante();

                clasificacion.addGoles(golesVisitante, golesLocal);

                if (golesVisitante > golesLocal) {
                    clasificacion.addVictoria();
                } else if (golesVisitante < golesLocal) {
                    clasificacion.addDerrota();
                } else {
                    clasificacion.addEmpate();
                }
            }
        }

        return clasificacion;
    }

    /**
     * Obtiene los N mejores equipos de una liga
     *
     * @param ligaId ID de la liga
     * @param cantidad Número de equipos a retornar
     * @return Lista con los mejores equipos
     */
    public List<ClasificacionEquipo> obtenerTopEquipos(Long ligaId, int cantidad) {
        List<ClasificacionEquipo> clasificacion = obtenerClasificacionLiga(ligaId);

        if (cantidad <= 0 || cantidad > clasificacion.size()) {
            return clasificacion;
        }

        return clasificacion.stream()
            .limit(cantidad)
            .collect(Collectors.toList());
    }
}

