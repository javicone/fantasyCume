package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.Equipo;
import com.example.Liga_Del_Cume.data.model.Jornada;
import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.model.Partido;
import com.example.Liga_Del_Cume.data.repository.AlineacionRepository;
import com.example.Liga_Del_Cume.data.repository.EquipoRepository;
import com.example.Liga_Del_Cume.data.repository.LigaCumeRepository;
import com.example.Liga_Del_Cume.data.repository.PartidoRepository;
import com.example.Liga_Del_Cume.data.exceptions.EquipoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar equipos de fútbol
 *
 * Funcionalidades principales:
 * - 1.1: Agregar un nuevo equipo a la liga
 * - 1.2: Modificar información de un equipo
 * - 1.3: Eliminar un equipo
 * - 1.4: Listar todos los equipos de una liga
 *
 * Este servicio maneja toda la lógica de negocio relacionada con los equipos,
 * incluyendo validaciones exhaustivas para garantizar la integridad de los datos.
 */
@Service
@Transactional
public class EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private LigaCumeRepository ligaCumeRepository;

    @Autowired
    private PartidoRepository partidoRepository;

    @Autowired
    private JornadaService jornadaService;

    @Autowired
    private AlineacionRepository alineacionRepository;

    /**
     * Funcionalidad 1.1: Agregar un nuevo equipo a la liga
     *
     * Este método permite crear un nuevo equipo y asociarlo a una liga específica.
     * El equipo debe tener un nombre único dentro de la liga para evitar confusiones.
     *
     * Validaciones:
     * 1. Verifica que el nombre no sea nulo o vacío
     * 2. Verifica que el nombre no contenga solo espacios
     * 3. Verifica que el ID de la liga no sea nulo
     * 4. Verifica que el ID de la liga sea un valor positivo
     * 5. Verifica que la liga exista en la base de datos
     * 6. Verifica que no exista otro equipo con el mismo nombre (evita duplicados)
     *
     * @param nombre Nombre del equipo a crear
     * @param idLiga ID de la liga a la que pertenecerá el equipo
     * @param URLescudo URL del escudo del equipo
     * @return Equipo creado y guardado en la base de datos
     * @throws EquipoException Si alguna validación falla
     */
    public Equipo agregarEquipo(String nombre, Long idLiga, String URLescudo) {
        // Validación 1: Verificar que el nombre no sea nulo o vacío
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new EquipoException("El nombre del equipo no puede ser nulo o vacío");
        }

        // Validación 2: Verificar que el nombre no contenga solo espacios en blanco
        if (nombre.trim().isEmpty()) {
            throw new EquipoException("El nombre del equipo no puede contener solo espacios en blanco");
        }

        // Validación 3: Verificar que el ID de la liga no sea nulo
        if (idLiga == null) {
            throw new EquipoException("El ID de la liga no puede ser nulo. El equipo debe pertenecer a una liga.");
        }

        // Validación 4: Verificar que el ID de la liga sea un valor positivo
        if (idLiga <= 0) {
            throw new EquipoException("El ID de la liga debe ser un valor positivo: " + idLiga);
        }

        // Validación 5: Verificar que la liga exista en la base de datos
        LigaCume liga = ligaCumeRepository.findById(idLiga)
                .orElseThrow(() -> new EquipoException(
                    "No existe ninguna liga con ID: " + idLiga
                ));

        // Validación 6: Verificar que no exista otro equipo con el mismo nombre
        // Esto evita duplicados y confusiones en el sistema
        Equipo equipoExistente = equipoRepository.findByNombreEquipoIgnoreCase(nombre.trim());
        if (equipoExistente != null) {
            throw new EquipoException(
                "Ya existe un equipo con el nombre '" + nombre +
                "'. Los nombres de equipos deben ser únicos."
            );
        }

        // Si todas las validaciones pasan, crear el equipo
        Equipo equipo = new Equipo();
        equipo.setNombreEquipo(nombre.trim()); // Eliminar espacios al inicio y final
        equipo.setLiga(liga);
        equipo.setEscudoURL(URLescudo);

        // Guardar y retornar el equipo creado
        return equipoRepository.save(equipo);
    }

    /**
     * Funcionalidad 1.2: Modificar información de un equipo
     *
     * Este método permite actualizar el nombre y/o escudo de un equipo existente.
     * Es flexible: puede modificar solo el nombre, solo el escudo, o ambos.
     *
     * Validaciones:
     * 1. Verifica que el ID no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que el equipo exista en la base de datos
     * 4. Si se proporciona un nuevo nombre, verifica que no esté vacío
     * 5. Si se proporciona un nuevo nombre, verifica que no exista otro equipo con ese nombre
     * 6. Verifica que al menos uno de los parámetros (nombre o escudo) tenga un valor válido
     *
     * @param id ID del equipo a modificar
     * @param nuevoNombre Nuevo nombre del equipo (null si no se desea cambiar)
     * @param nuevoEscudo Nueva URL del escudo (null si no se desea cambiar)
     * @return Equipo modificado y guardado
     * @throws EquipoException Si alguna validación falla
     */
    public Equipo modificarEquipo(Long id, String nuevoNombre, String nuevoEscudo) {
        // Validación 1: Verificar que el ID no sea nulo
        if (id == null) {
            throw new EquipoException("El ID del equipo no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (id <= 0) {
            throw new EquipoException("El ID del equipo debe ser un valor positivo: " + id);
        }

        // Validación 3: Verificar que el equipo exista
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new EquipoException(
                    "No existe ningún equipo con ID: " + id
                ));

        // Variable para rastrear si se realizó algún cambio
        boolean cambiosRealizados = false;

        // Modificar el nombre si se proporciona
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            // Validación 4: Verificar que el nuevo nombre no esté vacío
            if (nuevoNombre.trim().length() == 0) {
                throw new EquipoException("El nuevo nombre del equipo no puede contener solo espacios en blanco");
            }

            // Validación 5: Verificar que no exista otro equipo con el mismo nombre
            // Solo verificar si el nombre es diferente al actual
            if (!equipo.getNombreEquipo().equalsIgnoreCase(nuevoNombre.trim())) {
                Equipo equipoDuplicado = equipoRepository.findByNombreEquipoIgnoreCase(nuevoNombre.trim());
                if (equipoDuplicado != null) {
                    throw new EquipoException(
                        "Ya existe otro equipo con el nombre '" + nuevoNombre +
                        "'. Los nombres de equipos deben ser únicos."
                    );
                }
            }

            // Aplicar el cambio de nombre
            equipo.setNombreEquipo(nuevoNombre.trim());
            cambiosRealizados = true;
        }

        // Modificar el escudo si se proporciona
        if (nuevoEscudo != null && !nuevoEscudo.trim().isEmpty()) {
            equipo.setEscudoURL(nuevoEscudo.trim());
            cambiosRealizados = true;
        }

        // Validación 6: Verificar que se haya realizado al menos un cambio
        if (!cambiosRealizados) {
            throw new EquipoException(
                "Debe proporcionar al menos un valor válido para modificar (nombre o escudo)"
            );
        }

        // Guardar y retornar el equipo modificado
        return equipoRepository.save(equipo);
    }

    /**
     * Funcionalidad 1.3: Eliminar un equipo
     *
     * Este método elimina un equipo del sistema. Si la liga ha sido reiniciada
     * (no hay resultados en ningún partido), elimina todas las dependencias
     * (alineaciones, partidos, jornadas) y regenera los cuadros de competición.
     *
     * Validaciones:
     * 1. Verifica que el ID no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que el equipo exista en la base de datos
     * 4. Verifica si la liga ha sido reiniciada (sin resultados)
     * 5. Si está reiniciada, elimina dependencias y regenera jornadas
     *
     * @param id ID del equipo a eliminar
     * @throws EquipoException Si alguna validación falla
     */
    public void eliminarEquipo(Long id) {
        // Validación 1: Verificar que el ID no sea nulo
        if (id == null) {
            throw new EquipoException("El ID del equipo no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (id <= 0) {
            throw new EquipoException("El ID del equipo debe ser un valor positivo: " + id);
        }

        // Validación 3: Verificar que el equipo exista antes de intentar eliminarlo
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new EquipoException(
                    "No existe ningún equipo con ID: " + id + ". No se puede eliminar."
                ));

        // Obtener la liga del equipo
        LigaCume liga = equipo.getLiga();
        if (liga == null) {
            throw new EquipoException("El equipo no tiene una liga asociada");
        }

        // Validación 4: Verificar si la liga ha sido reiniciada (sin resultados)
        boolean ligaReiniciada = verificarLigaReiniciada(liga.getIdLigaCume());

        if (ligaReiniciada) {
            // La liga está reiniciada, eliminamos todas las dependencias
            // 1. Eliminar todas las alineaciones de las jornadas de la liga
            List<Jornada> jornadas = jornadaService.listarJornadasPorLiga(liga.getIdLigaCume());
            for (Jornada jornada : jornadas) {
                List<com.example.Liga_Del_Cume.data.model.Alineacion> alineaciones =
                    alineacionRepository.findByJornadaIdJornada(jornada.getIdJornada());
                if (alineaciones != null && !alineaciones.isEmpty()) {
                    alineacionRepository.deleteAll(alineaciones);
                }
            }

            // 2. Eliminar todas las jornadas (esto eliminará los partidos en cascada)
            for (Jornada jornada : jornadas) {
                jornadaService.eliminarJornada(jornada.getIdJornada());
            }

            // 3. Eliminar el equipo
            equipoRepository.deleteById(id);

            // 4. Regenerar cuadros de competición
            regenerarCuadrosCompeticion(liga.getIdLigaCume());
        } else {
            // La liga tiene resultados, no se puede eliminar el equipo sin afectar la integridad
            throw new EquipoException(
                "No se puede eliminar el equipo porque la liga ya tiene resultados registrados. " +
                "Para eliminar el equipo, primero debes reiniciar la liga."
            );
        }
    }

    /**
     * Verifica si la liga ha sido reiniciada (todos los partidos tienen resultado 0-0)
     *
     * @param ligaId ID de la liga
     * @return true si la liga está reiniciada, false en caso contrario
     */
    private boolean verificarLigaReiniciada(Long ligaId) {
        List<Jornada> jornadas = jornadaService.listarJornadasPorLiga(ligaId);

        // Si no hay jornadas, consideramos que está reiniciada
        if (jornadas == null || jornadas.isEmpty()) {
            return true;
        }

        // Verificar que todos los partidos tengan resultado 0-0
        for (Jornada jornada : jornadas) {
            List<Partido> partidos = partidoRepository.findByJornadaIdJornada(jornada.getIdJornada());
            for (Partido partido : partidos) {
                if (partido.getGolesLocal() != 0 || partido.getGolesVisitante() != 0) {
                    return false; // Hay al menos un partido con resultados
                }
            }
        }

        return true; // Todos los partidos están a 0-0
    }

    /**
     * Regenera los cuadros de competición para una liga
     *
     * @param ligaId ID de la liga
     */
    private void regenerarCuadrosCompeticion(Long ligaId) {
        try {
            // Obtener la liga
            LigaCume liga = ligaCumeRepository.findById(ligaId)
                    .orElseThrow(() -> new EquipoException("No se encontró la liga con ID: " + ligaId));

            // Obtener equipos
            List<Equipo> equipos = listarEquiposPorLiga(ligaId);

            // Si hay menos de 2 equipos, no se pueden generar jornadas
            if (equipos == null || equipos.size() < 2) {
                return; // No hay suficientes equipos para generar jornadas
            }

            // Preparar lista; si impar añadimos bye (null)
            List<Equipo> lista = new java.util.ArrayList<>(equipos);
            if (lista.size() % 2 == 1) {
                lista.add(null);
            }

            int n = lista.size();
            int rondas = n - 1;
            int partidosPorRonda = n / 2;

            // Construir emparejamientos para cada ronda (primera vuelta)
            List<List<Equipo[]>> rondasEmparejamientos = new java.util.ArrayList<>();
            List<Equipo> rotantes = new java.util.ArrayList<>(lista);
            for (int r = 0; r < rondas; r++) {
                List<Equipo[]> emparejamientos = new java.util.ArrayList<>();
                for (int i = 0; i < partidosPorRonda; i++) {
                    Equipo local = rotantes.get(i);
                    Equipo visitante = rotantes.get(n - 1 - i);
                    if (local == null || visitante == null) continue; // bye
                    emparejamientos.add(new Equipo[]{local, visitante});
                }
                rondasEmparejamientos.add(emparejamientos);
                // rotar sublista manteniendo índice 0 fijo
                if (rotantes.size() > 1) {
                    java.util.Collections.rotate(rotantes.subList(1, rotantes.size()), 1);
                }
            }

            // Crear jornadas para la primera vuelta
            for (List<Equipo[]> emparejamientos : rondasEmparejamientos) {
                Jornada jornada = jornadaService.crearJornada(liga);
                for (Equipo[] pair : emparejamientos) {
                    Partido partido = new Partido(pair[0], pair[1], 0, 0, jornada);
                    partidoRepository.save(partido);
                }
            }

            // Crear jornadas para la segunda vuelta (vuelta: invertir local/visitante)
            for (List<Equipo[]> emparejamientos : rondasEmparejamientos) {
                Jornada jornada = jornadaService.crearJornada(liga);
                for (Equipo[] pair : emparejamientos) {
                    Partido partido = new Partido(pair[1], pair[0], 0, 0, jornada);
                    partidoRepository.save(partido);
                }
            }
        } catch (Exception e) {
            throw new EquipoException("Error al regenerar cuadros de competición: " + e.getMessage());
        }
    }

    /**
     * Funcionalidad 1.4: Listar todos los equipos de una liga
     *
     * Este método recupera todos los equipos asociados a una liga específica.
     * Es útil para mostrar información de la liga, generar estadísticas,
     * o permitir al usuario seleccionar equipos de una liga determinada.
     *
     * Validaciones:
     * 1. Verifica que el ID de la liga no sea nulo
     * 2. Verifica que el ID de la liga sea un valor positivo
     * 3. Verifica que la liga exista en la base de datos
     *
     * @param ligaId ID de la liga de la cual se quieren listar los equipos
     * @return Lista de equipos de la liga (puede estar vacía si no hay equipos)
     * @throws EquipoException Si alguna validación falla
     */
    public List<Equipo> listarEquiposPorLiga(Long ligaId) {
        // Validación 1: Verificar que el ID de la liga no sea nulo
        if (ligaId == null) {
            throw new EquipoException("El ID de la liga no puede ser nulo");
        }

        // Validación 2: Verificar que el ID de la liga sea un valor positivo
        if (ligaId <= 0) {
            throw new EquipoException(
                "El ID de la liga debe ser un valor positivo: " + ligaId
            );
        }

        // Validación 3: Verificar que la liga exista
        LigaCume liga = ligaCumeRepository.findById(ligaId)
                .orElseThrow(() -> new EquipoException(
                    "No existe ninguna liga con ID: " + ligaId
                ));

        // Buscar y retornar todos los equipos de la liga
        return equipoRepository.findByLigaIdLigaCume(ligaId);
    }

    /**
     * Obtiene un equipo por su ID
     *
     * Este método recupera un equipo específico usando su identificador único.
     * Es un método básico de consulta útil para operaciones individuales.
     *
     * Validaciones:
     * 1. Verifica que el ID no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que el equipo exista en la base de datos
     *
     * @param id ID del equipo que se desea obtener
     * @return Equipo encontrado con todos sus datos
     * @throws EquipoException Si alguna validación falla
     */
    public Equipo obtenerEquipo(Long id) {
        // Validación 1: Verificar que el ID no sea nulo
        if (id == null) {
            throw new EquipoException("El ID del equipo no puede ser nulo");
        }

        // Validación 2: Verificar que el ID sea un valor positivo
        if (id <= 0) {
            throw new EquipoException("El ID del equipo debe ser un valor positivo: " + id);
        }

        // Validación 3: Buscar y verificar que el equipo exista
        return equipoRepository.findById(id)
                .orElseThrow(() -> new EquipoException(
                    "No existe ningún equipo con ID: " + id
                ));
    }

    /**
     * Lista todos los equipos del sistema
     *
     * Este método retorna todos los equipos existentes en la base de datos,
     * independientemente de la liga a la que pertenezcan. Es útil para:
     * - Administración y supervisión del sistema
     * - Estadísticas globales
     * - Reportes generales
     * - Búsqueda general de equipos
     *
     * Nota: Este método puede retornar grandes cantidades de datos si hay muchas ligas y equipos.
     * Para consultas específicas, es preferible usar listarEquiposPorLiga().
     *
     * @return Lista con todos los equipos del sistema (puede estar vacía)
     */
    public List<Equipo> listarTodosLosEquipos() {
        // Obtener todos los equipos de la base de datos
        return equipoRepository.findAll();
    }

    /**
     * Método alternativo para listar todos los equipos
     * (alias de listarTodosLosEquipos para mayor claridad)
     */
    public List<Equipo> listarTodosEquipos() {
        return listarTodosLosEquipos();
    }

    /**
     * Crear un equipo sin asociarlo a una liga específica
     *
     * Este método permite crear equipos que posteriormente pueden
     * ser asociados a diferentes ligas.
     *
     * Validaciones:
     * 1. Verifica que el nombre no sea nulo o vacío
     * 2. Verifica que la URL del escudo no sea nula o vacía
     * 3. Verifica que no exista otro equipo con el mismo nombre
     *
     * @param nombre Nombre del equipo
     * @param escudoURL URL de la imagen del escudo
     * @return Equipo creado y guardado
     * @throws EquipoException Si alguna validación falla
     */
    public Equipo crearEquipoSinLiga(String nombre, String escudoURL) {
        // Validación 1: Verificar que el nombre no sea nulo o vacío
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new EquipoException("El nombre del equipo no puede ser nulo o vacío");
        }

        // Validación 2: Verificar que la URL del escudo no sea nula o vacía
        if (escudoURL == null || escudoURL.trim().isEmpty()) {
            throw new EquipoException("La URL del escudo no puede ser nula o vacía");
        }

        // Validación 3: Verificar que no exista otro equipo con el mismo nombre
        Equipo equipoExistente = equipoRepository.findByNombreEquipoIgnoreCase(nombre.trim());
        if (equipoExistente != null) {
            throw new EquipoException(
                "Ya existe un equipo con el nombre '" + nombre +
                "'. Los nombres de equipos deben ser únicos."
            );
        }

        // Crear el equipo
        Equipo equipo = new Equipo();
        equipo.setNombreEquipo(nombre.trim());
        equipo.setEscudoURL(escudoURL.trim());

        // Guardar y retornar el equipo creado
        return equipoRepository.save(equipo);
    }

    /**
     * Busca un equipo por su nombre exacto (ignorando mayúsculas/minúsculas)
     *
     * Este método permite buscar un equipo usando su nombre de forma insensible
     * a mayúsculas y minúsculas. Útil para búsquedas y verificaciones.
     *
     * Validaciones:
     * 1. Verifica que el nombre no sea nulo o vacío
     * 2. Verifica que el nombre no contenga solo espacios
     *
     * @param nombre Nombre del equipo a buscar
     * @return Equipo encontrado o null si no existe
     * @throws EquipoException Si el nombre no es válido
     */
    public Equipo buscarEquipoPorNombre(String nombre) {
        // Validación 1: Verificar que el nombre no sea nulo o vacío
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new EquipoException("El nombre del equipo no puede ser nulo o vacío para realizar la búsqueda");
        }

        // Validación 2: Verificar que el nombre no contenga solo espacios
        if (nombre.trim().length() == 0) {
            throw new EquipoException("El nombre del equipo no puede contener solo espacios en blanco");
        }

        // Buscar el equipo por nombre (ignorando mayúsculas/minúsculas)
        return equipoRepository.findByNombreEquipoIgnoreCase(nombre.trim());
    }

    /**
     * Busca equipos cuyo nombre contenga el texto especificado
     *
     * Este método realiza una búsqueda parcial, útil para implementar
     * funciones de autocompletado o búsqueda flexible en la interfaz de usuario.
     * Por ejemplo: buscar "Real" puede devolver "Real Madrid", "Real Sociedad", etc.
     *
     * Validaciones:
     * 1. Verifica que el texto de búsqueda no sea nulo o vacío
     * 2. Verifica que el texto tenga al menos 2 caracteres (búsquedas significativas)
     *
     * @param nombreParcial Texto a buscar dentro de los nombres de equipos
     * @return Lista de equipos cuyo nombre contiene el texto (puede estar vacía)
     * @throws EquipoException Si el texto de búsqueda no es válido
     */
    public List<Equipo> buscarEquiposPorNombreParcial(String nombreParcial) {
        // Validación 1: Verificar que el texto de búsqueda no sea nulo o vacío
        if (nombreParcial == null || nombreParcial.trim().isEmpty()) {
            throw new EquipoException("El texto de búsqueda no puede ser nulo o vacío");
        }

        // Validación 2: Verificar que el texto tenga al menos 2 caracteres
        // Esto evita búsquedas demasiado amplias con un solo carácter
        if (nombreParcial.trim().length() < 2) {
            throw new EquipoException(
                "El texto de búsqueda debe tener al menos 2 caracteres. " +
                "Búsquedas con un solo carácter pueden devolver demasiados resultados."
            );
        }

        // Realizar búsqueda parcial (case-insensitive)
        return equipoRepository.findByNombreEquipoContainingIgnoreCase(nombreParcial.trim());
    }
}

