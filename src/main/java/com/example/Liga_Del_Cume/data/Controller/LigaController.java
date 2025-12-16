package com.example.Liga_Del_Cume.data.Controller;

import com.example.Liga_Del_Cume.data.exceptions.UsuarioException;
import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.model.Usuario;
import com.example.Liga_Del_Cume.data.service.LigaService;
import com.example.Liga_Del_Cume.data.service.UsuarioService;
import com.example.Liga_Del_Cume.data.exceptions.LigaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador para la gestión de ligas de fantasy
 *
 * Gestiona las operaciones CRUD de ligas y la navegación entre vistas
 */
@Controller
@RequestMapping("/liga")
public class LigaController {

    @Autowired
    private LigaService ligaService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Muestra la página de selección de ligas
     *
     * @param model Modelo para pasar datos a la vista
     * @return Vista de selección de ligas
     */
    @GetMapping
    public String mostrarLigas(Model model) {
        try {
            List<LigaCume> ligas = ligaService.listarLigas();
            model.addAttribute("ligas", ligas);
            return "ligas";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las ligas: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Muestra el detalle de una liga específica
     *
     * @param id ID de la liga
     * @param model Modelo para pasar datos a la vista
     * @return Vista de detalle de la liga
     */
    @GetMapping("/{id}")
    public String verLiga(@PathVariable Long id, Model model) {
        try {
            LigaCume liga = ligaService.buscarLigaPorId(id);
            model.addAttribute("liga", liga);
            model.addAttribute("usuarios", liga.getUsuarios());
            return "ligaDetalle";
        } catch (LigaException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/liga";
        }
    }

    /**
     * Muestra el formulario para crear una nueva liga
     *
     * @param usuarioId ID del usuario que crea la liga (desde sesión o parámetro)
     * @param model Modelo para pasar datos a la vista
     * @return Vista del formulario de creación
     */
    @GetMapping("/crear")
    public String mostrarFormularioCrear(
            @RequestParam(value = "usuarioId", required = false) Long usuarioId,
            Model model) {
        model.addAttribute("liga", new LigaCume());
        model.addAttribute("usuarioId", usuarioId);
        return "crearLiga";
    }

    /**
     * Procesa la creación de una nueva liga y suscribe al usuario creador
     *
     * @param nombreLiga Nombre de la liga
     * @param presupuestoMaximo Presupuesto máximo de la liga
     * @param usuarioId ID del usuario que crea la liga
     * @param redirectAttributes Atributos para redirección
     * @return Redirección a la liga creada
     */
    @PostMapping("/crear")
    public String crearLiga(
            @RequestParam("nombreLiga") String nombreLiga,
            @RequestParam("presupuestoMaximo") Double presupuestoMaximo,
            @RequestParam(value = "usuarioId", required = false) Long usuarioId,
            RedirectAttributes redirectAttributes) {
        try {
            // Crear la liga
            LigaCume liga = ligaService.darDeAltaLiga(nombreLiga, presupuestoMaximo);

            // Si hay un usuario, suscribirlo a la liga automáticamente
            if (usuarioId != null) {
                try {
                    usuarioService.suscribirUsuarioALiga(usuarioId, liga.getIdLigaCume());
                    redirectAttributes.addFlashAttribute("success",
                        "Liga '" + nombreLiga + "' creada exitosamente. ¡Bienvenido como miembro fundador!");
                } catch (UsuarioException e) {
                    redirectAttributes.addFlashAttribute("warning",
                        "Liga creada, pero hubo un error al suscribirte: " + e.getMessage());
                }
            } else {
                redirectAttributes.addFlashAttribute("success",
                    "Liga '" + nombreLiga + "' creada exitosamente");
            }

            return "liga";
        } catch (LigaException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/liga/crear" + (usuarioId != null ? "?usuarioId=" + usuarioId : "");
        }
    }

    /**
     * Muestra el formulario para buscar ligas
     *
     * @param model Modelo para pasar datos a la vista
     * @return Vista de búsqueda de ligas
     */
    @GetMapping("/buscar")
    public String mostrarFormularioBuscar(Model model) {
        return "buscarLiga";
    }

    /**
     * Busca ligas por nombre
     *
     * @param nombre Nombre a buscar
     * @param model Modelo para pasar datos a la vista
     * @return Vista con resultados de búsqueda
     */
    @GetMapping("/buscar/resultado")
    public String buscarLiga(@RequestParam(required = false) String nombre, Model model) {
        try {
            List<LigaCume> ligas;
            if (nombre != null && !nombre.trim().isEmpty()) {
                ligas = ligaService.buscarLigasPorNombre(nombre);
                model.addAttribute("busqueda", nombre);
            } else {
                ligas = ligaService.listarLigas();
            }
            model.addAttribute("ligas", ligas);
            return "buscarLiga";
        } catch (Exception e) {
            model.addAttribute("error", "Error en la búsqueda: " + e.getMessage());
            return "buscarLiga";
        }
    }

    /**
     * Permite unirse a una liga existente
     *
     * @param ligaId ID de la liga
     * @param usuarioId ID del usuario que se une
     * @param redirectAttributes Atributos para redirección
     * @return Redirección a la liga
     */
    @PostMapping("/{ligaId}/unirse")
    public String unirseALiga(
            @PathVariable Long ligaId,
            @RequestParam("usuarioId") Long usuarioId,
            RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);
            LigaCume liga = ligaService.buscarLigaPorId(ligaId);

            // Agregar usuario a la liga
            liga.getUsuarios().add(usuario);
            usuario.setLiga(liga);

            ligaService.actualizarLiga(ligaId, liga.getNombreLiga(), liga.getPresupuestoMaximo().doubleValue());

            redirectAttributes.addFlashAttribute("success",
                "Te has unido a la liga exitosamente");
            return "redirect:/liga/" + ligaId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al unirse a la liga: " + e.getMessage());
            return "redirect:/liga/buscar";
        }
    }

    /**
     * Muestra el formulario para editar una liga
     *
     * @param id ID de la liga
     * @param model Modelo para pasar datos a la vista
     * @return Vista del formulario de edición
     */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        try {
            LigaCume liga = ligaService.buscarLigaPorId(id);
            model.addAttribute("liga", liga);
            return "editarLiga";
        } catch (LigaException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/liga";
        }
    }

    /**
     * Procesa la actualización de una liga
     *
     * @param id ID de la liga
     * @param nombreLiga Nuevo nombre
     * @param presupuestoMaximo Nuevo presupuesto
     * @param redirectAttributes Atributos para redirección
     * @return Redirección a la liga actualizada
     */
    @PostMapping("/{id}/editar")
    public String editarLiga(
            @PathVariable Long id,
            @RequestParam("nombreLiga") String nombreLiga,
            @RequestParam("presupuestoMaximo") Double presupuestoMaximo,
            RedirectAttributes redirectAttributes) {
        try {
            ligaService.actualizarLiga(id, nombreLiga, presupuestoMaximo);
            redirectAttributes.addFlashAttribute("success",
                "Liga actualizada exitosamente");
            return "redirect:/liga/" + id;
        } catch (LigaException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/liga/" + id + "/editar";
        }
    }

    /**
     * Elimina una liga
     *
     * @param id ID de la liga a eliminar
     * @param redirectAttributes Atributos para redirección
     * @return Redirección a la lista de ligas
     */
    @PostMapping("/{id}/eliminar")
    public String eliminarLiga(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            LigaCume liga = ligaService.buscarLigaPorId(id);
            String nombreLiga = liga.getNombreLiga();
            ligaService.eliminarLiga(id);
            redirectAttributes.addFlashAttribute("success",
                "Liga '" + nombreLiga + "' eliminada exitosamente");
            return "redirect:/liga";
        } catch (LigaException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/liga/" + id;
        }
    }

    /**
     * Muestra la página de administración de ligas del usuario
     *
     * @param model Modelo para pasar datos a la vista
     * @return Vista de administración de ligas
     */
    @GetMapping("/perfil/ligas")
    public String administrarLigas(Model model) {
        try {
            List<LigaCume> ligas = ligaService.listarLigas();
            model.addAttribute("ligas", ligas);
            return "administrarLigas";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las ligas: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Endpoint para cambiar el presupuesto máximo de una liga
     *
     * @param ligaId ID de la liga a modificar
     * @param datos Map con el nuevo presupuesto
     * @return Respuesta JSON indicando éxito o error
     */
    @PostMapping("/{ligaId}/admin/cambiar-presupuesto")
    @ResponseBody
    public java.util.Map<String, Object> cambiarPresupuesto(
            @PathVariable("ligaId") Long ligaId,
            @RequestBody java.util.Map<String, Object> datos) {

        java.util.Map<String, Object> response = new java.util.HashMap<>();

        try {
            // Obtener el nuevo presupuesto del request
            Long nuevoPresupuesto = ((Number) datos.get("presupuestoMaximo")).longValue();

            // Validaciones
            if (nuevoPresupuesto == null || nuevoPresupuesto <= 0) {
                response.put("success", false);
                response.put("error", "El presupuesto debe ser mayor a 0");
                return response;
            }

            if (nuevoPresupuesto < 500000) {
                response.put("success", false);
                response.put("error", "El presupuesto mínimo es de 500.000€");
                return response;
            }

            // Actualizar el presupuesto usando el servicio
            ligaService.actualizarPresupuestoMaximo(ligaId, nuevoPresupuesto);

            // Formatear el presupuesto para mostrar
            String presupuestoFormateado = String.format("%,d€", nuevoPresupuesto);

            response.put("success", true);
            response.put("message", "Presupuesto actualizado correctamente a " + presupuestoFormateado);
            return response;

        } catch (LigaException e) {
            response.put("success", false);
            response.put("error", "Liga no encontrada: " + e.getMessage());
            return response;

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Error al actualizar el presupuesto: " + e.getMessage());
            return response;
        }
    }

    // NOTA: El endpoint /{id}/ranking ahora está manejado por RankingController
}

