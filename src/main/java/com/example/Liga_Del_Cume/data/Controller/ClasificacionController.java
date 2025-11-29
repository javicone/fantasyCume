package com.example.Liga_Del_Cume.data.Controller;

import com.example.Liga_Del_Cume.data.model.ClasificacionEquipo;
import com.example.Liga_Del_Cume.data.model.Usuario;
import com.example.Liga_Del_Cume.data.service.ClasificacionService;
import com.example.Liga_Del_Cume.data.exceptions.EquipoException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador para gestionar la clasificación de equipos en una liga
 *
 * Este controlador maneja la visualización de la tabla de clasificación
 * de los equipos de fútbol en una liga específica, mostrando:
 * - Posición en la tabla
 * - Nombre y escudo del equipo
 * - Estadísticas (partidos jugados, victorias, empates, derrotas)
 * - Goles a favor y en contra
 * - Puntos totales
 *
 * @author Liga del Cume Team
 * @version 1.0
 */
@Controller
@RequestMapping("/liga/{ligaId}/clasificacion")
public class ClasificacionController {

    @Autowired
    private ClasificacionService clasificacionService;

    /**
     * Muestra la clasificación de equipos de una liga específica
     *
     * Este método obtiene todos los equipos de una liga con sus estadísticas
     * calculadas y los ordena por puntos de forma descendente (de mayor a menor).
     * Los equipos se muestran en una tabla con toda su información relevante.
     *
     * Funcionalidad:
     * - Obtiene la clasificación completa de la liga
     * - Los equipos están ordenados por:
     *   1. Puntos (mayor a menor)
     *   2. Diferencia de goles (si hay empate en puntos)
     *   3. Goles a favor (si persiste el empate)
     * - Muestra escudos, nombres y estadísticas completas
     * - Maneja errores si la liga no existe
     *
     * @param ligaId ID de la liga de la cual se quiere ver la clasificación
     * @param model Modelo para pasar datos a la vista
     * @param redirectAttributes Atributos para mensajes de redirección
     * @param session Sesión HTTP para obtener el usuario actual
     * @return Vista "clasificacion" con la lista de equipos ordenados
     */
    @GetMapping
    public String mostrarClasificacion(
            @PathVariable("ligaId") Long ligaId,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            // Obtener el usuario de la sesión
            Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

            // Obtener la clasificación de equipos ordenada por puntos
            List<ClasificacionEquipo> clasificacion = clasificacionService.obtenerClasificacionLiga(ligaId);

            // Pasar datos a la vista
            model.addAttribute("clasificacion", clasificacion);
            model.addAttribute("ligaId", ligaId);
            model.addAttribute("currentPage", "clasificacion");
            model.addAttribute("usuario", usuarioSesion);

            // Si hay equipos, agregar información adicional
            if (!clasificacion.isEmpty()) {
                model.addAttribute("totalEquipos", clasificacion.size());
                model.addAttribute("lider", clasificacion.get(0)); // El primero de la clasificación
            }

            return "clasificacion";

        } catch (EquipoException e) {
            // Error al obtener la clasificación (ej: liga no existe)
            redirectAttributes.addFlashAttribute("error",
                    "Error al cargar la clasificación: " + e.getMessage());
            return "redirect:/liga";

        } catch (Exception e) {
            // Error inesperado
            redirectAttributes.addFlashAttribute("error",
                    "Error inesperado al cargar la clasificación. Por favor, inténtalo de nuevo.");
            return "redirect:/liga";
        }
    }

    /**
     * Endpoint alternativo para compatibilidad
     * Redirige a la ruta principal de la clasificación
     *
     * @param ligaId ID de la liga
     * @return Redirección a la ruta principal de la clasificación
     */
    @GetMapping("/")
    public String mostrarClasificacionSlash(@PathVariable("ligaId") Long ligaId) {
        return "redirect:/liga/" + ligaId + "/clasificacion";
    }
}

