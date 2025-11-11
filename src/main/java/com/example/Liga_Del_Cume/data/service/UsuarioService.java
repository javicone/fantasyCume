package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.model.Usuario;
import com.example.Liga_Del_Cume.data.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar usuarios (managers) de las ligas fantasy
 * Funcionalidades:
 * - 1.1 a 1.4: Agregar, modificar, eliminar y listar usuarios
 * - 8: Consultar ranking de usuarios
 */
@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Da de alta un nuevo usuario en una liga
     */
    public Usuario darDeAltaUsuario(String nombre, LigaCume liga, int puntosIniciales) {
        Usuario usuario = new Usuario(nombre, puntosIniciales, liga);
        return usuarioRepository.save(usuario);
    }

    /**
     * Modifica la información de un usuario
     */
    public Usuario modificarUsuario(Long id, String nuevoNombre, Integer nuevosPuntos) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        if (nuevoNombre != null) {
            usuario.setNombreUsuario(nuevoNombre);
        }
        if (nuevosPuntos != null) {
            usuario.setPuntosAcumulados(nuevosPuntos);
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Elimina un usuario del sistema (las alineaciones se eliminan en cascada)
     */
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    /**
     * Lista todos los usuarios de una liga
     */
    public List<Usuario> listarUsuariosPorLiga(Long ligaId) {
        return usuarioRepository.findByLigaIdLigaCume(ligaId);
    }

    /**
     * Obtiene un usuario por su ID
     */
    public Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Actualiza los puntos acumulados de un usuario
     */
    public void actualizarPuntosAcumulados(Long usuarioId, int nuevosPuntos) {
        Usuario usuario = obtenerUsuario(usuarioId);
        usuario.setPuntosAcumulados(usuario.getPuntosAcumulados() + nuevosPuntos);
        usuarioRepository.save(usuario);
    }

    /**
     * Funcionalidad 8: Consultar ranking de todos los usuarios de una liga
     * Ordena por puntos acumulados de forma descendente
     */
    public List<Usuario> obtenerRankingLiga(Long ligaId) {
        return usuarioRepository.findByLigaIdLigaCumeOrderByPuntosAcumuladosDesc(ligaId);
    }

    /**
     * Lista todos los usuarios del sistema
     */
    public List<Usuario> listarTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
}

