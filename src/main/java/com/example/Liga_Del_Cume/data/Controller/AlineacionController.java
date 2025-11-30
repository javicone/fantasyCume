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
            RedirectAttributes redirectAttributes) {

        try {
            // Obtener jornadas de la liga
            List<Jornada> jornadas = jornadaRepository.findByLigaIdLigaCume(ligaId);

            // Calcular próxima jornada: la primera que no tiene resultados agregados
            Long proximaJornadaNumero = calcularProximaJornada(jornadas);

            // Obtener usuario
            Usuario usuario;
            if (usuarioId == null) {
                List<Usuario> usuarios = usuarioRepository.findByLigaIdLigaCume(ligaId);
                if (usuarios.isEmpty()) {
                    model.addAttribute("mensaje", "No hay usuarios en esta liga");
                    return "alineacionFutura";
                }
                usuario = usuarios.get(0);
            } else {
                usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));
            }

            // Obtener el nombre y presupuesto de la liga
            LigaCume ligaObj = ligaService.buscarLigaPorId(ligaId);
            String nombreLiga = ligaObj != null ? ligaObj.getNombreLiga() : "Mis Ligas";
            Long presupuestoMaximo = ligaObj != null ? ligaObj.getPresupuestoMaximo() : 100000000L;

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
     * Obtiene la lista de jugadores disponibles para una posición
     */
    @GetMapping("/alineacion-futura/jugadores")
    @ResponseBody
    public List<JugadorDisponible> obtenerJugadoresDisponibles(
            @PathVariable("ligaId") Long ligaId,
            @RequestParam("posicion") String posicion,
            @RequestParam(value = "jugadoresSeleccionados", required = false) String jugadoresSeleccionados) {

        // Determinar si la posición es portero
        boolean esPortero = "portero".equalsIgnoreCase(posicion);

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
            if (!idsSeleccionados.contains(jugador.getIdJugador())) {
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
            // Eliminamos @RequestParam("usuarioId") porque lo sacamos de la sesión por seguridad
            @RequestParam("portero") Long porteroId,
            @RequestParam("jugador1") Long jugador1Id,
            @RequestParam("jugador2") Long jugador2Id,
            @RequestParam("jugador3") Long jugador3Id,
            @RequestParam("jugador4") Long jugador4Id,
            HttpSession session, // <--- IMPORTANTE: Inyectamos la sesión
            RedirectAttributes redirectAttributes) {

        try {
            // 1. OBTENER USUARIO DE LA SESIÓN (SEGURIDAD)
            Usuario usuarioSession = (Usuario) session.getAttribute("usuario");

            if (usuarioSession == null) {
                redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para guardar la alineación.");
                return "redirect:/";
            }

            // Refrescamos el usuario desde la BD para asegurar que está "vivo" en Hibernate
            // y evitar errores de LazyLoading o entidades detatched.
            Usuario usuario = usuarioRepository.findById(usuarioSession.getIdUsuario())
                    .orElseThrow(() -> new Exception("Usuario no encontrado en base de datos"));

            // 2. VALIDAR QUE EL USUARIO PERTENECE A LA LIGA
            if (usuario.getLiga() == null || !usuario.getLiga().getIdLigaCume().equals(ligaId)) {
                redirectAttributes.addFlashAttribute("error", "No puedes guardar alineación en una liga a la que no perteneces.");
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

            // 4. OBTENER JUGADORES
            Jugador portero = jugadorRepository.findById(porteroId).orElseThrow(() -> new Exception("Portero no encontrado"));
            Jugador jugador1 = jugadorRepository.findById(jugador1Id).orElseThrow(() -> new Exception("Jugador 1 no encontrado"));
            Jugador jugador2 = jugadorRepository.findById(jugador2Id).orElseThrow(() -> new Exception("Jugador 2 no encontrado"));
            Jugador jugador3 = jugadorRepository.findById(jugador3Id).orElseThrow(() -> new Exception("Jugador 3 no encontrado"));
            Jugador jugador4 = jugadorRepository.findById(jugador4Id).orElseThrow(() -> new Exception("Jugador 4 no encontrado"));

            // 5. BUSCAR SI YA EXISTE ALINEACIÓN PARA ESTE USUARIO Y JORNADA
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

            // 6. AGREGAR JUGADORES
            alineacion.getJugadores().add(portero);
            alineacion.getJugadores().add(jugador1);
            alineacion.getJugadores().add(jugador2);
            alineacion.getJugadores().add(jugador3);
            alineacion.getJugadores().add(jugador4);

            alineacion.setPuntosTotalesJornada(0);

            // 7. GUARDAR
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
            @RequestParam(value = "usuarioId") Long usuarioId,
            @RequestParam(value = "jornadaId", required = false) Long jornadaId,
            Model model,
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

            // Por ahora, usar el primer usuario si no se especifica
            // TODO: En el futuro, obtener de la sesión
            Usuario usuario;
            if (usuarioId == null) {
                List<Usuario> usuarios = usuarioRepository.findByLigaIdLigaCume(ligaId);
                if (usuarios.isEmpty()) {
                    model.addAttribute("mensaje", "No hay usuarios en esta liga");
                    model.addAttribute("ligaId", ligaId);
                    model.addAttribute("currentPage", "historial");
                    return "historialAlineaciones";
                }
                usuario = usuarios.get(0);
            } else {
                usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));
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

                for (Jugador jugador : alineacion.getJugadores()) {
                    // Obtener estadísticas del jugador para esta jornada
                    EstadisticasJornada stats = obtenerEstadisticasJugadorJornada(
                        jugador, jornadaSeleccionada.getIdJornada()
                    );

                    JugadorConEstadisticas jugadorConStats = new JugadorConEstadisticas(
                        jugador, stats
                    );

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
                model.addAttribute("puntosTotales", alineacion.getPuntosTotalesJornada());
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

