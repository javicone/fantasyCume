package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.Equipo;
import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.repository.EquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar equipos de fútbol
 * Funcionalidades:
 * - 1.1 a 1.4: Agregar, modificar, eliminar y listar equipos
 */
@Service
@Transactional
public class EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    /**
     * Funcionalidad 1.1: Agregar un nuevo equipo a la liga
     */
    public Equipo agregarEquipo(String nombre, LigaCume liga) {
        Equipo equipo = new Equipo();
        equipo.setNombreEquipo(nombre);
        equipo.setLiga(liga);
        return equipoRepository.save(equipo);
    }

    /**
     * Funcionalidad 1.2: Modificar información de un equipo
     */
    public Equipo modificarEquipo(Long id, String nuevoNombre, String nuevoEscudo) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + id));

        if (nuevoNombre != null) {
            equipo.setNombreEquipo(nuevoNombre);
        }
        if (nuevoEscudo != null) {
            equipo.setEscudoURL(nuevoEscudo);
        }

        return equipoRepository.save(equipo);
    }

    /**
     * Funcionalidad 1.3: Eliminar un equipo
     */
    public void eliminarEquipo(Long id) {
        equipoRepository.deleteById(id);
    }

    /**
     * Funcionalidad 1.4: Listar todos los equipos de una liga
     */
    public List<Equipo> listarEquiposPorLiga(Long ligaId) {
        return equipoRepository.findByLigaIdLigaCume(ligaId);
    }

    /**
     * Obtiene un equipo por su ID
     */
    public Equipo obtenerEquipo(Long id) {
        return equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + id));
    }

    /**
     * Lista todos los equipos del sistema
     */
    public List<Equipo> listarTodosLosEquipos() {
        return equipoRepository.findAll();
    }
}

