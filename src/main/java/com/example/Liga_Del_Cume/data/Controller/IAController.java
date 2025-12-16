package com.example.Liga_Del_Cume.data.Controller;

import com.example.Liga_Del_Cume.data.model.Usuario;
import com.example.Liga_Del_Cume.data.service.IAService;
import com.example.Liga_Del_Cume.data.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para gestionar las recomendaciones de alineación mediante IA
 * Utiliza OpenRouter API con el modelo Nemotron a través del IAService
 */
@Controller
@RequestMapping("/liga/{idLiga}")
public class IAController {

    @Autowired
    private IAService iaService;

    @Autowired
    private UsuarioService usuarioService;

    public IAController() {
        System.out.println("\t Builder of " + this.getClass().getSimpleName());
    }

    /**
     * Muestra la página de recomendación de IA con la sugerencia de alineación
     *
     * @param ligaId ID de la liga
     * @param usuarioId ID del usuario que solicita la recomendación
     * @param model Modelo para pasar datos a la vista
     * @param redirectAttributes Atributos para redirección
     * @return Vista de recomendación IA o redirección en caso de error
     */
    @GetMapping("/alineacion-sugeria")
    public String mostrarRecomendacionIA(
            @PathVariable("idLiga") Long ligaId,
            @RequestParam Long usuarioId,
            Model model,
            RedirectAttributes redirectAttributes) {

        System.out.println("\t GET /liga/" + ligaId + "/alineacion-sugeria - Usuario ID: " + usuarioId);

        try {
            // Obtener información del usuario
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/liga/" + ligaId + "/ranking";
            }

            // Generar recomendación personalizada
            String recomendacion = iaService.generarRecomendacionPersonalizada(
                ligaId,
                usuario.getNombreUsuario()
            );

            // Agregar datos al modelo (usando ligaId para consistencia con el menú)
            model.addAttribute("ligaId", ligaId);
            model.addAttribute("usuarioId", usuarioId);
            model.addAttribute("nombreUsuario", usuario.getNombreUsuario());
            model.addAttribute("recomendacion", recomendacion);
            model.addAttribute("asistenteName", "Guardiol-IA");
            model.addAttribute("currentPage", "alineacionIA");

            System.out.println("\t ✓ Recomendación generada exitosamente");

            return "alineacionSugerIA";

        } catch (Exception e) {
            System.err.println("\t ✗ Error al generar recomendación: " + e.getMessage());
            e.printStackTrace();

            redirectAttributes.addFlashAttribute("error",
                "Error al generar la recomendación: " + e.getMessage());
            return "redirect:/liga/" + ligaId + "/ranking";
        }
    }

    /**
     * Endpoint alternativo para regenerar la recomendación
     *
     * @param ligaId ID de la liga
     * @param usuarioId ID del usuario
     * @param model Modelo para la vista
     * @param redirectAttributes Atributos de redirección
     * @return Vista actualizada con nueva recomendación
     */
    @GetMapping("/alineacion-sugeria/regenerar")
    public String regenerarRecomendacion(
            @PathVariable("idLiga") Long ligaId,
            @RequestParam Long usuarioId,
            Model model,
            RedirectAttributes redirectAttributes) {

        System.out.println("\t GET /liga/" + ligaId + "/alineacion-sugeria/regenerar");

        // Redirigir al método principal para generar una nueva recomendación
        return mostrarRecomendacionIA(ligaId, usuarioId, model, redirectAttributes);
    }
}

