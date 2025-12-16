package com.example.Liga_Del_Cume.data.Controller;

import com.example.Liga_Del_Cume.data.model.*;
import com.example.Liga_Del_Cume.data.service.*;
import com.example.Liga_Del_Cume.data.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para gestionar las alineaciones de los usuarios
 * Maneja el historial de alineaciones por jornada
 */
@Controller
@RequestMapping("/liga/{ligaId}")
public class AlineacionController {

    @Autowired
    private AlineacionRepository alineacionRepository;

    @Autowired
    private JornadaRepository jornadaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstadisticaJugadorPartidoRepository estadisticaRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private LigaService ligaService;

    @Autowired
    private PartidoRepository partidoRepository;

    /**
     * Muestra la pantalla para crear una alineación futura (próxima jornada)
     */
    @GetMapping("/alineacion-futura")
    public String mostrarAlineacionFutura(
            @PathVariable("ligaId") Long ligaId,
            @RequestParam(value = "usuarioId", required = false) Long usuarioId,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // Obtener jornadas de la liga
            List<Jornada> jornadas = jornadaRepository.findByLigaIdLigaCume(ligaId);

            // Verificar si la liga ha terminado (todas las jornadas jugadas)
            boolean ligaFinalizada = !jornadas.isEmpty() && todasLasJornadasJugadas(jornadas);

            if (ligaFinalizada) {
                System.out.println("🏁 [ALINEACION FUTURA] Liga finalizada. Todas las jornadas han sido jugadas.");
                model.addAttribute("ligaFinalizada", true);
                model.addAttribute("mensajeFinal", "La liga ha finalizado. Todas las jornadas han sido jugadas y no se pueden añadir más alineaciones.");
                model.addAttribute("ligaId", ligaId);
                model.addAttribute("currentPage", "alineacion");

                // Obtener el nombre de la liga
                LigaCume ligaObj = ligaService.buscarLigaPorId(ligaId);
                String nombreLiga = ligaObj != null ? ligaObj.getNombreLiga() : "Mis Ligas";
                model.addAttribute("nombreLiga", nombreLiga);

                return "alineacionFutura";
            }

            // Calcular próxima jornada: la primera que no tiene resultados agregados
            Long proximaJornadaNumero = calcularProximaJornada(jornadas);

            // Obtener usuario: primero de sesión, luego del parámetro
            Usuario usuario = null;

            // Intentar obtener de la sesión
            if (session != null) {
                Object usuarioObj = session.getAttribute("usuario");
                if (usuarioObj instanceof Usuario) {
                    Usuario usuarioSession = (Usuario) usuarioObj;
                    usuario = usuarioRepository.findById(usuarioSession.getIdUsuario())
                            .orElse(null);
                    System.out.println("🔍 [ALINEACION FUTURA] Usuario de SESIÓN: " +
                        (usuario != null ? usuario.getNombreUsuario() + " (ID: " + usuario.getIdUsuario() + ")" : "null"));
                } else {
                    System.out.println("⚠️ [ALINEACION FUTURA] No hay usuario en sesión");
                }
            }

            // Si no hay en sesión, intentar del parámetro
            if (usuario == null && usuarioId != null) {
                usuario = usuarioRepository.findById(usuarioId)
                        .orElse(null);
                System.out.println("🔍 [ALINEACION FUTURA] Usuario del PARÁMETRO: " +
                    (usuario != null ? usuario.getNombreUsuario() + " (ID: " + usuario.getIdUsuario() + ")" : "null"));

                // Si encontramos usuario por parámetro, guardarlo en sesión
                if (usuario != null && session != null) {
                    session.setAttribute("usuario", usuario);
                    System.out.println("✅ [ALINEACION FUTURA] Usuario guardado en sesión");
                }
            }

            // Si aún no hay usuario, redirigir al login
            if (usuario == null) {
                System.out.println("❌ [ALINEACION FUTURA] No hay usuario. Redirigiendo a login.");
                redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para crear una alineación.");
                return "redirect:/";
            }

            // Obtener el nombre y presupuesto de la liga
            LigaCume ligaObj = ligaService.buscarLigaPorId(ligaId);
            String nombreLiga = ligaObj != null ? ligaObj.getNombreLiga() : "Mis Ligas";
            Long presupuestoMaximo = ligaObj != null ? ligaObj.getPresupuestoMaximo() : 500000L;

            // Pasar datos al modelo
            model.addAttribute("proximaJornada", proximaJornadaNumero);
            model.addAttribute("presupuestoDisponible", presupuestoMaximo);
            model.addAttribute("presupuestoTotal", presupuestoMaximo);
            model.addAttribute("usuario", usuario);
            model.addAttribute("ligaId", ligaId);
            model.addAttribute("currentPage", "alineacion");
            model.addAttribute("nombreLiga", nombreLiga);

            return "alineacionFutura";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al cargar alineación futura: " + e.getMessage());
            return "redirect:/liga/" + ligaId + "/ranking";
        }
    }

    /**
     * Calcula el número de la próxima jornada sin resultados agregados
     * Recorre todas las jornadas y devuelve el número de la primera que no tiene resultados
     */
    private Long calcularProximaJornada(List<Jornada> jornadas) {
        if (jornadas.isEmpty()) {
            return 1L;
        }

        // Ordenar jornadas por ID (asumiendo que ID correlaciona con el orden)
        jornadas.sort((j1, j2) -> j1.getIdJornada().compareTo(j2.getIdJornada()));

        // Buscar la primera jornada sin resultados
        for (Jornada jornada : jornadas) {
            if (jornadaSinResultados(jornada)) {
                // Devolver el número de orden de esta jornada
                return (long) (jornadas.indexOf(jornada) + 1);
            }
        }

        // Si todas tienen resultados, la próxima es la siguiente después de la última
        return (long) (jornadas.size() + 1);
    }

    /**
     * Verifica si una jornada no tiene resultados agregados
     * Una jornada sin resultados es aquella donde todos los partidos tienen 0-0
     * y no tienen estadísticas de jugadores registradas
     */
    private boolean jornadaSinResultados(Jornada jornada) {
        List<Partido> partidos = partidoRepository.findByJornadaIdJornada(jornada.getIdJornada());

        if (partidos.isEmpty()) {
            return true; // Si no hay partidos, consideramos que no tiene resultados
        }

        // Verificar si todos los partidos están sin jugar (0-0 y sin estadísticas)
        for (Partido partido : partidos) {
            // Si algún partido tiene goles, la jornada tiene resultados
            if (partido.getGolesLocal() != 0 || partido.getGolesVisitante() != 0) {
                return false;
            }

            // Si algún partido tiene estadísticas de jugadores, la jornada tiene resultados
            List<EstadisticaJugadorPartido> estadisticas =
                estadisticaRepository.findByPartidoIdPartido(partido.getIdPartido());
            if (estadisticas != null && !estadisticas.isEmpty()) {
                return false;
            }
        }

        return true; // Todos los partidos están sin jugar
    }

    /**
     * Verifica si todas las jornadas de la liga han sido jugadas
     * Retorna true si todos los partidos de todas las jornadas tienen resultados
     */
    private boolean todasLasJornadasJugadas(List<Jornada> jornadas) {
        if (jornadas.isEmpty()) {
            return false; // Si no hay jornadas, la liga no ha terminado
        }

        // Verificar que todas las jornadas tengan resultados
        for (Jornada jornada : jornadas) {
            if (jornadaSinResultados(jornada)) {
                return false; // Si encontramos una jornada sin resultados, la liga no ha terminado
            }
        }

        return true; // Todas las jornadas tienen resultados, la liga ha terminado
    }

    /**
     * Obtiene la lista de jugadores disponibles para una posición
     * Solo muestra jugadores de equipos que juegan en la próxima jornada
     */
    @GetMapping("/alineacion-futura/jugadores")
    @ResponseBody
    public List<JugadorDisponible> obtenerJugadoresDisponibles(
            @PathVariable("ligaId") Long ligaId,
            @RequestParam("posicion") String posicion,
            @RequestParam(value = "jugadoresSeleccionados", required = false) String jugadoresSeleccionados) {

        // Determinar si la posición es portero
        boolean esPortero = "portero".equalsIgnoreCase(posicion);

        // Obtener la próxima jornada
        List<Jornada> jornadas = jornadaRepository.findByLigaIdLigaCume(ligaId);
        Jornada proximaJornada = null;

        if (!jornadas.isEmpty()) {
            jornadas.sort((j1, j2) -> j1.getNumeroJornada().compareTo(j2.getNumeroJornada()));
            for (Jornada j : jornadas) {
                if (jornadaSinResultados(j)) {
                    proximaJornada = j;
                    break;
                }
            }
        }

        // Obtener equipos que juegan en la próxima jornada
        List<Long> equiposQueJuegan = new ArrayList<>();
        if (proximaJornada != null) {
            List<Partido> partidos = partidoRepository.findByJornadaIdJornada(proximaJornada.getIdJornada());
            for (Partido partido : partidos) {
                if (partido.getEquipoLocal() != null) {
                    equiposQueJuegan.add(partido.getEquipoLocal().getIdEquipo());
                }
                if (partido.getEquipoVisitante() != null) {
                    equiposQueJuegan.add(partido.getEquipoVisitante().getIdEquipo());
                }
            }
        }

        // Obtener jugadores según la posición
        List<Jugador> jugadores;
        if (esPortero) {
            jugadores = jugadorRepository.findByEsPortero(true);
        } else {
            jugadores = jugadorRepository.findByEsPortero(false);
        }

        // Filtrar jugadores ya seleccionados
        List<Long> idsSeleccionados = new ArrayList<>();
        if (jugadoresSeleccionados != null && !jugadoresSeleccionados.isEmpty()) {
            String[] ids = jugadoresSeleccionados.split(",");
            for (String id : ids) {
                try {
                    idsSeleccionados.add(Long.parseLong(id.trim()));
                } catch (NumberFormatException e) {
                    // Ignorar IDs inválidos
                }
            }
        }

        // Convertir a DTO y calcular puntos totales
        List<JugadorDisponible> jugadoresDisponibles = new ArrayList<>();
        for (Jugador jugador : jugadores) {
            // Solo incluir jugadores cuyo equipo juega en la próxima jornada
            boolean equipoJuega = equiposQueJuegan.isEmpty() ||
                                 (jugador.getEquipo() != null &&
                                  equiposQueJuegan.contains(jugador.getEquipo().getIdEquipo()));

            if (equipoJuega && !idsSeleccionados.contains(jugador.getIdJugador())) {
                int puntosTotal = calcularPuntosTotalesJugador(jugador);
                jugadoresDisponibles.add(new JugadorDisponible(
                    jugador.getIdJugador(),
                    jugador.getNombreJugador(),
                    jugador.getAvatarUrl(),
                    jugador.getEquipo().getEscudoURL(),
                    jugador.getEquipo().getNombreEquipo(),
                    puntosTotal,
                    (long) jugador.getPrecioMercado()
                ));
            }
        }

        return jugadoresDisponibles;
    }

    /**
     * Calcula los puntos totales de un jugador sumando todas sus estadísticas
     */
    private int calcularPuntosTotalesJugador(Jugador jugador) {
        List<EstadisticaJugadorPartido> estadisticas =
            estadisticaRepository.findByJugadorIdJugador(jugador.getIdJugador());

        int total = 0;
        for (EstadisticaJugadorPartido est : estadisticas) {
            total += est.getPuntosJornada();
        }
        return total;
    }

    /**
     * Guarda una nueva alineación para la próxima jornada
     */
    @PostMapping("/alineacion-futura/guardar")
    public String guardarAlineacionFutura(
            @PathVariable("ligaId") Long ligaId,
            @RequestParam(value = "usuarioId", required = false) Long usuarioIdParam,
            @RequestParam("portero") Long porteroId,
            @RequestParam("jugador1") Long jugador1Id,
            @RequestParam("jugador2") Long jugador2Id,
            @RequestParam("jugador3") Long jugador3Id,
            @RequestParam("jugador4") Long jugador4Id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. OBTENER USUARIO: primero de sesión, luego del parámetro
            Usuario usuario = null;

            System.out.println("\n========== GUARDAR ALINEACIÓN ==========");
            System.out.println("📋 Liga ID: " + ligaId);
            System.out.println("📋 Usuario Param: " + usuarioIdParam);

            // Intentar obtener de la sesión
            if (session != null) {
                Object usuarioObj = session.getAttribute("usuario");
                if (usuarioObj instanceof Usuario) {
                    Usuario usuarioSession = (Usuario) usuarioObj;
                    usuario = usuarioRepository.findById(usuarioSession.getIdUsuario())
                            .orElse(null);
                    System.out.println("🔍 [GUARDAR] Usuario de SESIÓN: " +
                        (usuario != null ? usuario.getNombreUsuario() + " (ID: " + usuario.getIdUsuario() + ")" : "null"));
                } else {
                    System.out.println("⚠️ [GUARDAR] No hay usuario en sesión");
                }
            }

            // Si no hay en sesión, intentar del parámetro
            if (usuario == null && usuarioIdParam != null) {
                usuario = usuarioRepository.findById(usuarioIdParam)
                        .orElse(null);
                System.out.println("🔍 [GUARDAR] Usuario del PARÁMETRO: " +
                    (usuario != null ? usuario.getNombreUsuario() + " (ID: " + usuario.getIdUsuario() + ")" : "null"));

                // Si encontramos usuario por parámetro, guardarlo en sesión
                if (usuario != null && session != null) {
                    session.setAttribute("usuario", usuario);
                    System.out.println("✅ [GUARDAR] Usuario guardado en sesión");
                }
            }

            // Si aún no hay usuario, redirigir al login
            if (usuario == null) {
                System.out.println("❌ [GUARDAR] NO SE ENCONTRÓ USUARIO. Redirigiendo a login.");
                System.out.println("==========================================\n");
                redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para guardar una alineación.");
                return "redirect:/";
            }

            System.out.println("✅ [GUARDAR] Alineación será guardada para: " +
                usuario.getNombreUsuario() + " (ID: " + usuario.getIdUsuario() + ")");
            System.out.println("==========================================\n");

            // 2. VALIDAR QUE EL USUARIO PERTENECE A LA LIGA
            if (usuario.getLiga() == null || !usuario.getLiga().getIdLigaCume().equals(ligaId)) {
                redirectAttributes.addFlashAttribute("error", "El usuario no pertenece a esta liga.");
                return "redirect:/liga/" + ligaId + "/alineacion-futura";
            }

            // 3. OBTENER LA PRÓXIMA JORNADA SIN RESULTADOS
            List<Jornada> jornadas = jornadaRepository.findByLigaIdLigaCume(ligaId);
            if (jornadas.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No hay jornadas disponibles en esta liga.");
                return "redirect:/liga/" + ligaId + "/alineacion-futura";
            }

            // Ordenar jornadas por número
            jornadas.sort((j1, j2) -> j1.getNumeroJornada().compareTo(j2.getNumeroJornada()));

            // Buscar la primera jornada sin resultados
            Jornada proximaJornada = null;
            for (Jornada j : jornadas) {
                if (jornadaSinResultados(j)) {
                    proximaJornada = j;
                    break;
                }
            }

            if (proximaJornada == null) {
                redirectAttributes.addFlashAttribute("error",
                    "No hay jornadas disponibles. Todas las jornadas ya tienen resultados.");
                return "redirect:/liga/" + ligaId + "/alineacion-futura";
            }

            // 4. VALIDAR QUE NO HAYA JUGADORES REPETIDOS
            List<Long> jugadorIds = new ArrayList<>();
            jugadorIds.add(porteroId);
            jugadorIds.add(jugador1Id);
            jugadorIds.add(jugador2Id);
            jugadorIds.add(jugador3Id);
            jugadorIds.add(jugador4Id);

            // Buscar duplicados
            List<Long> duplicados = new ArrayList<>();
            for (int i = 0; i < jugadorIds.size(); i++) {
                for (int j = i + 1; j < jugadorIds.size(); j++) {
                    if (jugadorIds.get(i).equals(jugadorIds.get(j))) {
                        duplicados.add(jugadorIds.get(i));
                    }
                }
            }

            if (!duplicados.isEmpty()) {
                // Obtener los nombres de los jugadores duplicados
                List<String> nombresRepetidos = new ArrayList<>();
                for (Long jugadorId : duplicados) {
                    jugadorRepository.findById(jugadorId).ifPresent(j -> {
                        if (!nombresRepetidos.contains(j.getNombreJugador())) {
                            nombresRepetidos.add(j.getNombreJugador());
                        }
                    });
                }

                String mensajeError;
                if (nombresRepetidos.size() == 1) {
                    mensajeError = "El jugador " + nombresRepetidos.get(0) + " está repetido en la alineación";
                } else {
                    mensajeError = "Los siguientes jugadores están repetidos: " + String.join(", ", nombresRepetidos);
                }

                redirectAttributes.addFlashAttribute("error", mensajeError);
                return "redirect:/liga/" + ligaId + "/alineacion-futura?usuarioId=" + usuario.getIdUsuario();
            }

            // 5. OBTENER JUGADORES
            Jugador portero = jugadorRepository.findById(porteroId).orElseThrow(() -> new Exception("Portero no encontrado"));
            Jugador jugador1 = jugadorRepository.findById(jugador1Id).orElseThrow(() -> new Exception("Jugador 1 no encontrado"));
            Jugador jugador2 = jugadorRepository.findById(jugador2Id).orElseThrow(() -> new Exception("Jugador 2 no encontrado"));
            Jugador jugador3 = jugadorRepository.findById(jugador3Id).orElseThrow(() -> new Exception("Jugador 3 no encontrado"));
            Jugador jugador4 = jugadorRepository.findById(jugador4Id).orElseThrow(() -> new Exception("Jugador 4 no encontrado"));

            // 6. BUSCAR SI YA EXISTE ALINEACIÓN PARA ESTE USUARIO Y JORNADA
            Optional<Alineacion> alineacionExistente = alineacionRepository
                    .findByUsuarioIdUsuarioAndJornadaIdJornada(usuario.getIdUsuario(), proximaJornada.getIdJornada());

            Alineacion alineacion;
            if (alineacionExistente.isPresent()) {
                // Actualizar alineación existente
                alineacion = alineacionExistente.get();
                alineacion.getJugadores().clear(); // Limpiamos los anteriores
            } else {
                // Crear nueva alineación
                alineacion = new Alineacion();
                alineacion.setUsuario(usuario); // <--- AQUI SE VINCULA AL USUARIO LOGUEADO
                alineacion.setJornada(proximaJornada);
            }

            // 7. AGREGAR JUGADORES
            alineacion.getJugadores().add(portero);
            alineacion.getJugadores().add(jugador1);
            alineacion.getJugadores().add(jugador2);
            alineacion.getJugadores().add(jugador3);
            alineacion.getJugadores().add(jugador4);

            alineacion.setPuntosTotalesJornada(0);

            // 8. GUARDAR
            alineacionRepository.save(alineacion);

            redirectAttributes.addFlashAttribute("success",
                    "Alineación guardada correctamente para la Jornada " + proximaJornada.getNumeroJornada());

            // Redirigimos pasando el usuarioId
            return "redirect:/liga/" + ligaId + "/alineacion-futura?usuarioId=" + usuario.getIdUsuario();

        } catch (Exception e) {
            e.printStackTrace(); // Útil para depurar en consola
            redirectAttributes.addFlashAttribute("error",
                    "Error al guardar la alineación: " + e.getMessage());
            return "redirect:/liga/" + ligaId + "/alineacion-futura";
        }
    }

    /**
     * Clase interna para representar un jugador disponible con sus datos
     */
    public static class JugadorDisponible {
        private Long id;
        private String nombre;
        private String avatarUrl;
        private String escudoEquipo;
        private String nombreEquipo;
        private int puntosTotal;
        private Long precio;

        public JugadorDisponible(Long id, String nombre, String avatarUrl, String escudoEquipo,
                                String nombreEquipo, int puntosTotal, Long precio) {
            this.id = id;
            this.nombre = nombre;
            this.avatarUrl = avatarUrl;
            this.escudoEquipo = escudoEquipo;
            this.nombreEquipo = nombreEquipo;
            this.puntosTotal = puntosTotal;
            this.precio = precio;
        }

        // Getters
        public Long getId() { return id; }
        public String getNombre() { return nombre; }
        public String getAvatarUrl() { return avatarUrl; }
        public String getEscudoEquipo() { return escudoEquipo; }
        public String getNombreEquipo() { return nombreEquipo; }
        public int getPuntosTotal() { return puntosTotal; }
        public Long getPrecio() { return precio; }
    }

    /**
     * Muestra el historial de alineaciones de un usuario en una jornada específica
     *
     * @param ligaId ID de la liga
     * @param usuarioId ID del usuario (puede venir de sesión)
     * @param jornadaId ID de la jornada (opcional, si no se proporciona usa la última)
     * @param model Modelo para pasar datos a la vista
     * @return Vista historialAlineaciones
     */
    @GetMapping("/historial")
    public String mostrarHistorialAlineaciones(
            @PathVariable("ligaId") Long ligaId,
            @RequestParam(value = "usuarioId", required = false) Long usuarioId,
            @RequestParam(value = "jornadaId", required = false) Long jornadaId,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // Obtener todas las jornadas de la liga para el desplegable
            List<Jornada> jornadas = jornadaRepository.findByLigaIdLigaCume(ligaId);

            if (jornadas.isEmpty()) {
                model.addAttribute("mensaje", "No hay jornadas disponibles en esta liga");
                model.addAttribute("ligaId", ligaId);
                model.addAttribute("currentPage", "historial");
                return "historialAlineaciones";
            }

            // Si no se especifica jornada, usar la primera (más reciente)
            Jornada jornadaSeleccionada;
            if (jornadaId == null) {
                jornadaSeleccionada = jornadas.get(0);
            } else {
                jornadaSeleccionada = jornadaRepository.findById(jornadaId)
                    .orElse(jornadas.get(0));
            }

            // Obtener usuario: primero intentar del parámetro, luego de la sesión, finalmente el primer usuario de la liga
            Usuario usuario = null;

            if (usuarioId != null) {
                // Si se proporciona usuarioId en el parámetro, usarlo
                usuario = usuarioRepository.findById(usuarioId)
                    .orElse(null);
            }

            if (usuario == null && session != null) {
                // Intentar obtener de la sesión
                Object usuarioObj = session.getAttribute("usuario");
                if (usuarioObj instanceof Usuario) {
                    usuario = (Usuario) usuarioObj;
                }
            }

            if (usuario == null) {
                // Como último recurso, usar el primer usuario de la liga
                List<Usuario> usuarios = usuarioRepository.findByLigaIdLigaCume(ligaId);
                if (usuarios.isEmpty()) {
                    model.addAttribute("mensaje", "No hay usuarios en esta liga");
                    model.addAttribute("ligaId", ligaId);
                    model.addAttribute("currentPage", "historial");
                    return "historialAlineaciones";
                }
                usuario = usuarios.get(0);
            }

            // Obtener la alineación del usuario para la jornada
            Optional<Alineacion> alineacionOpt = alineacionRepository.findByUsuarioIdUsuarioAndJornadaIdJornada(
                usuario.getIdUsuario(), jornadaSeleccionada.getIdJornada()
            );

            if (alineacionOpt.isPresent()) {
                Alineacion alineacion = alineacionOpt.get();

                // Separar portero y jugadores de campo
                Jugador portero = null;
                List<JugadorConEstadisticas> jugadoresCampo = new ArrayList<>();
                int puntosTotalesCalculados = 0; // Calcular puntos totales dinámicamente

                for (Jugador jugador : alineacion.getJugadores()) {
                    // Obtener estadísticas del jugador para esta jornada
                    EstadisticasJornada stats = obtenerEstadisticasJugadorJornada(
                        jugador, jornadaSeleccionada.getIdJornada()
                    );

                    JugadorConEstadisticas jugadorConStats = new JugadorConEstadisticas(
                        jugador, stats
                    );

                    // Sumar puntos si el jugador ha jugado
                    if (stats.haJugado) {
                        puntosTotalesCalculados += stats.puntos;
                    }

                    if (jugador.isEsPortero()) {
                        portero = jugador;
                        model.addAttribute("portero", jugadorConStats);
                    } else {
                        jugadoresCampo.add(jugadorConStats);
                    }
                }

                // Asignar posiciones a los jugadores de campo (4 posiciones)
                asignarPosicionesJugadores(jugadoresCampo, model);

                model.addAttribute("alineacion", alineacion);
                model.addAttribute("puntosTotales", puntosTotalesCalculados); // Usar puntos calculados dinámicamente
                model.addAttribute("dineroGastado", calcularDineroGastado(alineacion));

            } else {
                model.addAttribute("mensaje", "No hay alineación para esta jornada");
            }

            // Obtener el nombre de la liga
            LigaCume ligaObj = ligaService.buscarLigaPorId(ligaId);
            String nombreLiga = ligaObj != null ? ligaObj.getNombreLiga() : "Mis Ligas";

            // Pasar datos al modelo
            model.addAttribute("jornadas", jornadas);
            model.addAttribute("jornadaSeleccionada", jornadaSeleccionada);
            model.addAttribute("usuario", usuario);
            model.addAttribute("ligaId", ligaId);
            model.addAttribute("currentPage", "historial");
            model.addAttribute("nombreLiga", nombreLiga);

            return "historialAlineaciones";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al cargar el historial: " + e.getMessage());
            return "redirect:/liga/" + ligaId + "/ranking";
        }
    }

    /**
     * Obtiene las estadísticas de un jugador para una jornada específica
     */
    private EstadisticasJornada obtenerEstadisticasJugadorJornada(Jugador jugador, Long jornadaId) {
        List<EstadisticaJugadorPartido> estadisticas =
            estadisticaRepository.findByJugadorIdJugador(jugador.getIdJugador());

        for (EstadisticaJugadorPartido est : estadisticas) {
            if (est.getPartido().getJornada().getIdJornada().equals(jornadaId)) {
                return new EstadisticasJornada(
                    est.getPuntosJornada(),
                    est.getGolesAnotados(),
                    est.getAsistencias(),
                    true // Ha jugado
                );
            }
        }

        // No ha jugado o no tiene estadísticas
        return new EstadisticasJornada(0, 0, 0, false);
    }

    /**
     * Asigna posiciones a los jugadores de campo en el modelo
     * Posiciones: lateral izquierdo, central izquierdo, central derecho, lateral derecho
     */
    private void asignarPosicionesJugadores(List<JugadorConEstadisticas> jugadores, Model model) {
        if (jugadores.size() >= 1) {
            model.addAttribute("lateralIzquierdo", jugadores.get(0));
        }
        if (jugadores.size() >= 2) {
            model.addAttribute("lateralDerecho", jugadores.get(1));
        }
        if (jugadores.size() >= 3) {
            model.addAttribute("delanteroIzquierdo", jugadores.get(2));
        }
        if (jugadores.size() >= 4) {
            model.addAttribute("delanteroDerecho", jugadores.get(3));
        }
    }

    /**
     * Calcula el dinero gastado en la alineación
     */
    private long calcularDineroGastado(Alineacion alineacion) {
        long total = 0;
        for (Jugador jugador : alineacion.getJugadores()) {
            total += jugador.getPrecioMercado();
        }
        return total;
    }

    /**
     * Clase interna para representar un jugador con sus estadísticas
     */
    public static class JugadorConEstadisticas {
        private Jugador jugador;
        private EstadisticasJornada estadisticas;

        public JugadorConEstadisticas(Jugador jugador, EstadisticasJornada estadisticas) {
            this.jugador = jugador;
            this.estadisticas = estadisticas;
        }

        public Jugador getJugador() {
            return jugador;
        }

        public EstadisticasJornada getEstadisticas() {
            return estadisticas;
        }
    }

    /**
     * Clase interna para representar estadísticas de una jornada
     */
    public static class EstadisticasJornada {
        private int puntos;
        private int goles;
        private int asistencias;
        private boolean haJugado;

        public EstadisticasJornada(int puntos, int goles, int asistencias, boolean haJugado) {
            this.puntos = puntos;
            this.goles = goles;
            this.asistencias = asistencias;
            this.haJugado = haJugado;
        }

        public int getPuntos() {
            return puntos;
        }

        public int getGoles() {
            return goles;
        }

        public int getAsistencias() {
            return asistencias;
        }

        public boolean isHaJugado() {
            return haJugado;
        }
    }
}

