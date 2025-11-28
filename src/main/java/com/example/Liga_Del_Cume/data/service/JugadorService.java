package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.Equipo;
import com.example.Liga_Del_Cume.data.model.Jugador;
import com.example.Liga_Del_Cume.data.repository.EquipoRepository;
import com.example.Liga_Del_Cume.data.repository.JugadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar jugadores
 * Funcionalidades:
 * - 2.1 a 2.4: Agregar, actualizar, eliminar y listar jugadores
 * - 6.1: Listar jugadores disponibles por posición
 * - 7.1: Buscar jugadores (filtrar por nombre, puntos o equipo)
 */
@Service
@Transactional
public class JugadorService {

    private JugadorRepository jugadorRepository;
    private EquipoRepository equipoRepository;

    @Autowired
    public JugadorService(JugadorRepository jugadorRepository, EquipoRepository equipoRepository) {
        this.jugadorRepository = jugadorRepository;
        this.equipoRepository = equipoRepository;
    }

    /**
     * Funcionalidad 2.1: Agregar un jugador a un equipo
     *
     * @param nombre Nombre del jugador
     * @param esPortero Indica si es portero
     * @param equipo Equipo al que pertenece
     * @param precio Precio de mercado
     * @return Jugador creado
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    public Jugador agregarJugador(String nombre, boolean esPortero, Equipo equipo, float precio) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del jugador no puede estar vacío");
        }
        if (equipo == null) {
            throw new IllegalArgumentException("El equipo no puede ser nulo");
        }
        if (!equipoRepository.existsById(equipo.getIdEquipo())) {
            throw new RuntimeException("El equipo no existe en la base de datos");
        }
        if (equipo.getJugadores().size() >= 25) {
            throw new IllegalArgumentException("El equipo ya tiene el máximo de 25 jugadores");
        }
        if (jugadorRepository.findByNombreJugador(nombre) != null) {
            throw new IllegalArgumentException("Ya existe un jugador con ese nombre");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }

        Jugador jugador = new Jugador(nombre, esPortero, equipo, precio, "");
        return jugadorRepository.save(jugador);
    }

    /**
     * Funcionalidad 2.2: Actualizar información de un jugador
     *
     * @param id ID del jugador
     * @param nuevoNombre Nuevo nombre
     * @param nuevoPrecio Nuevo precio
     * @param esPortero Nueva posición
     * @return Jugador actualizado
     * @throws RuntimeException si el jugador no existe
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    public Jugador actualizarJugador(Long id, String nuevoNombre, Float nuevoPrecio, Boolean esPortero) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del jugador no puede ser nulo");
        }

        Jugador jugador = jugadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + id));

        if (nuevoNombre == null || nuevoNombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del jugador no puede estar vacío");
        }

        // Validar que no exista otro jugador con el mismo nombre
        Jugador jugadorExistente = jugadorRepository.findByNombreJugador(nuevoNombre);
        if (jugadorExistente != null && !jugadorExistente.getIdJugador().equals(id)) {
            throw new IllegalArgumentException("Ya existe otro jugador con ese nombre");
        }

        if (nuevoPrecio == null) {
            throw new IllegalArgumentException("El precio del jugador no puede ser nulo");
        }
        if (nuevoPrecio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }

        if (esPortero == null) {
            throw new IllegalArgumentException("El campo esPortero no puede ser nulo");
        }

        jugador.setNombreJugador(nuevoNombre);
        jugador.setPrecioMercado(nuevoPrecio);
        jugador.setEsPortero(esPortero);
        return jugadorRepository.save(jugador);
    }

    /**
     * Funcionalidad 2.3: Eliminar un jugador de un equipo
     *
     * @param id ID del jugador a eliminar
     * @throws IllegalArgumentException si el ID es nulo
     * @throws RuntimeException si el jugador no existe
     */
    public void eliminarJugador(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del jugador no puede ser nulo");
        }
        if (!jugadorRepository.existsById(id)) {
            throw new RuntimeException("Jugador no encontrado con ID: " + id);
        }
        jugadorRepository.deleteById(id);
    }

    /**
     * Funcionalidad 2.4: Listar todos los jugadores
     *
     * @return Lista de todos los jugadores
     */
    public List<Jugador> listarTodosLosJugadores() {
        return jugadorRepository.findAll();
    }

    /**
     * Lista jugadores de un equipo específico
     *
     * @param nombreEquipo Nombre del equipo
     * @return Lista de jugadores del equipo
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     * @throws RuntimeException si el equipo no existe
     */
    public List<Jugador> listarJugadoresPorEquipo(String nombreEquipo) {
        if (nombreEquipo == null || nombreEquipo.isEmpty()) {
            throw new IllegalArgumentException("El nombre del equipo no puede estar vacío");
        }

        Equipo equipo = equipoRepository.findByNombreEquipo(nombreEquipo);
        if (equipo == null) {
            throw new RuntimeException("No existe un equipo con ese nombre");
        }

        List<Jugador> jugadores = jugadorRepository.findByEquipoNombreEquipo(nombreEquipo);
        if (jugadores.isEmpty()) {
            throw new RuntimeException("No existen jugadores en ese equipo");
        }

        return jugadores;
    }

    /**
     * Funcionalidad 6.1: Listar jugadores disponibles por posición
     *
     * @return Lista de porteros
     */
    public List<Jugador> listarPorteros() {
        return jugadorRepository.findByEsPortero(true);
    }

    /**
     * Funcionalidad 6.1: Listar jugadores de campo
     *
     * @return Lista de jugadores de campo
     */
    public List<Jugador> listarJugadoresDeCampo() {
        return jugadorRepository.findByEsPortero(false);
    }

    /**
     * Funcionalidad 7.1: Buscar jugador por nombre (parcial)
     *
     * @param nombre Nombre a buscar
     * @return Lista de jugadores que coinciden
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    public List<Jugador> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre de búsqueda no puede estar vacío");
        }
        return jugadorRepository.findByNombreJugadorContainingIgnoreCase(nombre);
    }

    /**
     * Funcionalidad 7.1: Buscar jugadores por equipo
     *
     * @param nombreEquipo Nombre del equipo
     * @return Lista de jugadores del equipo
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     * @throws RuntimeException si el equipo no existe
     */
    public List<Jugador> buscarPorEquipo(String nombreEquipo) {
        if (nombreEquipo == null || nombreEquipo.isEmpty()) {
            throw new IllegalArgumentException("El nombre del equipo no puede estar vacío");
        }

        Equipo equipo = equipoRepository.findByNombreEquipo(nombreEquipo);
        if (equipo == null) {
            throw new RuntimeException("No existe un equipo con ese nombre: " + nombreEquipo);
        }

        return jugadorRepository.findByEquipoNombreEquipo(nombreEquipo);
    }

    /**
     * Obtiene un jugador por su ID
     *
     * @param id ID del jugador
     * @return Jugador encontrado
     * @throws IllegalArgumentException si el ID es nulo
     * @throws RuntimeException si el jugador no existe
     */
    public Jugador obtenerJugador(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del jugador no puede ser nulo");
        }
        return jugadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + id));
    }

    /**
     * Funcionalidad 7.1: Buscar jugadores ordenados por puntos totales
     *
     * @return Lista de jugadores ordenados por puntos descendente
     */
    public List<Jugador> buscarJugadoresOrdenadosPorPuntos() {
        return jugadorRepository.findAllByOrderByEstadisticasPuntosJornadaDesc();
    }

    /**
     * Busca jugadores de campo ordenados por precio de mayor a menor
     *
     * @return Lista de jugadores ordenados por precio descendente
     */
    public List<Jugador> buscarPorPrecioMayorAMenor() {
        return jugadorRepository.findByEsPorteroOrderByPrecioMercadoDesc(false);
    }


    public List<Jugador> buscarPorNombreAMedias(String nombre)
    {
        if (nombre == null || nombre.isEmpty()) {
            throw new RuntimeException("El nombre de búsqueda no puede estar vacío");
        }
        return jugadorRepository.findByNombreJugadorContainingIgnoreCase(nombre);
    }

    /**
     * Busca jugadores ordenados por goles descendente
     *
     * @return Lista de jugadores ordenados por goles
     */
    public List<Jugador> buscarJugadoresPorGolesDesc() {
        return jugadorRepository.findJugadoresOrdenadosPorGoles();
    }

    /**
     * Busca porteros ordenados por precio de mercado descendente
     *
     * @return Lista de porteros ordenados por precio
     */
    public List<Jugador> buscarPorterosPorPrecioMercado() {
        return jugadorRepository.findByEsPorteroOrderByPrecioMercadoDesc(true);
    }

    /**
     * Busca porteros ordenados por puntos descendente
     *
     * @return Lista de porteros ordenados por puntos
     */
    public List<Jugador> buscarPorterosPorPuntosDesc() {
        List<Jugador> porteros = jugadorRepository.findByEsPortero(true);
        // Ordenar por puntos si existe método en repository, de lo contrario usar este
        return porteros;
    }

    /**
     * Busca todos los porteros
     *
     * @return Lista de porteros
     */
    public List<Jugador> buscarPorteros() {
        return jugadorRepository.findByEsPortero(true);
    }

    /**
     * Obtiene el repositorio de jugadores
     *
     * @return JugadorRepository
     */
    public JugadorRepository getJugadorRepository() {
        return jugadorRepository;
    }
}
