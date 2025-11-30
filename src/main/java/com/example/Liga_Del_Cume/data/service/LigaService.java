package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.repository.LigaCumeRepository;
import com.example.Liga_Del_Cume.data.exceptions.LigaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable_;
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
     * Crea una nueva liga con nombre y presupuesto máximo.
     * @param nombre Nombre de la liga.
     * @param presupuesto Presupuesto máximo permitido para la liga.
     * @return La liga creada y persistida en la base de datos.
     * @throws LigaException si ya existe una liga con ese nombre o el presupuesto es inválido.
     */
    public LigaCume crearLiga(String nombre, Long presupuesto) {
        if(ligaCumeRepository.findByNombreLigaCume(nombre) != null) {
            throw new LigaException("Ya existe una liga con el nombre: " + nombre);
        }
        if(presupuesto == null) {
            throw new LigaException("El presupuesto máximo tiene que ser no nulo o mayor a 0");
        }
        LigaCume liga = new LigaCume();
        liga.setNombreLiga(nombre);

        if( presupuesto < 1000000)
        {
            liga.setPresupuestoMaximo(1000000L);
        }
        else {
            liga.setPresupuestoMaximo(presupuesto);
        }
        return ligaCumeRepository.save(liga);
    }


    /**
     * Obtiene una liga por su nombre.
     * @param nombre Nombre de la liga a buscar.
     * @return La liga encontrada.
     * @throws LigaException si no se encuentra la liga.
     */
    public LigaCume obtenerLiga(String nombre) {
        LigaCume liga = ligaCumeRepository.findByNombreLigaCume(nombre);
        if (liga == null) {
            throw new LigaException("Liga no encontrada con nombre: " + nombre);
        }
        return liga;
    }

    /**
     * Obtiene una liga por su ID.
     * @param id ID de la liga a buscar.
     * @return La liga encontrada o null si no existe.
     */
    public LigaCume obtenerLigaPorId(Long id) {
        return ligaCumeRepository.findById(id).orElse(null);
    }

    /**
     * Lista todas las ligas disponibles en la base de datos.
     * @return Lista de todas las ligas.
     */
    public List<LigaCume> listarTodasLasLigas() {
        return ligaCumeRepository.findAll();
    }

    /**
     * Modifica los datos de una liga existente.
     * @param id Identificador de la liga a modificar.
     * @param nuevoNombre Nuevo nombre para la liga.
     * @param nuevoPresupuesto Nuevo presupuesto máximo para la liga.
     * @return La liga modificada y persistida.
     * @throws LigaException si la liga no existe o los datos son nulos.
     */
    public LigaCume modificarLiga(Long id, String nuevoNombre, Long nuevoPresupuesto) {
        LigaCume liga = ligaCumeRepository.findById(id)
                .orElseThrow(() -> new LigaException("Liga no encontrada con ID: " + id));
        if (nuevoNombre != null && nuevoPresupuesto != null) {
            liga.setNombreLiga(nuevoNombre);
            liga.setPresupuestoMaximo(nuevoPresupuesto);
        }
        else{
            throw new LigaException("Nombre de liga o presupuesto nulo");
        }
        return ligaCumeRepository.save(liga);
    }

    /**
     * Elimina una liga por su ID.
     * @param id Identificador de la liga a eliminar.
     */
    public void eliminarLiga(Long id) {
        ligaCumeRepository.deleteById(id);
    }

    /**
     * Da de alta una nueva liga validando los datos de entrada.
     *
     * @param nombre Nombre de la liga
     * @param presupuestoMaximo Presupuesto máximo de la liga
     * @return Liga creada
     * @throws LigaException Si los datos no son válidos
     */
    public LigaCume darDeAltaLiga(String nombre, Double presupuestoMaximo) {
        // Validación: nombre no nulo y no vacío
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new LigaException("El nombre de la liga no puede ser nulo o vacío");
        }

        String nombreLimpio = nombre.trim();

        // Validación: nombre único
        if (ligaCumeRepository.findByNombreLigaCume(nombreLimpio) != null) {
            throw new LigaException("Ya existe una liga con el nombre: " + nombreLimpio);
        }

        // Validación: presupuesto no nulo y mayor a 0
        if (presupuestoMaximo == null || presupuestoMaximo <= 0) {
            throw new LigaException("El presupuesto debe ser mayor a 0");
        }

        // Crear la liga
        LigaCume liga = new LigaCume();
        liga.setNombreLiga(nombreLimpio);

        // Establecer presupuesto mínimo si es muy bajo
        if (presupuestoMaximo < 1000000) {
            liga.setPresupuestoMaximo(1000000L);
        } else {
            liga.setPresupuestoMaximo(presupuestoMaximo.longValue());
        }

        return ligaCumeRepository.save(liga);
    }

    /**
     * Busca una liga por su ID.
     *
     * @param id ID de la liga
     * @return Liga encontrada
     * @throws LigaException Si no existe la liga
     */
    public LigaCume buscarLigaPorId(Long id) {
        if (id == null) {
            throw new LigaException("El ID de la liga no puede ser nulo");
        }

        return ligaCumeRepository.findById(id)
                .orElseThrow(() -> new LigaException("No se encontró la liga con ID: " + id));
    }

    /**
     * Lista todas las ligas disponibles.
     *
     * @return Lista de todas las ligas
     */
    public List<LigaCume> listarLigas() {
        return ligaCumeRepository.findAll();
    }

    /**
     * Busca ligas por nombre (búsqueda parcial).
     *
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de ligas que coinciden con el nombre
     */
    public List<LigaCume> buscarLigasPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return listarLigas();
        }

        String nombreBusqueda = nombre.trim().toLowerCase();
        return ligaCumeRepository.findAll().stream()
                .filter(liga -> liga.getNombreLiga().toLowerCase().contains(nombreBusqueda))
                .toList();
    }

    /**
     * Actualiza los datos de una liga existente.
     *
     * @param id ID de la liga
     * @param nuevoNombre Nuevo nombre
     * @param nuevoPresupuesto Nuevo presupuesto
     * @return Liga actualizada
     * @throws LigaException Si hay errores en la validación
     */
    public LigaCume actualizarLiga(Long id, String nuevoNombre, Double nuevoPresupuesto) {
        // Buscar la liga
        LigaCume liga = buscarLigaPorId(id);

        // Validar nuevo nombre
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            String nombreLimpio = nuevoNombre.trim();

            // Verificar que no exista otra liga con ese nombre
            LigaCume ligaExistente = ligaCumeRepository.findByNombreLigaCume(nombreLimpio);
            if (ligaExistente != null && !ligaExistente.getIdLigaCume().equals(id)) {
                throw new LigaException("Ya existe otra liga con el nombre: " + nombreLimpio);
            }

            liga.setNombreLiga(nombreLimpio);
        }

        // Validar nuevo presupuesto
        if (nuevoPresupuesto != null) {
            if (nuevoPresupuesto <= 0) {
                throw new LigaException("El presupuesto debe ser mayor a 0");
            }

            if (nuevoPresupuesto < 1000000) {
                liga.setPresupuestoMaximo(1000000L);
            } else {
                liga.setPresupuestoMaximo(nuevoPresupuesto.longValue());
            }
        }

        return ligaCumeRepository.save(liga);
    }



}

