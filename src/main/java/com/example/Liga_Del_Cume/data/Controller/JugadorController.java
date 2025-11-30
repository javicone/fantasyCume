package com.example.Liga_Del_Cume.data.Controller;

import com.example.Liga_Del_Cume.data.model.EstadisticaJugadorPartido;
import com.example.Liga_Del_Cume.data.model.Jugador;
import com.example.Liga_Del_Cume.data.model.Usuario;
import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.service.JugadorService;
import com.example.Liga_Del_Cume.data.service.EstadisticaService;
import com.example.Liga_Del_Cume.data.service.LigaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Controller
public class JugadorController {

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private EstadisticaService estadisticaService;

    @Autowired
    private LigaService ligaService;

    /**
     * Muestra la lista de estadísticas de jugadores con filtros
     */
    @GetMapping("/liga/{idLiga}/estadisticasJugadores")
    public String mostrarEstadisticasJugadores(
            @PathVariable Long idLiga,
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false, defaultValue = "false") boolean mostrarPorteros,
            @RequestParam(required = false) String ordenar,
            HttpSession session,
            Model model) {

        // Obtener usuario de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        List<Jugador> jugadores;
        jugadores = jugadorService.listarTodosLosJugadores();
        // 4. CÁLCULO DE MAPAS (Esta lógica está correcta)
        Map<Long, Integer> puntosTotalesMap = jugadores.stream()
                .collect(Collectors.toMap(Jugador::getIdJugador, this::calcularPuntosTotales));
        Map<Long, Integer> golesTotalesMap = jugadores.stream()
                .collect(Collectors.toMap(Jugador::getIdJugador, this::calcularGolesTotal));

        // Obtener el nombre de la liga
        LigaCume ligaObj = ligaService.obtenerLigaPorId(idLiga);
        String nombreLiga = ligaObj != null ? ligaObj.getNombreLiga() : "Mis Ligas";

        // 5. AÑADIR AL MODELO (Esto es correcto)
        model.addAttribute("jugadores", jugadores);
        model.addAttribute("puntosTotalesMap", puntosTotalesMap);
        model.addAttribute("golesTotalesMap", golesTotalesMap);
        model.addAttribute("buscar", buscar);
        model.addAttribute("ordenar", ordenar);
        model.addAttribute("ligaId", idLiga);
        model.addAttribute("usuario", usuario);
        model.addAttribute("nombreLiga", nombreLiga);
        model.addAttribute("currentPage", "estadisticas");

        // Es importante enviar el valor de 'mostrarPorteros' al modelo
        // La RequestParam ya lo tiene, pero para claridad en Thymeleaf lo añadimos.
        model.addAttribute("mostrarPorteros", mostrarPorteros);

        return "estadisticasJugadores";
    }


    /**
     * Muestra el detalle de un jugador específico
     */
    @GetMapping("/liga/{idLiga}/jugador/{idJugador}/usuario/{idUsuario}")
    public String verDetalleJugador(
            @PathVariable Long idLiga,
            @PathVariable Long idJugador,
            @RequestParam Long idUsuario,
            Model model) {

        model.addAttribute("idUsuario", idUsuario);

        Jugador jugador = jugadorService.obtenerJugador(idJugador);
        List<EstadisticaJugadorPartido> estadisticas = estadisticaService.obtenerEstadisticasJugador(idJugador);
        // IMPORTANTE: Ordenar las estadísticas por número de jornada para la gráfica
        // Asumiendo que EstadisticaJugadorPartido tiene -> getPartido() -> getJornada() -> getNumeroJornada()

        model.addAttribute("jugador", jugador);
        // Pasamos la lista completa en lugar de los totales sueltos
        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("idLiga", idLiga);

        // Calcular estadísticas totales
        int golesTotal = calcularGolesTotal(jugador);
        int asistenciasTotal = calcularAsistenciasTotal(jugador);
        int tarjetasAmarillasTotal = calcularTarjetasAmarillasTotal(jugador);
        int tarjetasRojasTotal = calcularTarjetasRojasTotal(jugador);
        int golesRecibidosTotal = calcularGolesRecibidosTotal(jugador);
        int puntosTotal = calcularPuntosTotales(jugador);


        model.addAttribute("golesTotal", golesTotal);
        model.addAttribute("asistenciasTotal", asistenciasTotal);
        model.addAttribute("tarjetasAmarillasTotal", tarjetasAmarillasTotal);
        model.addAttribute("tarjetasRojasTotal", tarjetasRojasTotal);
        model.addAttribute("golesRecibidosTotal", golesRecibidosTotal);
        model.addAttribute("puntosTotal", puntosTotal);

        return "detalleJugador";
    }


    // Métodos auxiliares para calcular estadísticas totales
    private int calcularGolesTotal(Jugador jugador) {
        return jugador.getEstadisticas().stream()
                .mapToInt(EstadisticaJugadorPartido::getGolesAnotados)
                .sum();
    }

    private int calcularAsistenciasTotal(Jugador jugador) {
        return jugador.getEstadisticas().stream()
                .mapToInt(EstadisticaJugadorPartido::getAsistencias)
                .sum();
    }

    private int calcularTarjetasAmarillasTotal(Jugador jugador) {
        return jugador.getEstadisticas().stream()
                .mapToInt(EstadisticaJugadorPartido::getTarjetaAmarillas)
                .sum();
    }

    private int calcularTarjetasRojasTotal(Jugador jugador) {
        return (int) jugador.getEstadisticas().stream()
                .filter(EstadisticaJugadorPartido::isTarjetaRojas)
                .count();
    }

    private int calcularGolesRecibidosTotal(Jugador jugador) {
        return jugador.getEstadisticas().stream()
                .mapToInt(EstadisticaJugadorPartido::getGolesRecibidos)
                .sum();
    }

    private int calcularPuntosTotales(Jugador jugador) {
        return jugador.getEstadisticas().stream()
                .mapToInt(EstadisticaJugadorPartido::getPuntosJornada)
                .sum();
    }
}

