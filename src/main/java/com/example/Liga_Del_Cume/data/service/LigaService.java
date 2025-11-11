package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.repository.LigaCumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar las operaciones de negocio relacionadas con las ligas.
 * Funcionalidad: Crear y gestionar ligas fantasy
 */
@Service
@Transactional
public class LigaService {

    @Autowired
    private LigaCumeRepository ligaCumeRepository;

    /**
     * Crea una nueva liga con nombre y presupuesto máximo
     */
    public LigaCume crearLiga(String nombre, Long presupuesto) {
        LigaCume liga = new LigaCume();
        liga.setNombreLiga(nombre);
        liga.setPresupuestoMaximo(presupuesto);
        return ligaCumeRepository.save(liga);
    }

    /**
     * Obtiene una liga por su ID
     */
    public LigaCume obtenerLiga(Long id) {
        return ligaCumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Liga no encontrada con ID: " + id));
    }

    /**
     * Lista todas las ligas disponibles
     */
    public List<LigaCume> listarTodasLasLigas() {
        return ligaCumeRepository.findAll();
    }

    /**
     * Modifica los datos de una liga existente
     */
    public LigaCume modificarLiga(Long id, String nuevoNombre, Long nuevoPresupuesto) {
        LigaCume liga = obtenerLiga(id);
        if (nuevoNombre != null) {
            liga.setNombreLiga(nuevoNombre);
        }
        if (nuevoPresupuesto != null) {
            liga.setPresupuestoMaximo(nuevoPresupuesto);
        }
        return ligaCumeRepository.save(liga);
    }

    /**
     * Elimina una liga por su ID
     */
    public void eliminarLiga(Long id) {
        ligaCumeRepository.deleteById(id);
    }
}

