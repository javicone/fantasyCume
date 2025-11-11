package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.Equipo;
import com.example.Liga_Del_Cume.data.model.Jornada;
import com.example.Liga_Del_Cume.data.model.Partido;
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

    /**
     * Funcionalidad 3.1: Agregar resultado de un partido en una jornada
     */
    public Partido agregarPartido(Equipo equipoLocal, Equipo equipoVisitante,
                                   int golesLocal, int golesVisitante, Jornada jornada) {
        Partido partido = new Partido(equipoLocal, equipoVisitante, golesLocal, golesVisitante, jornada);
        return partidoRepository.save(partido);
    }

    /**
     * Funcionalidad 3.2: Modificar resultado de un partido
     */
    public Partido modificarResultado(Long partidoId, Integer nuevosGolesLocal, Integer nuevosGolesVisitante) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado con ID: " + partidoId));

        if (nuevosGolesLocal != null) {
            partido.setGolesLocal(nuevosGolesLocal);
        }
        if (nuevosGolesVisitante != null) {
            partido.setGolesVisitante(nuevosGolesVisitante);
        }

        return partidoRepository.save(partido);
    }

    /**
     * Funcionalidad 5.1: Generar/Obtener cuadro de enfrentamientos por jornada
     */
    public List<Partido> obtenerPartidosPorJornada(Long jornadaId) {
        return partidoRepository.findByJornadaIdJornada(jornadaId);
    }

    /**
     * Funcionalidad 9: Ver resultados de partidos disputados
     */
    public List<Partido> verResultadosJornada(Long jornadaId) {
        return partidoRepository.findByJornadaIdJornada(jornadaId);
    }

    /**
     * Obtiene un partido por su ID
     */
    public Partido obtenerPartido(Long id) {
        return partidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado con ID: " + id));
    }

    /**
     * Lista todos los partidos
     */
    public List<Partido> listarTodosLosPartidos() {
        return partidoRepository.findAll();
    }

    /**
     * Obtiene partidos de un equipo (como local o visitante)
     */
    public List<Partido> obtenerPartidosDeEquipo(Long equipoId) {
        return partidoRepository.findByEquipoLocalIdEquipoOrEquipoVisitanteIdEquipo(equipoId, equipoId);
    }

    /**
     * Elimina un partido
     */
    public void eliminarPartido(Long id) {
        partidoRepository.deleteById(id);
    }
}

