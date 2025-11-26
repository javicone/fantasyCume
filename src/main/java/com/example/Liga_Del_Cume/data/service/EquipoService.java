package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.Equipo;
import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.repository.EquipoRepository;
import com.example.Liga_Del_Cume.data.repository.LigaCumeRepository;
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

    /**
     * Funcionalidad 1.1: Agregar un nuevo equipo a la liga
     *
     * Este método permite crear un nuevo equipo y asociarlo a una liga específica.
     * El equipo debe tener un nombre único dentro de la liga para evitar confusiones.
     *
     * Validaciones:
     * 1. Verifica que el nombre no sea nulo o vacío
     * 2. Verifica que el nombre no contenga solo espacios
     * 3. Verifica que la liga no sea nula
     * 4. Verifica que la liga exista en la base de datos
     * 5. Verifica que no exista otro equipo con el mismo nombre (evita duplicados)
     *
     * @param nombre Nombre del equipo a crear
     * @param liga Liga a la que pertenecerá el equipo
     * @return Equipo creado y guardado en la base de datos
     * @throws EquipoException Si alguna validación falla
     */
    public Equipo agregarEquipo(String nombre, LigaCume liga) {
        // Validación 1: Verificar que el nombre no sea nulo o vacío
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new EquipoException("El nombre del equipo no puede ser nulo o vacío");
        }

        // Validación 2: Verificar que el nombre no contenga solo espacios en blanco
        if (nombre.trim().length() == 0) {
            throw new EquipoException("El nombre del equipo no puede contener solo espacios en blanco");
        }

        // Validación 3: Verificar que la liga no sea nula
        if (liga == null) {
            throw new EquipoException("La liga no puede ser nula. El equipo debe pertenecer a una liga.");
        }

        // Validación 4: Verificar que la liga exista en la base de datos
        if (liga.getIdLigaCume() != null) {
            LigaCume ligaExistente = ligaCumeRepository.findById(liga.getIdLigaCume())
                    .orElseThrow(() -> new EquipoException(
                        "No existe ninguna liga con ID: " + liga.getIdLigaCume()
                    ));
        }

        // Validación 5: Verificar que no exista otro equipo con el mismo nombre
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
     * Este método elimina un equipo del sistema. Es importante tener en cuenta
     * que eliminar un equipo puede afectar a jugadores, partidos y estadísticas
     * relacionadas, dependiendo de las configuraciones de cascada en las entidades.
     *
     * Validaciones:
     * 1. Verifica que el ID no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que el equipo exista en la base de datos
     * 4. Opcionalmente, verifica si el equipo tiene jugadores asociados (advertencia)
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

        // Validación 4 (opcional): Verificar si el equipo tiene jugadores asociados
        // Esto es una advertencia útil para el usuario
        if (equipo.getJugadores() != null && !equipo.getJugadores().isEmpty()) {
            // Nota: Dependiendo de la configuración de cascada, los jugadores
            // pueden eliminarse automáticamente o puede generarse un error
            int cantidadJugadores = equipo.getJugadores().size();
            // Se podría lanzar una excepción aquí si no se desea permitir
            // la eliminación de equipos con jugadores
            // Por ahora, solo se procede con la eliminación
        }

        // Si todas las validaciones pasan, eliminar el equipo
        equipoRepository.deleteById(id);
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

