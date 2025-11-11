package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.Jornada;
import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.repository.JornadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar jornadas de una liga
 */
@Service
@Transactional
public class JornadaService {

    @Autowired
    private JornadaRepository jornadaRepository;

    /**
     * Crea una nueva jornada para una liga
     */
    public Jornada crearJornada(LigaCume liga) {
        Jornada jornada = new Jornada();
        jornada.setLiga(liga);
        return jornadaRepository.save(jornada);
    }

    /**
     * Obtiene una jornada por su ID
     */
    public Jornada obtenerJornada(Long id) {
        return jornadaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jornada no encontrada con ID: " + id));
    }

    /**
     * Lista todas las jornadas de una liga
     */
    public List<Jornada> listarJornadasPorLiga(Long ligaId) {
        return jornadaRepository.findByLigaIdLigaCume(ligaId);
    }

    /**
     * Elimina una jornada
     */
    public void eliminarJornada(Long id) {
        jornadaRepository.deleteById(id);
    }

    /**
     * Lista todas las jornadas
     */
    public List<Jornada> listarTodasLasJornadas() {
        return jornadaRepository.findAll();
    }
}

