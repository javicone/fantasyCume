package com.example.Liga_Del_Cume.data.Controller;

import com.example.Liga_Del_Cume.data.model.Equipo;
import com.example.Liga_Del_Cume.data.model.Jornada;
import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.service.EquipoService;
import com.example.Liga_Del_Cume.data.service.JornadaService;
import com.example.Liga_Del_Cume.data.service.PartidoService;
import com.example.Liga_Del_Cume.data.service.LigaService;
import com.example.Liga_Del_Cume.data.service.EstadisticaService;
import com.example.Liga_Del_Cume.data.repository.AlineacionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controlador para operaciones relacionadas con jornadas (calendario).
 * Proporciona endpoints para generar automáticamente las jornadas de una liga
 * creando un calendario round-robin (todos contra todos).
 */
@Controller
public class JornadaController {

    private static final Logger log = LoggerFactory.getLogger(JornadaController.class);

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

    @Autowired
    private AlineacionRepository alineacionRepository;

    public JornadaController() {
        // constructor
    }

    /**
     * Generar jornadas para una liga (round-robin).
     * POST /jornada/generar?ligaId={id}&force={true|false}
     * Si existe ya al menos una jornada y force != true, se aborta para evitar duplicados.
     */
    @RequestMapping(path = "/jornada/generar", method = {RequestMethod.GET, RequestMethod.POST})
    @Transactional
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

        // Comprobar si ya existen jornadas
        int jornadasExistentes = jornadaService.contarJornadasPorLiga(ligaId);
        if (jornadasExistentes > 0) {
            if (!force) {
                redirectAttributes.addFlashAttribute("message", "La liga ya tiene jornadas. Si quieres resetear estadísticas y resultados, confirma en el diálogo (Generar Jornadas).");
                redirectAttributes.addFlashAttribute("messageType", "warning");
                return "redirect:/liga/" + ligaId + "/clasificacion";
            }

            // force == true -> reseteamos estadísticas y resultados (no eliminar jornadas)
            try {
                estadisticaService.resetEstadisticasYResultadosDeLiga(ligaId);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "Error al resetear estadísticas: " + e.getMessage());
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/liga/" + ligaId + "/clasificacion";
            }

            // BORRAR alineaciones de todas las jornadas de la liga
            try {
                List<Jornada> existentes = jornadaService.listarJornadasPorLiga(ligaId);
                for (Jornada j : existentes) {
                    // borrar alineaciones de la jornada
                    List<com.example.Liga_Del_Cume.data.model.Alineacion> alineaciones = alineacionRepository.findByJornadaIdJornada(j.getIdJornada());
                    if (alineaciones != null && !alineaciones.isEmpty()) {
                        alineacionRepository.deleteAll(alineaciones);
                    }
                }
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "Error al borrar alineaciones: " + e.getMessage());
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/liga/" + ligaId + "/clasificacion";
            }

            // Ahora intentamos eliminar las jornadas existentes (y sus partidos)
            try {
                List<Jornada> existentes = jornadaService.listarJornadasPorLiga(ligaId);
                for (Jornada j : existentes) {
                    jornadaService.eliminarJornada(j.getIdJornada());
                }
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "No se pueden eliminar las jornadas existentes: " + e.getMessage());
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/liga/" + ligaId + "/clasificacion";
            }

            // seguimos adelante: al haber eliminado jornadas, procedemos a generar de nuevo (no retornamos aquí)
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

        // Construir emparejamientos para cada ronda (primera vuelta)
        List<List<Equipo[]>> rondasEmparejamientos = new ArrayList<>();
        List<Equipo> rotantes = new ArrayList<>(lista);
        for (int r = 0; r < rondas; r++) {
            List<Equipo[]> emparejamientos = new ArrayList<>();
            for (int i = 0; i < partidosPorRonda; i++) {
                Equipo local = rotantes.get(i);
                Equipo visitante = rotantes.get(n - 1 - i);
                if (local == null || visitante == null) continue; // bye
                emparejamientos.add(new Equipo[]{local, visitante});
            }
            rondasEmparejamientos.add(emparejamientos);
            // rotar sublista manteniendo índice 0 fijo
            if (rotantes.size() > 1) Collections.rotate(rotantes.subList(1, rotantes.size()), 1);
        }

        // Crear jornadas para la primera vuelta
        for (List<Equipo[]> emparejamientos : rondasEmparejamientos) {
            Jornada jornada = jornadaService.crearJornada(liga);
            for (Equipo[] pair : emparejamientos) {
                partidoService.agregarPartido(pair[0], pair[1], 0, 0, jornada);
            }
        }

        // Crear jornadas para la segunda vuelta (vuelta: invertir local/visitante)
        for (List<Equipo[]> emparejamientos : rondasEmparejamientos) {
            Jornada jornada = jornadaService.crearJornada(liga);
            for (Equipo[] pair : emparejamientos) {
                partidoService.agregarPartido(pair[1], pair[0], 0, 0, jornada);
            }
        }

        redirectAttributes.addFlashAttribute("message", "Jornadas generadas correctamente");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/liga/" + ligaId + "/clasificacion";
    }

    /**
     * Ruta compatible con el menú: /generar-cuadros-jornadas
     * Acepta un parámetro opcional ligaId. Si no se proporciona, redirige a /liga
     * para que el usuario seleccione la liga.
     */
    @RequestMapping(path = "/generar-cuadros-jornadas", method = {RequestMethod.GET, RequestMethod.POST})
    public String generarCuadrosDesdeMenu(@RequestParam(required = false) Long ligaId,
                                          @RequestParam(required = false, defaultValue = "false") boolean force,
                                          RedirectAttributes redirectAttributes,
                                          HttpSession session) {
        boolean sessionHasUser = session != null && session.getAttribute("usuario") != null;
        log.info("/generar-cuadros-jornadas called with ligaId={} force={} sessionUserPresent={}", ligaId, force, sessionHasUser);

        // Si no viene ligaId, intentar obtenerlo de la sesión (usuario logueado)
        if (ligaId == null) {
            Object usuarioObj = null;
            if (session != null) {
                usuarioObj = session.getAttribute("usuario");
            }
            if (usuarioObj instanceof com.example.Liga_Del_Cume.data.model.Usuario usuario) {
                if (usuario.getLiga() != null) {
                    ligaId = usuario.getLiga().getIdLigaCume();
                }
            }
        }

        if (ligaId == null) {
            redirectAttributes.addFlashAttribute("message", "Selecciona primero una liga para generar sus jornadas.");
            redirectAttributes.addFlashAttribute("messageType", "warning");
            return "redirect:/liga";
        }

        // Redirigir al endpoint /jornada/generar para que la operación sea tratada por Spring MVC
        return "redirect:/jornada/generar?ligaId=" + ligaId + "&force=" + force;
    }
}
