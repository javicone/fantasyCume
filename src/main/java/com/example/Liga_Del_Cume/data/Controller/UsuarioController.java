package com.example.Liga_Del_Cume.data.Controller;

import com.example.Liga_Del_Cume.data.exceptions.UsuarioException;
import com.example.Liga_Del_Cume.data.model.Usuario;
import com.example.Liga_Del_Cume.data.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    public UsuarioController() {
        System.out.println("\t Builder of " + this.getClass().getSimpleName());
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model,
                        RedirectAttributes redirectAttributes,
                        HttpSession session) { // <--- Inyectar HttpSession)

        System.out.println("\t Peticion POST de login - Email: " + email);

        try {
            Usuario user = usuarioService.autenticarUsuario(email, password);
            if(user != null){
                redirectAttributes.addFlashAttribute("message", "Inicio de sesión exitoso. Bienvenido " + user.getNombreUsuario());
                redirectAttributes.addFlashAttribute("messageType", "success");
                model.addAttribute("usuario", user);
                session.setAttribute("usuario", user);
                // Si el usuario tiene liga, lo mandamos a su historial de alineaciones y alineación futura
                if (user.getLiga() != null) {
                    // Redirige a la pantalla principal de la liga, pasando el idUsuario en la URL
                    return "redirect:/liga/" + user.getLiga().getIdLigaCume() + "/ranking";
                } else {
                    // Si no tiene liga, lo mandamos al selector de ligas pasando su ID
                    return "redirect:/liga?usuarioId=" + user.getIdUsuario();
                }
            }

        } catch (UsuarioException e) {
            redirectAttributes.addFlashAttribute("message", "Credenciales inválidas");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        return "redirect:/";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombreUsuario,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String passwordConfirm,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        System.out.println("\t Peticion POST de registro - Usuario: " + nombreUsuario);

        try {
            if (!password.equals(passwordConfirm)) {
                redirectAttributes.addFlashAttribute("message", "Las contraseñas no coinciden");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/";
            }
            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("message", "La contraseña debe tener al menos 6 caracteres");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/";
            }

            try{
                Usuario user = usuarioService.darDeAltaUsuario(nombreUsuario, email, password);
                if(user!=null) {
                    redirectAttributes.addFlashAttribute("message", "Registro exitoso. Bienvenido " + user.getNombreUsuario());
                    redirectAttributes.addFlashAttribute("messageType", "success");
                    return "redirect:/";
                }
            } catch (UsuarioException e) {
                redirectAttributes.addFlashAttribute("message", "Error al registrar usuario: " + e.getMessage());
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/";
            }

            return "redirect:/";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error al registrar usuario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/";
        }
    }



    @PostMapping("/logout")
    public String logout(RedirectAttributes redirectAttributes,
                         HttpSession session) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("message", "Sesión cerrada exitosamente");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/";
    }
    @PostMapping("/eliminar")
    public String abandonarLiga(RedirectAttributes redirectAttributes,
                                @RequestParam (required = false) Long ligaId,
                                @RequestParam (required = false) Long usuarioId,
                                HttpSession session) {
        try {
            usuarioService.eliminarUsuario(usuarioId);
            redirectAttributes.addFlashAttribute("message", "Usuario eliminado exitosamente");
            redirectAttributes.addFlashAttribute("messageType", "success");
    }

        catch (UsuarioException e) {
            redirectAttributes.addFlashAttribute("message", "Error al abandonar la liga: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        catch (Exception e) {
        System.out.println("aaaa error al borrar ");
        return "redirect:/";
        }
        finally{
            session.invalidate();
        }
        return "redirect:/";
    }
}