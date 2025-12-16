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
     * Muestra la página inicial con pantalla de carga
     * La recomendación se carga después mediante AJAX
     *
     * @param ligaId ID de la liga
     * @param usuarioId ID del usuario que solicita la recomendación
     * @param model Modelo para pasar datos a la vista
     * @return Vista de recomendación IA con pantalla de carga
     */
    @GetMapping("/alineacion-sugeria")
    public String mostrarPaginaIA(
            @PathVariable("idLiga") Long ligaId,
            @RequestParam Long usuarioId,
            Model model) {

        System.out.println("\t GET /liga/" + ligaId + "/alineacion-sugeria - Usuario ID: " + usuarioId);

        // Obtener información del usuario
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);
        String nombreUsuario = usuario != null ? usuario.getNombreUsuario() : "Usuario";

        // Agregar datos al modelo (la recomendación se cargará con AJAX)
        model.addAttribute("ligaId", ligaId);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("nombreUsuario", nombreUsuario);
        model.addAttribute("asistenteName", "Guardiol-IA");
        model.addAttribute("currentPage", "alineacionIA");

        return "alineacionSugerIA";
    }

    /**
     * Endpoint AJAX que genera y devuelve la recomendación en formato JSON
     *
     * @param ligaId ID de la liga
     * @param usuarioId ID del usuario
     * @return ResponseEntity con la recomendación o error en JSON
     */
    @GetMapping("/alineacion-sugeria/generar")
    @org.springframework.web.bind.annotation.ResponseBody
    public org.springframework.http.ResponseEntity<?> generarRecomendacion(
            @PathVariable("idLiga") Long ligaId,
            @RequestParam Long usuarioId) {

        System.out.println("\t AJAX GET /liga/" + ligaId + "/alineacion-sugeria/generar - Usuario ID: " + usuarioId);

        try {
            // Obtener información del usuario
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);
            if (usuario == null) {
                return org.springframework.http.ResponseEntity
                    .status(org.springframework.http.HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of("error", "Usuario no encontrado. Por favor, verifica tu sesión."));
            }

            // Generar recomendación personalizada
            String recomendacion = iaService.generarRecomendacionPersonalizada(
                ligaId,
                usuario.getNombreUsuario()
            );

            System.out.println("\t ✓ Recomendación generada exitosamente");

            // Devolver la recomendación en formato JSON
            return org.springframework.http.ResponseEntity.ok(
                java.util.Map.of(
                    "success", true,
                    "recomendacion", recomendacion,
                    "nombreUsuario", usuario.getNombreUsuario()
                )
            );

        } catch (Exception e) {
            System.err.println("\t ✗ Error al generar recomendación: " + e.getMessage());
            e.printStackTrace();

            String errorMsg = e.getMessage() != null ? e.getMessage() : "Error de conexión con el servicio de IA.";

            return org.springframework.http.ResponseEntity
                .status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of(
                    "success", false,
                    "error", "No se pudo generar la recomendación. " + errorMsg
                ));
        }
    }
}

