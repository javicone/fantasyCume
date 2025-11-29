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

            // Calcular próxima jornada (última + 1)
            Long proximaJornadaNumero = jornadas.isEmpty() ? 1L : (long) (jornadas.size() + 1);

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

            // Obtener presupuesto de la liga
            LigaCume liga = jornadaRepository.findById(ligaId).map(Jornada::getLiga)
                    .orElseGet(() -> usuarioRepository.findById(usuario.getIdUsuario())
                            .map(Usuario::getLiga).orElse(null));

            Long presupuestoMaximo =  100000000L;

            // Pasar datos al modelo
            model.addAttribute("proximaJornada", proximaJornadaNumero);
            model.addAttribute("presupuestoDisponible", presupuestoMaximo);
            model.addAttribute("presupuestoTotal", presupuestoMaximo);
            model.addAttribute("usuario", usuario);
            model.addAttribute("ligaId", ligaId);
            model.addAttribute("currentPage", "alineacion");

            return "alineacionFutura";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al cargar alineación futura: " + e.getMessage());
            return "redirect:/liga/" + ligaId + "/ranking";
        }
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

            // 3. LOGICA DE JORNADA (Mantenemos tu lógica original)
            List<Jornada> jornadas = jornadaRepository.findByLigaIdLigaCume(ligaId);
            Jornada proximaJornada = null;
            boolean jornadaFuturaExiste = false;

            for (Jornada j : jornadas) {
                // Buscamos una jornada que aún no tenga partidos (asumimos que es la futura)
                if (j.getPartidos().isEmpty()) {
                    proximaJornada = j;
                    jornadaFuturaExiste = true;
                    break;
                }
            }

            if (!jornadaFuturaExiste) {
                // Crear nueva jornada vinculada a la liga del usuario
                proximaJornada = new Jornada();
                proximaJornada.setLiga(usuario.getLiga());
                proximaJornada = jornadaRepository.save(proximaJornada);
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
                    "Alineación guardada correctamente para la jornada " + proximaJornada.getIdJornada());

            // Redirigimos pasando el usuarioId para que la vista GET lo reconozca si lo necesita,
            // aunque idealmente el GET también debería usar la sesión.
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

            // Pasar datos al modelo
            model.addAttribute("jornadas", jornadas);
            model.addAttribute("jornadaSeleccionada", jornadaSeleccionada);
            model.addAttribute("usuario", usuario);
            model.addAttribute("ligaId", ligaId);
            model.addAttribute("currentPage", "historial");

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

