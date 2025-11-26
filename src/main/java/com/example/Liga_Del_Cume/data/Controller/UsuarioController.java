package com.example.Liga_Del_Cume.data.Controller;

import com.example.Liga_Del_Cume.data.exceptions.UsuarioException;
import com.example.Liga_Del_Cume.data.model.Usuario;
import com.example.Liga_Del_Cume.data.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para gestionar las operaciones relacionadas con usuarios
 * @author Liga del Cume
 * @version 1.0
 */
@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    public UsuarioController() {
        System.out.println("\t Builder of " + this.getClass().getSimpleName());
    }

    /**
     * Procesa el formulario de login
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @param model Modelo para pasar datos a la vista
     * @param redirectAttributes Atributos para redirección
     * @return Vista correspondiente según el resultado del login
     */
    @PostMapping("/login")
    public String login(@RequestParam String email,
                       @RequestParam String password,
                       Model model,
                       RedirectAttributes redirectAttributes) {

        System.out.println("\t Peticion POST de login - Email: " + email);


            // Aquí implementarás la lógica de autenticación
            try {
            Usuario user = usuarioService.autenticarUsuario(email, password);
            if(user != null){
                redirectAttributes.addFlashAttribute("message", "Inicio de sesión exitoso. Bienvenido " + user.getNombreUsuario());
                redirectAttributes.addFlashAttribute("messageType", "success");
            }
            }
            catch  (UsuarioException e) {

                    redirectAttributes.addFlashAttribute("message", "Credenciales inválidas");
                    redirectAttributes.addFlashAttribute("messageType", "danger");
                }

            return "redirect:/";
    }

    /**
     * Procesa el formulario de registro
     * @param nombreUsuario Nombre del usuario
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @param passwordConfirm Confirmación de contraseña
     * @param model Modelo para pasar datos a la vista
     * @param redirectAttributes Atributos para redirección
     * @return Vista correspondiente según el resultado del registro
     */
    @PostMapping("/registro")
    public String registro(@RequestParam String nombreUsuario,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String passwordConfirm,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        System.out.println("\t Peticion POST de registro - Usuario: " + nombreUsuario + ", Email: " + email);

        try {
            // Validar que las contraseñas coincidan
            if (!password.equals(passwordConfirm)) {
                redirectAttributes.addFlashAttribute("message", "Las contraseñas no coinciden");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/";
            }

            // Validar longitud de contraseña
            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("message", "La contraseña debe tener al menos 6 caracteres");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/";
            }

            // Aquí implementarás la lógica de registro usando el UsuarioService
            try{
                Usuario user = usuarioService.darDeAltaUsuario(nombreUsuario, email, password);
                System.out.println(user.toString());
                if(user!=null)
                {
                    redirectAttributes.addFlashAttribute("message", "Registro exitoso. Bienvenido " + user.getNombreUsuario());
                    redirectAttributes.addFlashAttribute("messageType", "success");
                    return "redirect:/";
                }
            }
            catch (UsuarioException e) {
                redirectAttributes.addFlashAttribute("message", "Error al registrar usuario: " + e.getMessage());
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/";
            }
            // Usuario nuevoUsuario = usuarioService.crearUsuario(nombreUsuario, email, password);

            redirectAttributes.addFlashAttribute("message", "Registro exitoso. Funcionalidad en desarrollo");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error al registrar usuario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/";
        }
    }

    /**
     * Muestra el dashboard del usuario
     * @param model Modelo para pasar datos a la vista
     * @return Vista del dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        System.out.println("\t Peticion GET de dashboard");
        // Aquí implementarás la vista del dashboard del usuario
        return "usuario/dashboard";
    }

    /**
     * Cierra la sesión del usuario
     * @param redirectAttributes Atributos para redirección
     * @return Redirección a la página principal
     */
    @PostMapping("/logout")
    public String logout(RedirectAttributes redirectAttributes) {
        System.out.println("\t Peticion POST de logout");
        redirectAttributes.addFlashAttribute("message", "Sesión cerrada exitosamente");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/";
    }
}

