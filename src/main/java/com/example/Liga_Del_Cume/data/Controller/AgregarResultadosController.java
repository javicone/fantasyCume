package com.example.Liga_Del_Cume.data.Controller;

import com.example.Liga_Del_Cume.data.model.*;
import com.example.Liga_Del_Cume.data.repository.*;
import com.example.Liga_Del_Cume.data.service.LigaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador para agregar y editar resultados de partidos (Admin)
 */
@Controller
@RequestMapping("/liga/{ligaId}/admin")
public class AgregarResultadosController {

    @Autowired
    private JornadaRepository jornadaRepository;

    @Autowired
    private PartidoRepository partidoRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private EstadisticaJugadorPartidoRepository estadisticaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlineacionRepository alineacionRepository;

    @Autowired
    private LigaService ligaService;

    /**
     * GET: Mostrar página para agregar resultados
     * Muestra los partidos de una jornada seleccionada
     */
    @GetMapping("/agregar-resultados")
    public String mostrarAgregarResultados(
            @PathVariable("ligaId") Long ligaId,
            @RequestParam(value = "jornadaId", required = false) Long jornadaId,
            Model model,
            HttpSession session) {

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");

            // Obtener todas las jornadas de la liga
            List<Jornada> jornadas = jornadaRepository.findByLigaIdLigaCume(ligaId);

            if (jornadas.isEmpty()) {
                model.addAttribute("error", "No hay jornadas disponibles en esta liga");
                model.addAttribute("ligaId", ligaId);
                model.addAttribute("currentPage", "agregarResultados");
                return "agregarResultados";
            }

            // Si no se especifica jornada, usar la primera
            Jornada jornadaSeleccionada;
            if (jornadaId == null) {
                jornadaSeleccionada = jornadas.get(0);
            } else {
                jornadaSeleccionada = jornadaRepository.findById(jornadaId)
                    .orElse(jornadas.get(0));
            }

            // Obtener partidos de la jornada seleccionada
            List<Partido> partidos = partidoRepository.findByJornadaIdJornada(jornadaSeleccionada.getIdJornada());

            // Obtener el nombre de la liga
            LigaCume ligaObj = ligaService.obtenerLigaPorId(ligaId);
            String nombreLiga = ligaObj != null ? ligaObj.getNombreLiga() : "Mis Ligas";

            // Pasar datos al modelo
            model.addAttribute("jornadas", jornadas);
            model.addAttribute("jornadaSeleccionada", jornadaSeleccionada);
            model.addAttribute("partidos", partidos);
            model.addAttribute("ligaId", ligaId);
            model.addAttribute("usuario", usuario);
            model.addAttribute("currentPage", "agregarResultados");
            model.addAttribute("nombreLiga", nombreLiga);

            return "agregarResultados";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar la página: " + e.getMessage());
            model.addAttribute("ligaId", ligaId);
            return "agregarResultados";
        }
    }

    /**
     * GET: Mostrar página de edición de partido
     */
    @GetMapping("/partido/{partidoId}/editar")
    public String mostrarEditarPartido(
            @PathVariable("ligaId") Long ligaId,
            @PathVariable("partidoId") Long partidoId,
            Model model,
            HttpSession session) {

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");

            Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

            model.addAttribute("partido", partido);
            model.addAttribute("ligaId", ligaId);
            model.addAttribute("usuario", usuario);
            model.addAttribute("currentPage", "agregarResultados");

            return "editarPartido";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/liga/" + ligaId + "/admin/agregar-resultados";
        }
    }

    /**
     * GET: Obtener detalles de un partido para editar
     * Retorna JSON con los jugadores de ambos equipos y sus estadísticas
     */
    @GetMapping("/partido/{partidoId}/detalles")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerDetallesPartido(
            @PathVariable("ligaId") Long ligaId,
            @PathVariable("partidoId") Long partidoId) {

        try {
            Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

            Map<String, Object> response = new HashMap<>();

            // Información del partido
            response.put("partidoId", partido.getIdPartido());
            response.put("equipoLocal", partido.getEquipoLocal().getNombreEquipo());
            response.put("equipoVisitante", partido.getEquipoVisitante().getNombreEquipo());
            response.put("golesLocal", partido.getGolesLocal());
            response.put("golesVisitante", partido.getGolesVisitante());

            // Jugadores del equipo local
            List<Jugador> jugadoresLocal = jugadorRepository.findByEquipoIdEquipo(
                partido.getEquipoLocal().getIdEquipo()
            );
            List<Map<String, Object>> jugadoresLocalData = new ArrayList<>();

            for (Jugador jugador : jugadoresLocal) {
                Map<String, Object> jugadorData = new HashMap<>();
                jugadorData.put("idJugador", jugador.getIdJugador());
                jugadorData.put("nombre", jugador.getNombreJugador());
                jugadorData.put("avatarUrl", jugador.getAvatarUrl());
                jugadorData.put("esPortero", jugador.isEsPortero());

                // Obtener estadísticas si existen
                EstadisticaJugadorPartido estadistica = estadisticaRepository
                    .findByJugadorIdJugadorAndPartidoIdPartido(jugador.getIdJugador(), partidoId);

                if (estadistica != null) {
                    jugadorData.put("goles", estadistica.getGolesAnotados());
                    jugadorData.put("asistencias", estadistica.getAsistencias());
                    jugadorData.put("tarjetasAmarillas", estadistica.getTarjetaAmarillas());
                    jugadorData.put("tarjetaRoja", estadistica.isTarjetaRojas());
                    jugadorData.put("minMinutosJugados", estadistica.isMinMinutosJugados());
                    jugadorData.put("golesRecibidos", estadistica.getGolesRecibidos());
                    jugadorData.put("puntosJornada", estadistica.getPuntosJornada());
                } else {
                    jugadorData.put("goles", 0);
                    jugadorData.put("asistencias", 0);
                    jugadorData.put("tarjetasAmarillas", 0);
                    jugadorData.put("tarjetaRoja", false);
                    jugadorData.put("minMinutosJugados", false);
                    jugadorData.put("golesRecibidos", 0);
                    jugadorData.put("puntosJornada", 0);
                }

                jugadoresLocalData.add(jugadorData);
            }

            // Jugadores del equipo visitante
            List<Jugador> jugadoresVisitante = jugadorRepository.findByEquipoIdEquipo(
                partido.getEquipoVisitante().getIdEquipo()
            );
            List<Map<String, Object>> jugadoresVisitanteData = new ArrayList<>();

            for (Jugador jugador : jugadoresVisitante) {
                Map<String, Object> jugadorData = new HashMap<>();
                jugadorData.put("idJugador", jugador.getIdJugador());
                jugadorData.put("nombre", jugador.getNombreJugador());
                jugadorData.put("avatarUrl", jugador.getAvatarUrl());
                jugadorData.put("esPortero", jugador.isEsPortero());

                // Obtener estadísticas si existen
                EstadisticaJugadorPartido estadistica = estadisticaRepository
                    .findByJugadorIdJugadorAndPartidoIdPartido(jugador.getIdJugador(), partidoId);

                if (estadistica != null) {
                    jugadorData.put("goles", estadistica.getGolesAnotados());
                    jugadorData.put("asistencias", estadistica.getAsistencias());
                    jugadorData.put("tarjetasAmarillas", estadistica.getTarjetaAmarillas());
                    jugadorData.put("tarjetaRoja", estadistica.isTarjetaRojas());
                    jugadorData.put("minMinutosJugados", estadistica.isMinMinutosJugados());
                    jugadorData.put("golesRecibidos", estadistica.getGolesRecibidos());
                    jugadorData.put("puntosJornada", estadistica.getPuntosJornada());
                } else {
                    jugadorData.put("goles", 0);
                    jugadorData.put("asistencias", 0);
                    jugadorData.put("tarjetasAmarillas", 0);
                    jugadorData.put("tarjetaRoja", false);
                    jugadorData.put("minMinutosJugados", false);
                    jugadorData.put("golesRecibidos", 0);
                    jugadorData.put("puntosJornada", 0);
                }

                jugadoresVisitanteData.add(jugadorData);
            }

            response.put("jugadoresLocal", jugadoresLocalData);
            response.put("jugadoresVisitante", jugadoresVisitanteData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * POST: Guardar estadísticas de un partido
     * Actualiza las estadísticas de todos los jugadores y recalcula puntos
     */
    @PostMapping("/partido/{partidoId}/guardar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarEstadisticasPartido(
            @PathVariable("ligaId") Long ligaId,
            @PathVariable("partidoId") Long partidoId,
            @RequestBody Map<String, Object> datos) {

        try {
            Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

            // Obtener jugadores del request
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> jugadores = (List<Map<String, Object>>) datos.get("jugadores");

            int golesLocal = 0;
            int golesVisitante = 0;

            // Procesar cada jugador
            for (Map<String, Object> jugadorData : jugadores) {
                Long idJugador = Long.valueOf(jugadorData.get("idJugador").toString());
                int goles = Integer.parseInt(jugadorData.get("goles").toString());
                int asistencias = Integer.parseInt(jugadorData.get("asistencias").toString());
                int tarjetasAmarillas = Integer.parseInt(jugadorData.get("tarjetasAmarillas").toString());
                boolean tarjetaRoja = Boolean.parseBoolean(jugadorData.get("tarjetaRoja").toString());
                boolean minMinutosJugados = Boolean.parseBoolean(jugadorData.get("minMinutosJugados").toString());
                int golesRecibidos = Integer.parseInt(jugadorData.get("golesRecibidos").toString());

                Jugador jugador = jugadorRepository.findById(idJugador)
                    .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

                // Calcular puntos de fantasy
                int puntosJornada = calcularPuntosFantasy(jugador, goles, asistencias,
                    tarjetasAmarillas, tarjetaRoja, minMinutosJugados, golesRecibidos);

                // Sumar goles al marcador
                if (jugador.getEquipo().getIdEquipo().equals(partido.getEquipoLocal().getIdEquipo())) {
                    golesLocal += goles;
                } else {
                    golesVisitante += goles;
                }

                // Guardar o actualizar estadística
                EstadisticaJugadorPartido estadistica = estadisticaRepository
                    .findByJugadorIdJugadorAndPartidoIdPartido(idJugador, partidoId);

                int puntosAnteriores = 0;
                boolean esActualizacion = false;

                if (estadistica == null) {
                    estadistica = new EstadisticaJugadorPartido();
                    estadistica.setJugador(jugador);
                    estadistica.setPartido(partido);
                } else {
                    // Si la estadística ya existía, guardamos los puntos anteriores para ajustar el precio
                    puntosAnteriores = estadistica.getPuntosJornada();
                    esActualizacion = true;
                }

                estadistica.setGolesAnotados(goles);
                estadistica.setAsistencias(asistencias);
                estadistica.setTarjetaAmarillas(tarjetasAmarillas);
                estadistica.setTarjetaRojas(tarjetaRoja);
                estadistica.setMinMinutosJugados(minMinutosJugados);
                estadistica.setGolesRecibidos(golesRecibidos);
                estadistica.setPuntosJornada(puntosJornada);

                estadisticaRepository.save(estadistica);

                // Actualizar precio del jugador basado en los puntos obtenidos
                if (esActualizacion) {
                    // Si es actualización, ajustar la diferencia
                    int diferenciaPuntos = puntosJornada - puntosAnteriores;
                    actualizarPrecioJugador(jugador, diferenciaPuntos);
                } else {
                    // Si es nueva estadística, aplicar los puntos completos
                    actualizarPrecioJugador(jugador, puntosJornada);
                }
            }

            // Actualizar marcador del partido
            partido.setGolesLocal(golesLocal);
            partido.setGolesVisitante(golesVisitante);
            partidoRepository.save(partido);

            // Actualizar goles recibidos de los porteros
            actualizarGolesRecibidosPorteros(partido, golesLocal, golesVisitante);

            // NUEVO: Mover alineaciones futuras a esta jornada (historial)
            moverAlineacionesFuturasAHistorial(partido.getJornada());

            // Recalcular puntos de usuarios
            recalcularPuntosUsuarios(partido.getJornada().getIdJornada());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estadísticas guardadas correctamente");
            response.put("golesLocal", golesLocal);
            response.put("golesVisitante", golesVisitante);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Calcula los puntos de fantasy de un jugador según sus estadísticas
     */
    private int calcularPuntosFantasy(Jugador jugador, int goles, int asistencias,
                                      int tarjetasAmarillas, boolean tarjetaRoja,
                                      boolean minMinutosJugados, int golesRecibidos) {
        int puntos = 0;

        // Puntos base por jugar mínimo de minutos
        if (minMinutosJugados) {
            puntos += 1;
        }

        if (jugador.isEsPortero()) {
            // Puntos para porteros
            puntos += goles * 6; // Gol de portero
            puntos += asistencias * 3;

            // Penalización por goles recibidos
            if (golesRecibidos == 0 && minMinutosJugados) {
                puntos += 5; // Portería a cero
            } else if (golesRecibidos >= 1 && golesRecibidos <= 2) {
                puntos -= 1;
            } else if (golesRecibidos >= 3) {
                puntos -= 2;
            }
        } else {
            // Puntos para jugadores de campo
            puntos += goles * 4; // Gol normal
            puntos += asistencias * 3;
        }

        // Penalizaciones por tarjetas
        puntos -= tarjetasAmarillas;
        if (tarjetaRoja) {
            puntos -= 3;
        }

        return puntos;
    }

    /**
     * Actualiza los goles recibidos de los porteros automáticamente
     */
    private void actualizarGolesRecibidosPorteros(Partido partido, int golesLocal, int golesVisitante) {
        // Porteros del equipo local reciben los goles visitantes
        List<Jugador> porterosLocal = jugadorRepository.findByEquipoIdEquipo(
            partido.getEquipoLocal().getIdEquipo()
        ).stream().filter(Jugador::isEsPortero).collect(Collectors.toList());

        for (Jugador portero : porterosLocal) {
            EstadisticaJugadorPartido estadistica = estadisticaRepository
                .findByJugadorIdJugadorAndPartidoIdPartido(portero.getIdJugador(), partido.getIdPartido());

            if (estadistica != null) {
                estadistica.setGolesRecibidos(golesVisitante);
                estadisticaRepository.save(estadistica);
            }
        }

        // Porteros del equipo visitante reciben los goles locales
        List<Jugador> porterosVisitante = jugadorRepository.findByEquipoIdEquipo(
            partido.getEquipoVisitante().getIdEquipo()
        ).stream().filter(Jugador::isEsPortero).collect(Collectors.toList());

        for (Jugador portero : porterosVisitante) {
            EstadisticaJugadorPartido estadistica = estadisticaRepository
                .findByJugadorIdJugadorAndPartidoIdPartido(portero.getIdJugador(), partido.getIdPartido());

            if (estadistica != null) {
                estadistica.setGolesRecibidos(golesLocal);
                estadisticaRepository.save(estadistica);
            }
        }
    }

    /**
     * Recalcula los puntos de todos los usuarios basándose en sus alineaciones
     */
    private void recalcularPuntosUsuarios(Long jornadaId) {
        // Obtener todas las alineaciones de la jornada
        List<Alineacion> alineaciones = alineacionRepository.findByJornadaIdJornada(jornadaId);

        for (Alineacion alineacion : alineaciones) {
            int puntosTotal = 0;

            // Sumar puntos de todos los jugadores de la alineación
            for (Jugador jugador : alineacion.getJugadores()) {
                EstadisticaJugadorPartido estadistica = estadisticaRepository
                    .findByJugadorIdJugadorAndPartidoJornadaIdJornada(jugador.getIdJugador(), jornadaId);

                if (estadistica != null) {
                    puntosTotal += estadistica.getPuntosJornada();
                }
            }

            // Actualizar puntos del usuario
            Usuario usuario = alineacion.getUsuario();
            usuario.setPuntosAcumulados(usuario.getPuntosAcumulados() + puntosTotal);
            usuarioRepository.save(usuario);
        }
    }

    /**
     * Mueve las alineaciones "futuras" (sin jornada asignada o de jornadas futuras)
     * al historial de la jornada que acaba de jugarse.
     *
     * Este método se ejecuta cuando se agregan resultados a un partido por primera vez.
     *
     * Lógica:
     * 1. Si es la primera vez que se juega un partido de esta jornada, todas las alineaciones
     *    que los usuarios tenían guardadas para "alineación futura" deben moverse a esta jornada.
     * 2. Esto asegura que las alineaciones se guarden en el historial automáticamente.
     *
     * @param jornada La jornada que acaba de jugarse
     */
    private void moverAlineacionesFuturasAHistorial(Jornada jornada) {
        try {
            Long jornadaId = jornada.getIdJornada();
            Long ligaId = jornada.getLiga().getIdLigaCume();

            // Verificar si es la primera vez que se agregan resultados a esta jornada
            // (es decir, si esta jornada ya tiene alineaciones guardadas, no hacemos nada)
            List<Alineacion> alineacionesExistentes = alineacionRepository.findByJornadaIdJornada(jornadaId);

            if (!alineacionesExistentes.isEmpty()) {
                // Ya hay alineaciones guardadas para esta jornada, no hacer nada
                return;
            }

            // Obtener la próxima jornada sin resultados (que ahora ES esta jornada que se está jugando)
            List<Jornada> todasJornadas = jornadaRepository.findByLigaIdLigaCume(ligaId);
            todasJornadas.sort((j1, j2) -> j1.getNumeroJornada().compareTo(j2.getNumeroJornada()));

            // Encontrar la posición de la jornada actual
            int posicionJornadaActual = -1;
            for (int i = 0; i < todasJornadas.size(); i++) {
                if (todasJornadas.get(i).getIdJornada().equals(jornadaId)) {
                    posicionJornadaActual = i;
                    break;
                }
            }

            if (posicionJornadaActual == -1) {
                return; // No se encontró la jornada, algo raro pasó
            }

            // Buscar alineaciones que tengan una jornada "futura" (mayor número de jornada)
            // o que no tengan jornada asignada
            List<Usuario> usuariosDeLiga = usuarioRepository.findByLigaIdLigaCume(ligaId);

            for (Usuario usuario : usuariosDeLiga) {
                // Buscar si el usuario tiene una alineación en una jornada futura
                // o en una jornada sin resultados
                Alineacion alineacionFutura = null;

                // Buscar la alineación más cercana que el usuario tenga para jornadas futuras
                for (int i = posicionJornadaActual; i < todasJornadas.size(); i++) {
                    Jornada jornadaBusqueda = todasJornadas.get(i);
                    Optional<Alineacion> alineacionOpt = alineacionRepository
                        .findByUsuarioIdUsuarioAndJornadaIdJornada(
                            usuario.getIdUsuario(),
                            jornadaBusqueda.getIdJornada()
                        );

                    if (alineacionOpt.isPresent()) {
                        alineacionFutura = alineacionOpt.get();
                        break; // Encontramos la primera alineación futura
                    }
                }

                // Si encontramos una alineación futura, moverla a esta jornada
                if (alineacionFutura != null && !alineacionFutura.getJornada().getIdJornada().equals(jornadaId)) {
                    // Cambiar la jornada de la alineación
                    alineacionFutura.setJornada(jornada);
                    alineacionRepository.save(alineacionFutura);
                }
            }

        } catch (Exception e) {
            // Log del error pero no interrumpir el flujo principal
            System.err.println("Error al mover alineaciones futuras: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Actualiza el precio de mercado de un jugador basándose en los puntos obtenidos en un partido.
     *
     * Lógica de actualización de precios:
     * - Si los puntos son positivos: se suman (puntos * 1000) al precio actual
     * - Si los puntos son negativos: se restan (|puntos| * 1000) del precio actual
     * - El precio nunca puede ser menor que 0
     *
     * Ejemplos:
     * - Jugador con precio 5000, obtiene 3 puntos: nuevo precio = 5000 + (3 * 1000) = 8000
     * - Jugador con precio 5000, obtiene -2 puntos: nuevo precio = 5000 - (2 * 1000) = 3000
     * - Jugador con precio 1000, obtiene -3 puntos: nuevo precio = max(0, 1000 - 3000) = 0
     *
     * @param jugador Jugador cuyo precio se actualizará
     * @param puntosJornada Puntos obtenidos en la jornada (pueden ser positivos o negativos)
     */
    private void actualizarPrecioJugador(Jugador jugador, int puntosJornada) {
        if (jugador == null) {
            return;
        }

        float precioActual = jugador.getPrecioMercado();
        float cambio = puntosJornada * 1000.0f;
        float nuevoPrecio = precioActual + cambio;

        // Asegurar que el precio nunca sea negativo
        if (nuevoPrecio < 0) {
            nuevoPrecio = 0;
        }

        jugador.setPrecioMercado(nuevoPrecio);
        jugadorRepository.save(jugador);
    }
}

