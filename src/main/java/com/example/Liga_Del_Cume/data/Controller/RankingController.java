package com.example.Liga_Del_Cume.data.Controller;

import com.example.Liga_Del_Cume.data.model.Usuario;
import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.service.UsuarioService;
import com.example.Liga_Del_Cume.data.service.LigaService;
import com.example.Liga_Del_Cume.data.exceptions.UsuarioException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador para gestionar el ranking de usuarios en una liga
 *
 * Este controlador maneja la visualización del ranking de clasificación
 * de los usuarios/managers en una liga específica, ordenados por puntos
 * acumulados de mayor a menor.
 *
 * @author Liga del Cume Team
 * @version 1.0
 */
@Controller
@RequestMapping("/liga/{ligaId}/ranking")
public class RankingController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private LigaService ligaService;

    /**
     * Muestra el ranking de usuarios de una liga específica
     *
     * Este método obtiene todos los usuarios de una liga y los ordena
     * por puntos acumulados de forma descendente (de mayor a menor).
     * Los usuarios se muestran en una vista con sus posiciones, nombres
     * y puntuaciones.
     *
     * Funcionalidad:
     * - Obtiene el ranking completo de la liga
     * - Los usuarios están ordenados por puntos (mayor a menor)
     * - Muestra medallas especiales para los 3 primeros lugares
     * - Maneja errores si la liga no existe
     *
     * @param ligaId ID de la liga de la cual se quiere ver el ranking
     * @param model Modelo para pasar datos a la vista
     * @param redirectAttributes Atributos para mensajes de redirección
     * @return Vista "ranking" con la lista de usuarios ordenados
     */
    @GetMapping
    public String mostrarRanking(
            @PathVariable("ligaId") Long ligaId,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
            // Obtener el ranking de usuarios ordenados por puntos (mayor a menor)
            List<Usuario> usuarios = usuarioService.obtenerRankingLiga(ligaId);

            // Obtener el nombre de la liga
            LigaCume liga = ligaService.obtenerLigaPorId(ligaId);
            String nombreLiga = liga != null ? liga.getNombreLiga() : "Mis Ligas";

            // Pasar datos a la vista
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("ligaId", ligaId);
            model.addAttribute("currentPage", "ranking");
            model.addAttribute("usuario", usuarioSesion);
            model.addAttribute("nombreLiga", nombreLiga);
            // Si hay usuarios, agregar información adicional
            if (!usuarios.isEmpty()) {
                model.addAttribute("totalUsuarios", usuarios.size());
                model.addAttribute("lider", usuarios.get(0)); // El primero del ranking
            }

            return "ranking";

        } catch (UsuarioException e) {
            // Error al obtener el ranking (ej: liga no existe)
            redirectAttributes.addFlashAttribute("error",
                    "Error al cargar el ranking: " + e.getMessage());
            return "redirect:/liga";

        } catch (Exception e) {
            // Error inesperado
            redirectAttributes.addFlashAttribute("error",
                    "Error inesperado al cargar el ranking. Por favor, inténtalo de nuevo.");
            return "redirect:/liga";
        }
    }

    /**
     * Endpoint alternativo para compatibilidad
     * Redirige a la ruta principal del ranking
     *
     * @param ligaId ID de la liga
     * @return Redirección a la ruta principal del ranking
     */
    @GetMapping("/")
    public String mostrarRankingSlash(@PathVariable("ligaId") Long ligaId) {
        return "redirect:/liga/" + ligaId + "/ranking";
    }
}

