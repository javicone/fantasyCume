package com.example.Liga_Del_Cume.data.Controller;

import com.example.Liga_Del_Cume.data.model.*;
import com.example.Liga_Del_Cume.data.repository.*;
import com.example.Liga_Del_Cume.data.service.LigaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para gestionar la visualización de resultados de partidos
 * Funcionalidad 9: Ver Resultados
 */
@Controller
@RequestMapping("/liga/{ligaId}")
public class ResultadosController {

    @Autowired
    private JornadaRepository jornadaRepository;

    @Autowired
    private PartidoRepository partidoRepository;

    @Autowired
    private LigaService ligaService;

    /**
     * Muestra los resultados de los partidos de una jornada
     *
     * @param ligaId ID de la liga
     * @param jornadaId ID de la jornada (opcional, si no se proporciona usa la primera)
     * @param model Modelo para pasar datos a la vista
     * @return Vista de resultados
     */
    @GetMapping("/resultados")
    public String mostrarResultados(
            @PathVariable("ligaId") Long ligaId,
            @RequestParam(value = "jornadaId", required = false) Long jornadaId,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // Obtener todas las jornadas de la liga
            List<Jornada> jornadas = jornadaRepository.findByLigaIdLigaCume(ligaId);

            if (jornadas.isEmpty()) {
                model.addAttribute("mensaje", "No hay jornadas disponibles en esta liga");
                model.addAttribute("ligaId", ligaId);
                model.addAttribute("currentPage", "resultados");
                return "resultados";
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
            model.addAttribute("currentPage", "resultados");
            model.addAttribute("nombreLiga", nombreLiga);

            return "resultados";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al cargar los resultados: " + e.getMessage());
            return "redirect:/liga/" + ligaId + "/ranking";
        }
    }
}

