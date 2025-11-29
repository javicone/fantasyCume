package com.example.Liga_Del_Cume.data.Controller;

import com.example.Liga_Del_Cume.data.model.Equipo;
import com.example.Liga_Del_Cume.data.model.Jugador;
import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.service.EquipoService;
import com.example.Liga_Del_Cume.data.service.JugadorService;
import com.example.Liga_Del_Cume.data.repository.LigaCumeRepository;
import com.example.Liga_Del_Cume.data.exceptions.EquipoException;
import com.example.Liga_Del_Cume.data.exceptions.JugadorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador para la gestión de equipos (Admin)
 *
 * Funcionalidades:
 * - Listar todos los equipos
 * - Crear nuevo equipo
 * - Eliminar equipo
 */
@Controller
@RequestMapping("/liga/{ligaId}/admin/equipos")
public class EquipoController {

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private LigaCumeRepository ligaCumeRepository;

    /**
     * GET: Mostrar página de gestión de equipos
     * Lista todos los equipos con su información
     */
    @GetMapping
    public String gestionarEquipos(@PathVariable("ligaId") Long ligaId, Model model) {
        try {
            // Obtener todos los equipos
            List<Equipo> equipos = equipoService.listarTodosEquipos();

            model.addAttribute("equipos", equipos);
            model.addAttribute("ligaId", ligaId);
            model.addAttribute("currentPage", "gestionarEquipos");

            return "gestionarEquipos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los equipos: " + e.getMessage());
            model.addAttribute("ligaId", ligaId);
            return "gestionarEquipos";
        }
    }

    /**
     * GET: Mostrar formulario para crear nuevo equipo
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoEquipo(@PathVariable("ligaId") Long ligaId, Model model) {
        // Obtener todas las ligas disponibles
        List<LigaCume> ligas = ligaCumeRepository.findAll();

        model.addAttribute("equipo", new Equipo());
        model.addAttribute("ligas", ligas);
        model.addAttribute("ligaId", ligaId);
        model.addAttribute("currentPage", "gestionarEquipos");
        return "nuevoEquipo";
    }

    /**
     * POST: Crear un nuevo equipo
     */
    @PostMapping("/crear")
    public String crearEquipo(
            @PathVariable("ligaId") Long ligaId,
            @RequestParam("nombreEquipo") String nombreEquipo,
            @RequestParam("escudoURL") String escudoURL,
            @RequestParam("idLiga") Long idLiga,
            RedirectAttributes redirectAttributes) {
        try {
            // Validar que los campos no estén vacíos
            if (nombreEquipo == null || nombreEquipo.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El nombre del equipo es obligatorio");
                return "redirect:/liga/" + ligaId + "/admin/equipos/nuevo";
            }

            if (escudoURL == null || escudoURL.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "La URL del escudo es obligatoria");
                return "redirect:/liga/" + ligaId + "/admin/equipos/nuevo";
            }

            if (idLiga == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar una liga");
                return "redirect:/liga/" + ligaId + "/admin/equipos/nuevo";
            }

            // Crear el equipo usando el servicio con los parámetros: nombre, idLiga, escudoURL
            Equipo nuevoEquipo = equipoService.agregarEquipo(nombreEquipo.trim(), idLiga, escudoURL.trim());

            redirectAttributes.addFlashAttribute("success",
                "Equipo '" + nuevoEquipo.getNombreEquipo() + "' creado exitosamente");

            return "redirect:/liga/" + ligaId + "/admin/equipos";

        } catch (EquipoException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/liga/" + ligaId + "/admin/equipos/nuevo";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al crear el equipo: " + e.getMessage());
            return "redirect:/liga/" + ligaId + "/admin/equipos/nuevo";
        }
    }

    /**
     * DELETE: Eliminar un equipo
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarEquipo(
            @PathVariable("ligaId") Long ligaId,
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes) {
        try {
            equipoService.eliminarEquipo(id);
            redirectAttributes.addFlashAttribute("success", "Equipo eliminado exitosamente");
        } catch (EquipoException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el equipo");
        }

        return "redirect:/liga/" + ligaId + "/admin/equipos";
    }

    /**
     * GET: Mostrar detalle de un equipo con sus jugadores (FOTO 2)
     */
    @GetMapping("/{id}")
    public String editarEquipo(
            @PathVariable("ligaId") Long ligaId,
            @PathVariable("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener el equipo con sus jugadores
            Equipo equipo = equipoService.obtenerEquipo(id);

            // Contar jugadores activos
            int jugadoresActivos = equipo.getJugadores() != null ? equipo.getJugadores().size() : 0;

            // Contar porteros
            long numPorteros = equipo.getJugadores() != null ?
                equipo.getJugadores().stream().filter(Jugador::isEsPortero).count() : 0;

            model.addAttribute("equipo", equipo);
            model.addAttribute("jugadoresActivos", jugadoresActivos);
            model.addAttribute("numPorteros", numPorteros);
            model.addAttribute("ligaId", ligaId);
            model.addAttribute("currentPage", "gestionarEquipos");

            return "editarEquipo";
        } catch (EquipoException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/liga/" + ligaId + "/admin/equipos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el equipo");
            return "redirect:/liga/" + ligaId + "/admin/equipos";
        }
    }

    /**
     * GET: Mostrar formulario para crear nuevo jugador
     */
    @GetMapping("/{equipoId}/jugador/nuevo")
    public String mostrarFormularioNuevoJugador(
            @PathVariable("ligaId") Long ligaId,
            @PathVariable("equipoId") Long equipoId,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Equipo equipo = equipoService.obtenerEquipo(equipoId);
            model.addAttribute("equipo", equipo);
            model.addAttribute("ligaId", ligaId);
            model.addAttribute("currentPage", "gestionarEquipos");
            return "nuevoJugador";
        } catch (EquipoException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/liga/" + ligaId + "/admin/equipos";
        }
    }

    /**
     * POST: Crear un nuevo jugador para el equipo
     */
    @PostMapping("/{equipoId}/jugador/crear")
    public String crearJugador(
            @PathVariable("ligaId") Long ligaId,
            @PathVariable("equipoId") Long equipoId,
            @RequestParam("nombreJugador") String nombreJugador,
            @RequestParam("precioMercado") float precioMercado,
            @RequestParam("avatarUrl") String avatarUrl,
            @RequestParam("esPortero") boolean esPortero,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener el equipo
            Equipo equipo = equipoService.obtenerEquipo(equipoId);

            // Crear el jugador
            Jugador nuevoJugador = jugadorService.agregarJugador(
                nombreJugador, esPortero, equipo, precioMercado, avatarUrl
            );

            redirectAttributes.addFlashAttribute("success",
                "Jugador '" + nuevoJugador.getNombreJugador() + "' creado exitosamente");

            return "redirect:/liga/" + ligaId + "/admin/equipos/" + equipoId;

        } catch (JugadorException | EquipoException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/liga/" + ligaId + "/admin/equipos/" + equipoId + "/jugador/nuevo";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al crear el jugador");
            return "redirect:/liga/" + ligaId + "/admin/equipos/" + equipoId + "/jugador/nuevo";
        }
    }

    /**
     * POST: Eliminar un jugador del equipo
     */
    @PostMapping("/{equipoId}/jugador/eliminar/{jugadorId}")
    public String eliminarJugador(
            @PathVariable("ligaId") Long ligaId,
            @PathVariable("equipoId") Long equipoId,
            @PathVariable("jugadorId") Long jugadorId,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener el equipo para verificar porteros
            Equipo equipo = equipoService.obtenerEquipo(equipoId);
            Jugador jugador = jugadorService.obtenerJugador(jugadorId);

            // Validar si es portero y es el único
            if (jugador.isEsPortero()) {
                long numPorteros = equipo.getJugadores().stream()
                    .filter(Jugador::isEsPortero)
                    .count();

                if (numPorteros <= 1) {
                    redirectAttributes.addFlashAttribute("error",
                        "No se puede eliminar al único portero del equipo. Debe haber al menos un portero.");
                    return "redirect:/liga/" + ligaId + "/admin/equipos/" + equipoId;
                }
            }

            // Eliminar el jugador
            jugadorService.eliminarJugador(jugadorId);
            redirectAttributes.addFlashAttribute("success", "Jugador eliminado exitosamente");

        } catch (JugadorException | EquipoException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el jugador");
        }

        return "redirect:/liga/" + ligaId + "/admin/equipos/" + equipoId;
    }

    /**
     * GET: Mostrar formulario para editar un jugador (FOTO 3)
     */
    @GetMapping("/{equipoId}/jugador/{jugadorId}/editar")
    public String mostrarFormularioEditarJugador(
            @PathVariable("ligaId") Long ligaId,
            @PathVariable("equipoId") Long equipoId,
            @PathVariable("jugadorId") Long jugadorId,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Equipo equipo = equipoService.obtenerEquipo(equipoId);
            Jugador jugador = jugadorService.obtenerJugador(jugadorId);

            // Verificar que el jugador pertenece al equipo
            if (!jugador.getEquipo().getIdEquipo().equals(equipoId)) {
                redirectAttributes.addFlashAttribute("error", "El jugador no pertenece a este equipo");
                return "redirect:/liga/" + ligaId + "/admin/equipos/" + equipoId;
            }

            // Contar porteros del equipo
            long numPorteros = equipo.getJugadores().stream()
                .filter(Jugador::isEsPortero)
                .count();

            model.addAttribute("equipo", equipo);
            model.addAttribute("jugador", jugador);
            model.addAttribute("numPorteros", numPorteros);
            model.addAttribute("ligaId", ligaId);
            model.addAttribute("currentPage", "gestionarEquipos");

            return "editarJugador";
        } catch (JugadorException | EquipoException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/liga/" + ligaId + "/admin/equipos";
        }
    }

    /**
     * POST: Actualizar información de un jugador (FOTO 3)
     */
    @PostMapping("/{equipoId}/jugador/{jugadorId}/actualizar")
    public String actualizarJugador(
            @PathVariable("ligaId") Long ligaId,
            @PathVariable("equipoId") Long equipoId,
            @PathVariable("jugadorId") Long jugadorId,
            @RequestParam("nombreJugador") String nombreJugador,
            @RequestParam("precioMercado") float precioMercado,
            @RequestParam("avatarUrl") String avatarUrl,
            @RequestParam(value = "esPortero", required = false, defaultValue = "false") boolean esPortero,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener el equipo y el jugador
            Equipo equipo = equipoService.obtenerEquipo(equipoId);
            Jugador jugador = jugadorService.obtenerJugador(jugadorId);

            // Verificar si está intentando cambiar de portero a jugador de campo
            if (jugador.isEsPortero() && !esPortero) {
                // Contar cuántos porteros hay
                long numPorteros = equipo.getJugadores().stream()
                    .filter(Jugador::isEsPortero)
                    .count();

                // Si es el único portero, no permitir el cambio
                if (numPorteros <= 1) {
                    redirectAttributes.addFlashAttribute("error",
                        "No se puede cambiar la posición. El equipo debe tener al menos un portero.");
                    return "redirect:/liga/" + ligaId + "/admin/equipos/" + equipoId + "/jugador/" + jugadorId + "/editar";
                }
            }

            // Actualizar el jugador
            Jugador jugadorActualizado = jugadorService.actualizarJugadorCompleto(
                jugadorId, nombreJugador, precioMercado, esPortero, avatarUrl
            );

            redirectAttributes.addFlashAttribute("success",
                "Jugador '" + jugadorActualizado.getNombreJugador() + "' actualizado exitosamente");

            return "redirect:/liga/" + ligaId + "/admin/equipos/" + equipoId;

        } catch (JugadorException | EquipoException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/liga/" + ligaId + "/admin/equipos/" + equipoId + "/jugador/" + jugadorId + "/editar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al actualizar el jugador");
            return "redirect:/liga/" + ligaId + "/admin/equipos/" + equipoId + "/jugador/" + jugadorId + "/editar";
        }
    }
}

