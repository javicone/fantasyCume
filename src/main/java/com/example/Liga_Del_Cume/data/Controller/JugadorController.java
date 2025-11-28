package com.example.Liga_Del_Cume.data.Controller;

import com.example.Liga_Del_Cume.data.model.EstadisticaJugadorPartido;
import com.example.Liga_Del_Cume.data.model.Jugador;
import com.example.Liga_Del_Cume.data.service.JugadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class JugadorController {

    @Autowired
    private JugadorService jugadorService;

    /**
     * Muestra la lista de estadísticas de jugadores con filtros
     */
    @GetMapping("/liga/{idLiga}/estadisticasJugadores")
    public String mostrarEstadisticasJugadores(
    @PathVariable Long idLiga,
    @RequestParam(required = false) String buscar,
    @RequestParam(required = false, defaultValue = "false") boolean mostrarPorteros,
    @RequestParam(required = false) String ordenar,
    Model model) {

        List<Jugador> jugadores;

        // Filtrar por tipo de jugador
        if (mostrarPorteros) {
            jugadores = jugadorService.listarPorteros();
        } else {
            jugadores = jugadorService.listarJugadoresDeCampo();
        }

        // Aplicar búsqueda por nombre
        if (buscar != null && !buscar.trim().isEmpty()) {
            String busquedaLower = buscar.toLowerCase();
            jugadores = jugadorService.buscarPorNombreAMedias(busquedaLower);

            // Aplicar ordenación
            if ("goles".equals(ordenar)) {
                jugadores = jugadorService.buscarJugadoresPorGolesDesc();
            } else if ("puntos".equals(ordenar)) {
                jugadores = jugadorService.buscarJugadoresOrdenadosPorPuntos();
            } else if ("precio".equals(ordenar)) {
                jugadores = jugadorService.buscarPorPrecioMayorAMenor();
            }

            // Mapa de puntos Y Mapa de Goles
            Map<Long, Integer> puntosTotalesMap = new HashMap<>();
            Map<Long, Integer> golesTotalesMap = new HashMap<>(); // <--- NUEVO MAPA

            for (Jugador jugador : jugadores) {
                puntosTotalesMap.put(jugador.getIdJugador(), calcularPuntosTotales(jugador));
                // Calculamos y guardamos los goles para enviarlos a la vista
                golesTotalesMap.put(jugador.getIdJugador(), calcularGolesTotal(jugador));
            }

            model.addAttribute("jugadores", jugadores);
            model.addAttribute("puntosTotalesMap", puntosTotalesMap);
            model.addAttribute("golesTotalesMap", golesTotalesMap); // <--- AÑADIR AL MODELO
            model.addAttribute("buscar", buscar);
            model.addAttribute("ordenar", ordenar);
            model.addAttribute("idLiga", idLiga);
        }
            return "estadisticasJugadores";

    }

    /**
     * Muestra el detalle de un jugador específico
     */
    @GetMapping("/liga/{idLiga}/jugador/{idJugador}")
    public String verDetalleJugador (
            @PathVariable Long idLiga,
            @PathVariable Long idJugador,
            Model model){

        Jugador jugador = jugadorService.obtenerJugador(idJugador);

        // Calcular estadísticas totales
        int golesTotal = calcularGolesTotal(jugador);
        int asistenciasTotal = calcularAsistenciasTotal(jugador);
        int tarjetasAmarillasTotal = calcularTarjetasAmarillasTotal(jugador);
        int tarjetasRojasTotal = calcularTarjetasRojasTotal(jugador);
        int golesRecibidosTotal = calcularGolesRecibidosTotal(jugador);
        int puntosTotal = calcularPuntosTotales(jugador);

        model.addAttribute("jugador", jugador);
        model.addAttribute("golesTotal", golesTotal);
        model.addAttribute("asistenciasTotal", asistenciasTotal);
        model.addAttribute("tarjetasAmarillasTotal", tarjetasAmarillasTotal);
        model.addAttribute("tarjetasRojasTotal", tarjetasRojasTotal);
        model.addAttribute("golesRecibidosTotal", golesRecibidosTotal);
        model.addAttribute("puntosTotal", puntosTotal);
        model.addAttribute("idLiga", idLiga);

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

