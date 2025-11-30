package com.example.Liga_Del_Cume.data.Controller;

import com.example.Liga_Del_Cume.data.model.Equipo;
import com.example.Liga_Del_Cume.data.model.Jornada;
import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.service.EquipoService;
import com.example.Liga_Del_Cume.data.service.JornadaService;
import com.example.Liga_Del_Cume.data.service.PartidoService;
import com.example.Liga_Del_Cume.data.service.LigaService;
import com.example.Liga_Del_Cume.data.service.EstadisticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para operaciones relacionadas con jornadas (calendario).
 * Proporciona endpoints para generar automáticamente las jornadas de una liga
 * creando un calendario round-robin (todos contra todos).
 */
@Controller
public class JornadaController {

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private JornadaService jornadaService;

    @Autowired
    private PartidoService partidoService;

    @Autowired
    private LigaService ligaService;

    @Autowired
    private EstadisticaService estadisticaService;

    public JornadaController() {
        // constructor
    }

    /**
     * Generar jornadas para una liga (round-robin).
     * POST /jornada/generar?ligaId={id}&force={true|false}
     * Si existe ya al menos una jornada y force != true, se aborta para evitar duplicados.
     */
    @PostMapping("/jornada/generar")
    public String generarJornadas(@RequestParam Long ligaId,
                                  @RequestParam(required = false, defaultValue = "false") boolean force,
                                  RedirectAttributes redirectAttributes) {
        // Validaciones básicas
        if (ligaId == null || ligaId <= 0) {
            redirectAttributes.addFlashAttribute("message", "ID de liga inválido");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/liga";
        }

        // Obtener liga (lanza excepción si no existe)
        LigaCume liga;
        try {
            liga = ligaService.buscarLigaPorId(ligaId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Liga no encontrada: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/liga";
        }

        // Si ya existen jornadas y no se fuerza, abortar
        int jornadasExistentes = jornadaService.contarJornadasPorLiga(ligaId);
        if (jornadasExistentes > 0 && !force) {
            redirectAttributes.addFlashAttribute("message", "La liga ya tiene jornadas. Si quieres resetear estadísticas y resultados, confirma en el diálogo (Generar Jornadas).");
            redirectAttributes.addFlashAttribute("messageType", "warning");
            return "redirect:/liga/" + ligaId + "/clasificacion";
        }

        // Si force = true pero ya hay jornadas, SOLO reseteamos estadísticas y resultados (no eliminar jornadas)
        if (jornadasExistentes > 0 && force) {
            try {
                estadisticaService.resetEstadisticasYResultadosDeLiga(ligaId);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "Error al resetear estadísticas: " + e.getMessage());
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/liga/" + ligaId + "/clasificacion";
            }

            redirectAttributes.addFlashAttribute("message", "Estadísticas y resultados reseteados correctamente (jugadores y equipos preservados). Si deseas regenerar el calendario, borra las jornadas manualmente o usa la opción específica.");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/liga/" + ligaId + "/clasificacion";
        }

        // Si no hay jornadas, seguimos con la generación normal (creación de jornadas y partidos)
        // Obtener equipos
        List<Equipo> equipos = equipoService.listarEquiposPorLiga(ligaId);
        if (equipos == null || equipos.size() < 2) {
            redirectAttributes.addFlashAttribute("message", "Necesitas al menos 2 equipos para generar jornadas");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/liga/" + ligaId + "/clasificacion";
        }

        // Preparar lista; si impar añadimos bye (null)
        List<Equipo> lista = new ArrayList<>(equipos);
        if (lista.size() % 2 == 1) {
            lista.add(null);
        }

        int n = lista.size();
        int rondas = n - 1;
        int partidosPorRonda = n / 2;

        // Algoritmo round-robin (círculo)
        List<Equipo> rotantes = new ArrayList<>(lista);

        for (int ronda = 0; ronda < rondas; ronda++) {
            Jornada jornada = jornadaService.crearJornada(liga);

            for (int i = 0; i < partidosPorRonda; i++) {
                Equipo equipoLocal = rotantes.get(i);
                Equipo equipoVisitante = rotantes.get(n - 1 - i);

                if (equipoLocal == null || equipoVisitante == null) continue; // bye

                // Crear partido con 0-0 inicial (sin resultado)
                partidoService.agregarPartido(equipoLocal, equipoVisitante, 0, 0, jornada);
            }

            // Rotación: tomar el último y colocarlo en la posición 1
            Equipo ultimo = rotantes.remove(rotantes.size() - 1);
            rotantes.add(1, ultimo);
        }

        redirectAttributes.addFlashAttribute("message", "Jornadas generadas correctamente");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/liga/" + ligaId + "/clasificacion";
    }

    /**
     * Endpoint GET para facilitar la invocación desde navegador (redirige a POST).
     */
    @GetMapping("/jornada/generar")
    public String generarJornadasGet(@RequestParam Long ligaId, RedirectAttributes redirectAttributes) {
        // redirigir al POST sin force
        return generarJornadas(ligaId, false, redirectAttributes);
    }

    /**
     * Ruta compatible con el menú: /generar-cuadros-jornadas
     * Acepta un parámetro opcional ligaId. Si no se proporciona, redirige a /liga
     * para que el usuario seleccione la liga.
     */
    @GetMapping(path = "/generar-cuadros-jornadas")
    public String generarCuadrosDesdeMenu(@RequestParam(required = false) Long ligaId,
                                          @RequestParam(required = false, defaultValue = "false") boolean force,
                                          RedirectAttributes redirectAttributes) {
        if (ligaId == null) {
            redirectAttributes.addFlashAttribute("message", "Selecciona primero una liga para generar sus jornadas.");
            redirectAttributes.addFlashAttribute("messageType", "warning");
            return "redirect:/liga";
        }
        // Llamar al generador pasando el parámetro force segun venga
        return generarJornadas(ligaId, force, redirectAttributes);
    }
}
