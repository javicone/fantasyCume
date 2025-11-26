package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.model.Usuario;
import com.example.Liga_Del_Cume.data.repository.UsuarioRepository;
import com.example.Liga_Del_Cume.data.repository.LigaCumeRepository;
import com.example.Liga_Del_Cume.data.service.exceptions.UsuarioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar usuarios (managers) de las ligas fantasy
 *
 * Funcionalidades principales:
 * - 1.1 a 1.4: Agregar, modificar, eliminar y listar usuarios
 * - 8: Consultar ranking de usuarios por puntos acumulados
 *
 * Los usuarios son los participantes o "managers" que crean alineaciones
 * y compiten entre sí en las ligas fantasy.
 *
 * Este servicio maneja toda la lógica de negocio relacionada con los usuarios,
 * incluyendo validaciones exhaustivas para garantizar la integridad de los datos.
 */
@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LigaCumeRepository ligaCumeRepository;

    /**
     * Funcionalidad 1.1: Da de alta un nuevo usuario en una liga
     *
     * Este método crea un nuevo usuario (manager) y lo asocia a una liga específica.
     * El usuario comienza con puntos iniciales (generalmente 0) y puede acumular
     * puntos a medida que juega en las jornadas.
     *
     * Validaciones:
     * 1. Verifica que el nombre no sea nulo o vacío
     * 2. Verifica que el nombre no contenga solo espacios
     * 3. Verifica que el nombre tenga al menos 3 caracteres (nombres significativos)
     * 4. Verifica que la liga no sea nula
     * 5. Verifica que la liga tenga un ID válido
     * 6. Verifica que la liga exista en la base de datos
     * 7. Verifica que no exista otro usuario con el mismo nombre en esa liga
     * 8. Verifica que los puntos iniciales no sean negativos
     *
     * @param nombre Nombre del usuario/manager
     * @param liga Liga a la que pertenecerá el usuario
     * @param puntosIniciales Puntos iniciales del usuario (generalmente 0)
     * @return Usuario creado y guardado en la base de datos
     * @throws UsuarioException Si alguna validación falla
     */
    public Usuario darDeAltaUsuario(String nombre, LigaCume liga, int puntosIniciales) {
        // Validación: nombre no nulo y no vacío (se usa helper para evitar duplicación)
        validarNombreNoVacio(nombre);

        // Normalizar el nombre una sola vez
        String nombreLimpio = nombre.trim();

        // Validación 3: Verificar longitud mínima del nombre (nombres significativos)
        if (nombreLimpio.length() < 3) {
            throw new UsuarioException(
                "El nombre del usuario debe tener al menos 3 caracteres. Nombre recibido: '" + nombre + "'"
            );
        }

        // Validación 4: Verificar que la liga no sea nula
        if (liga == null) {
            throw new UsuarioException("La liga no puede ser nula. El usuario debe pertenecer a una liga.");
        }

        // Validación 5: Verificar que la liga tenga un ID válido
        validarIdPositivo(liga.getIdLigaCume(), "ID de la liga");

        // Validación 6: Verificar que la liga exista en la base de datos
        LigaCume ligaExistente = obtenerLigaExistentePorId(liga.getIdLigaCume());

        // Validación 7: Verificar que no exista otro usuario con el mismo nombre en la liga
        Optional<Usuario> usuarioExistenteOpt = usuarioRepository.findByNombreUsuario(nombreLimpio);
        if (usuarioExistenteOpt.isPresent()) {
            Usuario usuarioExistente = usuarioExistenteOpt.get();
            if (usuarioExistente.getLiga() != null &&
                usuarioExistente.getLiga().getIdLigaCume().equals(liga.getIdLigaCume())) {
                throw new UsuarioException(
                    "Ya existe un usuario con el nombre '" + nombreLimpio +
                    "' en esta liga. Los nombres de usuario deben ser únicos dentro de cada liga."
                );
            }
        }

        // Validación 8: Verificar que los puntos iniciales no sean negativos
        if (puntosIniciales < 0) {
            throw new UsuarioException(
                "Los puntos iniciales no pueden ser negativos. Valor recibido: " + puntosIniciales
            );
        }

        // Si todas las validaciones pasan, crear el usuario
        Usuario usuario = new Usuario(nombreLimpio, puntosIniciales, ligaExistente);

        // Guardar y retornar el usuario creado
        return usuarioRepository.save(usuario);
    }

    /**
     * Funcionalidad 1.2: Modifica la información de un usuario
     *
     * Este método permite actualizar el nombre y/o los puntos acumulados de un usuario.
     * Es flexible: puede modificar solo el nombre, solo los puntos, o ambos.
     *
     * Validaciones:
     * 1. Verifica que el ID no sea nulo y positivo
     * 2. Verifica que el usuario exista en la base de datos
     * 3. Si se proporciona nombre, verifica que no esté vacío y tenga longitud mínima
     * 4. Si se proporciona nombre, verifica que no exista otro usuario con ese nombre
     * 5. Si se proporciona puntos, verifica que no sean negativos
     * 6. Verifica que al menos uno de los parámetros tenga un valor válido
     *
     * @param id ID del usuario a modificar
     * @param nuevoNombre Nuevo nombre del usuario (null si no se desea cambiar)
     * @param nuevosPuntos Nuevos puntos acumulados (null si no se desea cambiar)
     * @return Usuario modificado y guardado
     * @throws UsuarioException Si alguna validación falla
     */
    public Usuario modificarUsuario(Long id, String nuevoNombre, Integer nuevosPuntos) {
        // Validación 1: Verificar que el ID no sea nulo y positivo
        validarIdPositivo(id, "ID del usuario");

        // Validación 3: Verificar que el usuario exista
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioException(
                    "No existe ningún usuario con ID: " + id
                ));

        // Variable para rastrear si se realizó algún cambio
        boolean cambiosRealizados = false;

        // Modificar el nombre si se proporciona
        if (nuevoNombre != null) {
            String nuevoNombreLimpio = nuevoNombre.trim();
            if (!nuevoNombreLimpio.isEmpty()) {
                // Validación 4: Verificar longitud mínima del nombre
                if (nuevoNombreLimpio.length() < 3) {
                    throw new UsuarioException(
                        "El nombre del usuario debe tener al menos 3 caracteres. Nombre recibido: '" + nuevoNombre + "'"
                    );
                }

                // Validación 5: Verificar que no exista otro usuario con el mismo nombre en la misma liga
                if (!usuario.getNombreUsuario().equalsIgnoreCase(nuevoNombreLimpio)) {
                    Optional<Usuario> usuarioDuplicadoOpt = usuarioRepository.findByNombreUsuario(nuevoNombreLimpio);
                    if (usuarioDuplicadoOpt.isPresent()) {
                        Usuario usuarioDuplicado = usuarioDuplicadoOpt.get();
                        if (usuarioDuplicado.getLiga() != null &&
                            usuarioDuplicado.getLiga().getIdLigaCume().equals(usuario.getLiga().getIdLigaCume())) {
                            throw new UsuarioException(
                                "Ya existe otro usuario con el nombre '" + nuevoNombreLimpio +
                                "' en esta liga. Los nombres de usuario deben ser únicos."
                            );
                        }
                    }
                }

                // Aplicar el cambio de nombre
                usuario.setNombreUsuario(nuevoNombreLimpio);
                cambiosRealizados = true;
            }
        }

        // Modificar los puntos si se proporcionan
        if (nuevosPuntos != null) {
            // Validación 6: Verificar que los puntos no sean negativos
            if (nuevosPuntos < 0) {
                throw new UsuarioException(
                    "Los puntos acumulados no pueden ser negativos. Valor recibido: " + nuevosPuntos
                );
            }

            // Aplicar el cambio de puntos
            usuario.setPuntosAcumulados(nuevosPuntos);
            cambiosRealizados = true;
        }

        // Validación 7: Verificar que se haya realizado al menos un cambio
        if (!cambiosRealizados) {
            throw new UsuarioException(
                "Debe proporcionar al menos un valor válido para modificar (nombre o puntos)"
            );
        }

        // Guardar y retornar el usuario modificado
        return usuarioRepository.save(usuario);
    }

    /**
     * Funcionalidad 1.3: Elimina un usuario del sistema
     *
     * Este método elimina un usuario del sistema. Es importante tener en cuenta que:
     * - Las alineaciones del usuario se eliminarán en cascada (según configuración de entidad)
     * - Se perderá el historial del usuario en la liga
     * - Esta operación es irreversible
     *
     * Validaciones:
     * 1. Verifica que el ID no sea nulo y positivo
     * 2. Verifica que el usuario exista en la base de datos
     *
     * @param id ID del usuario a eliminar
     * @throws UsuarioException Si alguna validación falla
     */
    public void eliminarUsuario(Long id) {
        // Validación 1: Verificar que el ID no sea nulo y positivo
        validarIdPositivo(id, "ID del usuario");

        // Validación 3: Verificar que el usuario exista antes de intentar eliminarlo
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioException(
                    "No existe ningún usuario con ID: " + id + ". No se puede eliminar."
                ));

        // Nota: Las alineaciones se eliminarán en cascada según configuración de entidad
        // Se podría agregar validación adicional si se desea prevenir eliminación
        // de usuarios con alineaciones en jornadas evaluadas

        // Si todas las validaciones pasan, eliminar el usuario (usar la entidad para evitar warning de variable sin usar)
        usuarioRepository.delete(usuario);
    }

    /**
     * Funcionalidad 1.4: Lista todos los usuarios de una liga
     *
     * Este método recupera todos los usuarios asociados a una liga específica.
     * Es útil para:
     * - Mostrar los participantes de una liga
     * - Ver quién está inscrito en la liga
     * - Generar listados de usuarios
     *
     * Validaciones:
     * 1. Verifica que el ID de la liga no sea nulo
     * 2. Verifica que el ID de la liga sea un valor positivo
     * 3. Verifica que la liga exista en la base de datos
     *
     * @param ligaId ID de la liga de la cual se quieren listar los usuarios
     * @return Lista de usuarios de la liga (puede estar vacía si no hay usuarios)
     * @throws UsuarioException Si alguna validación falla
     */
    public List<Usuario> listarUsuariosPorLiga(Long ligaId) {
        // Validación 1: Verificar que el ID de la liga no sea nulo y positivo
        validarIdPositivo(ligaId, "ID de la liga");

        // Verificar que la liga exista (llamada sin asignar variable para evitar warning)
        obtenerLigaExistentePorId(ligaId);

        // Buscar y retornar todos los usuarios de la liga
        return usuarioRepository.findByLigaIdLigaCume(ligaId);
    }

    /**
     * Obtiene un usuario por su ID
     *
     * Este método recupera un usuario específico usando su identificador único.
     * Es un método básico de consulta útil para operaciones individuales.
     *
     * Validaciones:
     * 1. Verifica que el ID no sea nulo y positivo
     * 2. Verifica que el usuario exista en la base de datos
     *
     * @param id ID del usuario que se desea obtener
     * @return Usuario encontrado con todos sus datos
     * @throws UsuarioException Si alguna validación falla
     */
    public Usuario obtenerUsuario(Long id) {
        // Validación 1: Verificar que el ID no sea nulo y positivo
        validarIdPositivo(id, "ID del usuario");

        // Validación 3: Buscar y verificar que el usuario exista
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioException(
                    "No existe ningún usuario con ID: " + id
                ));
    }

    /**
     * Actualiza los puntos acumulados de un usuario (suma incremental)
     *
     * Este método añade puntos a los puntos acumulados actuales del usuario.
     * Se usa típicamente después de calcular los puntos de una alineación en una jornada.
     *
     * Validaciones:
     * 1. Verifica que el ID del usuario no sea nulo y positivo
     * 2. Verifica que el usuario exista (usa obtenerUsuario con sus validaciones)
     * 3. Verifica que los puntos a sumar sean válidos (pueden ser negativos para penalizaciones)
     * 4. Verifica que el resultado final no sea negativo
     *
     * @param usuarioId ID del usuario
     * @param puntosASumar Puntos a añadir (positivos para sumar, negativos para penalizar)
     * @throws UsuarioException Si alguna validación falla
     */
    public void actualizarPuntosAcumulados(Long usuarioId, int puntosASumar) {
        // Evitar validación duplicada: obtenerUsuario ya valida el id
        Usuario usuario = obtenerUsuario(usuarioId);

        // Calcular los nuevos puntos totales
        int puntosActuales = usuario.getPuntosAcumulados();
        int puntosNuevos = puntosActuales + puntosASumar;

        // Validación: Verificar que el resultado final no sea negativo
        if (puntosNuevos < 0) {
            throw new UsuarioException(
                "El resultado de actualizar puntos no puede ser negativo. " +
                "Puntos actuales: " + puntosActuales + ", Puntos a sumar: " + puntosASumar +
                ", Resultado: " + puntosNuevos
            );
        }

        // Actualizar los puntos
        usuario.setPuntosAcumulados(puntosNuevos);
        usuarioRepository.save(usuario);
    }

    /**
     * Funcionalidad 8: Consultar ranking de todos los usuarios de una liga
     *
     * Este método retorna todos los usuarios de una liga ordenados por sus puntos
     * acumulados de forma descendente (del mayor al menor). Es la tabla de clasificación
     * de la liga fantasy.
     *
     * Validaciones:
     * 1. Verifica que el ID de la liga no sea nulo
     * 2. Verifica que el ID de la liga sea un valor positivo
     * 3. Verifica que la liga exista en la base de datos
     *
     * @param ligaId ID de la liga
     * @return Lista de usuarios ordenados por puntos descendente (ranking)
     * @throws UsuarioException Si alguna validación falla
     */
    public List<Usuario> obtenerRankingLiga(Long ligaId) {
        // Validación 1: Verificar que el ID de la liga no sea nulo y positivo
        validarIdPositivo(ligaId, "ID de la liga");

        // Validación 3: Verificar que la liga exista (llamada sin asignar variable para evitar warning)
        obtenerLigaExistentePorId(ligaId);

        // Obtener y retornar el ranking (usuarios ordenados por puntos descendente)
        return usuarioRepository.findByLigaIdLigaCumeOrderByPuntosAcumuladosDesc(ligaId);
    }

    /**
     * Lista todos los usuarios del sistema
     *
     * Este método retorna todos los usuarios existentes en la base de datos,
     * independientemente de la liga a la que pertenezcan. Es útil para:
     * - Administración y supervisión del sistema
     * - Estadísticas globales de todos los usuarios
     * - Reportes generales del sistema
     *
     * Nota: Este método puede retornar grandes cantidades de datos si hay muchas ligas.
     * Para consultas específicas de una liga, es preferible usar listarUsuariosPorLiga().
     *
     * @return Lista con todos los usuarios del sistema (puede estar vacía)
     */
    public List<Usuario> listarTodosLosUsuarios() {
        // Obtener todos los usuarios de la base de datos
        return usuarioRepository.findAll();
    }

    /**
     * Busca un usuario por su nombre exacto
     *
     * Este método permite buscar un usuario por su nombre. Útil para verificaciones
     * y búsquedas específicas. No distingue entre mayúsculas y minúsculas.
     *
     * Validaciones:
     * 1. Verifica que el nombre no sea nulo o vacío
     * 2. Verifica que el nombre no contenga solo espacios
     *
     * @param nombre Nombre del usuario a buscar
     * @return Usuario encontrado o null si no existe
     * @throws UsuarioException Si el nombre no es válido
     */
    public Usuario buscarUsuarioPorNombre(String nombre) {
        // Usar helper para validar de forma consistente que el nombre no sea nulo ni vacío
        validarNombreNoVacio(nombre);

        String nombreLimpio = nombre.trim();

        // Buscar el usuario por nombre
        return usuarioRepository.findByNombreUsuario(nombreLimpio).orElse(null);
    }

    /**
     * Obtiene el número de usuarios en una liga
     *
     * Este método retorna la cantidad de usuarios registrados en una liga específica.
     * Es útil para estadísticas y mostrar información de la liga.
     *
     * Validaciones:
     * 1. Verifica que el ID de la liga no sea nulo
     * 2. Verifica que el ID sea un valor positivo
     * 3. Verifica que la liga exista
     *
     * @param ligaId ID de la liga
     * @return Número de usuarios en la liga (0 si no hay usuarios)
     * @throws UsuarioException Si alguna validación falla
     */
    public int contarUsuariosPorLiga(Long ligaId) {
        // Validación 1: Verificar que el ID no sea nulo y positivo
        validarIdPositivo(ligaId, "ID de la liga");

        // Verificar que la liga exista
        obtenerLigaExistentePorId(ligaId);

        // Contar y retornar los usuarios de la liga
        List<Usuario> usuarios = usuarioRepository.findByLigaIdLigaCume(ligaId);
        return usuarios.size();
    }

    /**
     * Resetea los puntos de un usuario a cero
     *
     * Este método establece los puntos acumulados de un usuario a cero.
     * Útil para resetear estadísticas o comenzar una nueva temporada.
     *
     * Validaciones:
     * 1. Verifica que el ID no sea nulo y positivo
     * 2. Verifica que el usuario exista
     *
     * @param usuarioId ID del usuario
     * @throws UsuarioException Si alguna validación falla
     */
    public void resetearPuntos(Long usuarioId) {
        // Evitar validación duplicada: obtenerUsuario ya valida el id
        Usuario usuario = obtenerUsuario(usuarioId);

        // Resetear los puntos a cero
        usuario.setPuntosAcumulados(0);
        usuarioRepository.save(usuario);
    }

    // Métodos auxiliares para evitar duplicación de condiciones y validar de forma centralizada
    private void validarNombreNoVacio(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new UsuarioException("El nombre del usuario no puede ser nulo o vacío");
        }
    }

    private void validarIdPositivo(Long id, String campo) {
        if (id == null) {
            throw new UsuarioException(campo + " no puede ser nulo");
        }
        if (id <= 0) {
            throw new UsuarioException(campo + " debe ser un valor positivo: " + id);
        }
    }

    private LigaCume obtenerLigaExistentePorId(Long ligaId) {
        return ligaCumeRepository.findById(ligaId)
                .orElseThrow(() -> new UsuarioException("No existe ninguna liga con ID: " + ligaId));
    }
}
