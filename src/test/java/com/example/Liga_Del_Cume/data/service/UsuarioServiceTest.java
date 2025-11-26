package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.model.Usuario;
import com.example.Liga_Del_Cume.data.repository.LigaCumeRepository;
import com.example.Liga_Del_Cume.data.repository.UsuarioRepository;
import com.example.Liga_Del_Cume.data.service.exceptions.UsuarioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LigaCumeRepository ligaCumeRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private LigaCume ligaExistente;
    private Usuario usuarioExistente;

    @BeforeEach
    void setUp() {
        ligaExistente = new LigaCume();
        ligaExistente.setIdLigaCume(1L);
        ligaExistente.setNombreLiga("Liga Cume 2024");

        usuarioExistente = new Usuario();
        usuarioExistente.setIdUsuario(10L);
        usuarioExistente.setNombreUsuario("Usuario1");
        usuarioExistente.setPuntosAcumulados(150);
        usuarioExistente.setLiga(ligaExistente);
    }

    // ==================== TESTS PARA darDeAltaUsuario ====================

    @Test
    void darDeAltaUsuario_ok() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));
        when(usuarioRepository.findByNombreUsuario("NuevoUsuario")).thenReturn(Optional.empty());

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setIdUsuario(15L);
        usuarioGuardado.setNombreUsuario("NuevoUsuario");
        usuarioGuardado.setPuntosAcumulados(0);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        Usuario result = usuarioService.darDeAltaUsuario("NuevoUsuario", ligaExistente, 0);

        assertNotNull(result);
        assertEquals(15L, result.getIdUsuario());
        assertEquals("NuevoUsuario", result.getNombreUsuario());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void darDeAltaUsuario_nombreNulo_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.darDeAltaUsuario(null, ligaExistente, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("nombre"));
    }

    @Test
    void darDeAltaUsuario_nombreVacio_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.darDeAltaUsuario("", ligaExistente, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("nombre"));
    }

    @Test
    void darDeAltaUsuario_nombreSoloEspacios_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.darDeAltaUsuario("   ", ligaExistente, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("nombre"));
    }

    @Test
    void darDeAltaUsuario_nombreMuyCorto_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.darDeAltaUsuario("Ab", ligaExistente, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("al menos 3 caracteres"));
    }

    @Test
    void darDeAltaUsuario_ligaNula_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.darDeAltaUsuario("Usuario", null, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("liga"));
    }

    @Test
    void darDeAltaUsuario_ligaSinId_debeFallar() {
        LigaCume ligaSinId = new LigaCume();
        ligaSinId.setIdLigaCume(null);

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.darDeAltaUsuario("Usuario", ligaSinId, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void darDeAltaUsuario_ligaIdNegativo_debeFallar() {
        LigaCume ligaInvalida = new LigaCume();
        ligaInvalida.setIdLigaCume(-5L);

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.darDeAltaUsuario("Usuario", ligaInvalida, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void darDeAltaUsuario_ligaIdCero_debeFallar() {
        LigaCume ligaInvalida = new LigaCume();
        ligaInvalida.setIdLigaCume(0L);

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.darDeAltaUsuario("Usuario", ligaInvalida, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void darDeAltaUsuario_ligaNoExiste_debeFallar() {
        LigaCume ligaInexistente = new LigaCume();
        ligaInexistente.setIdLigaCume(999L);
        when(ligaCumeRepository.findById(999L)).thenReturn(Optional.empty());

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.darDeAltaUsuario("Usuario", ligaInexistente, 0));
        assertTrue(ex.getMessage().contains("No existe ninguna liga"));
    }

    @Test
    void darDeAltaUsuario_nombreDuplicadoMismaLiga_debeFallar() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));
        when(usuarioRepository.findByNombreUsuario("Usuario1")).thenReturn(Optional.of(usuarioExistente));

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.darDeAltaUsuario("Usuario1", ligaExistente, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("ya existe"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void darDeAltaUsuario_nombreDuplicadoOtraLiga_ok() {
        LigaCume otraLiga = new LigaCume();
        otraLiga.setIdLigaCume(2L);

        Usuario usuarioOtraLiga = new Usuario();
        usuarioOtraLiga.setNombreUsuario("Usuario1");
        usuarioOtraLiga.setLiga(otraLiga);

        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));
        when(usuarioRepository.findByNombreUsuario("Usuario1")).thenReturn(Optional.of(usuarioOtraLiga));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        Usuario result = usuarioService.darDeAltaUsuario("Usuario1", ligaExistente, 0);

        assertNotNull(result);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void darDeAltaUsuario_puntosInicialesNegativos_debeFallar() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.darDeAltaUsuario("Usuario", ligaExistente, -10));
        assertTrue(ex.getMessage().toLowerCase().contains("puntos") && ex.getMessage().toLowerCase().contains("negativo"));
    }

    @Test
    void darDeAltaUsuario_puntosInicialesPositivos_ok() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));
        when(usuarioRepository.findByNombreUsuario("Usuario")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        Usuario result = usuarioService.darDeAltaUsuario("Usuario", ligaExistente, 100);

        assertNotNull(result);
    }

    // ==================== TESTS PARA modificarUsuario ====================

    @Test
    void modificarUsuario_okCambiarNombre() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.findByNombreUsuario("NuevoNombre")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario result = usuarioService.modificarUsuario(10L, "NuevoNombre", null);

        assertEquals("NuevoNombre", result.getNombreUsuario());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void modificarUsuario_okCambiarPuntos() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario result = usuarioService.modificarUsuario(10L, null, 250);

        assertEquals(250, result.getPuntosAcumulados());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void modificarUsuario_okCambiarAmbos() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.findByNombreUsuario("Nuevo")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario result = usuarioService.modificarUsuario(10L, "Nuevo", 300);

        assertEquals("Nuevo", result.getNombreUsuario());
        assertEquals(300, result.getPuntosAcumulados());
    }

    @Test
    void modificarUsuario_idNulo_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.modificarUsuario(null, "Nombre", 100));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void modificarUsuario_idNegativo_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.modificarUsuario(-5L, "Nombre", 100));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void modificarUsuario_idCero_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.modificarUsuario(0L, "Nombre", 100));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void modificarUsuario_usuarioNoExiste_debeFallar() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.modificarUsuario(999L, "Nombre", 100));
        assertTrue(ex.getMessage().contains("No existe ningún usuario"));
    }

    @Test
    void modificarUsuario_nombreMuyCorto_debeFallar() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioExistente));

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.modificarUsuario(10L, "Ab", null));
        assertTrue(ex.getMessage().toLowerCase().contains("al menos 3 caracteres"));
    }

    @Test
    void modificarUsuario_nombreDuplicado_debeFallar() {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setIdUsuario(20L);
        otroUsuario.setNombreUsuario("Usuario2");
        otroUsuario.setLiga(ligaExistente);

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.findByNombreUsuario("Usuario2")).thenReturn(Optional.of(otroUsuario));

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.modificarUsuario(10L, "Usuario2", null));
        assertTrue(ex.getMessage().toLowerCase().contains("ya existe"));
    }

    @Test
    void modificarUsuario_mismoNombre_ok() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.findByNombreUsuario("Usuario1")).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario result = usuarioService.modificarUsuario(10L, "Usuario1", 200);

        assertEquals("Usuario1", result.getNombreUsuario());
        assertEquals(200, result.getPuntosAcumulados());
    }

    @Test
    void modificarUsuario_puntosNegativos_debeFallar() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioExistente));

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.modificarUsuario(10L, null, -50));
        assertTrue(ex.getMessage().toLowerCase().contains("puntos") && ex.getMessage().toLowerCase().contains("negativo"));
    }

    @Test
    void modificarUsuario_sinCambios_debeFallar() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioExistente));

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.modificarUsuario(10L, null, null));
        assertTrue(ex.getMessage().toLowerCase().contains("debe proporcionar"));
    }

    @Test
    void modificarUsuario_nombreVacio_noCambia() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioExistente));

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.modificarUsuario(10L, "", null));
        assertTrue(ex.getMessage().toLowerCase().contains("debe proporcionar"));
    }

    // ==================== TESTS PARA eliminarUsuario ====================

    @Test
    void eliminarUsuario_ok() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioExistente));

        usuarioService.eliminarUsuario(10L);

        verify(usuarioRepository).delete(usuarioExistente);
    }

    @Test
    void eliminarUsuario_idNulo_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.eliminarUsuario(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void eliminarUsuario_idNegativo_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.eliminarUsuario(-3L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void eliminarUsuario_usuarioNoExiste_debeFallar() {
        when(usuarioRepository.findById(888L)).thenReturn(Optional.empty());

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.eliminarUsuario(888L));
        assertTrue(ex.getMessage().contains("No existe ningún usuario"));
    }

    // ==================== TESTS PARA listarUsuariosPorLiga ====================

    @Test
    void listarUsuariosPorLiga_ok() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(usuarioExistente);
        usuarios.add(new Usuario());
        when(usuarioRepository.findByLigaIdLigaCume(1L)).thenReturn(usuarios);

        List<Usuario> result = usuarioService.listarUsuariosPorLiga(1L);

        assertEquals(2, result.size());
    }

    @Test
    void listarUsuariosPorLiga_idNulo_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.listarUsuariosPorLiga(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void listarUsuariosPorLiga_idNegativo_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.listarUsuariosPorLiga(-2L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void listarUsuariosPorLiga_ligaNoExiste_debeFallar() {
        when(ligaCumeRepository.findById(777L)).thenReturn(Optional.empty());

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.listarUsuariosPorLiga(777L));
        assertTrue(ex.getMessage().contains("No existe ninguna liga"));
    }

    @Test
    void listarUsuariosPorLiga_vacio_ok() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));
        when(usuarioRepository.findByLigaIdLigaCume(1L)).thenReturn(new ArrayList<>());

        List<Usuario> result = usuarioService.listarUsuariosPorLiga(1L);

        assertEquals(0, result.size());
    }

    // ==================== TESTS PARA obtenerUsuario ====================

    @Test
    void obtenerUsuario_ok() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioExistente));

        Usuario result = usuarioService.obtenerUsuario(10L);

        assertNotNull(result);
        assertEquals(10L, result.getIdUsuario());
    }

    @Test
    void obtenerUsuario_idNulo_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.obtenerUsuario(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void obtenerUsuario_idNegativo_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.obtenerUsuario(-7L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void obtenerUsuario_noExiste_debeFallar() {
        when(usuarioRepository.findById(555L)).thenReturn(Optional.empty());

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.obtenerUsuario(555L));
        assertTrue(ex.getMessage().contains("No existe ningún usuario"));
    }

    // ==================== TESTS PARA actualizarPuntosAcumulados ====================

    @Test
    void actualizarPuntosAcumulados_sumarPositivos() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        usuarioService.actualizarPuntosAcumulados(10L, 50);

        assertEquals(200, usuarioExistente.getPuntosAcumulados()); // 150 + 50
        verify(usuarioRepository).save(usuarioExistente);
    }

    @Test
    void actualizarPuntosAcumulados_sumarNegativos() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        usuarioService.actualizarPuntosAcumulados(10L, -30);

        assertEquals(120, usuarioExistente.getPuntosAcumulados()); // 150 - 30
        verify(usuarioRepository).save(usuarioExistente);
    }

    @Test
    void actualizarPuntosAcumulados_resultadoNegativo_debeFallar() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioExistente));

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.actualizarPuntosAcumulados(10L, -200));
        assertTrue(ex.getMessage().toLowerCase().contains("resultado") && ex.getMessage().toLowerCase().contains("negativo"));
    }

    @Test
    void actualizarPuntosAcumulados_sumarCero() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        usuarioService.actualizarPuntosAcumulados(10L, 0);

        assertEquals(150, usuarioExistente.getPuntosAcumulados()); // Sin cambio
        verify(usuarioRepository).save(usuarioExistente);
    }

    // ==================== TESTS PARA obtenerRankingLiga ====================

    @Test
    void obtenerRankingLiga_ok() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));

        Usuario usuario2 = new Usuario();
        usuario2.setPuntosAcumulados(200);
        Usuario usuario3 = new Usuario();
        usuario3.setPuntosAcumulados(100);

        List<Usuario> ranking = new ArrayList<>();
        ranking.add(usuario2);
        ranking.add(usuarioExistente);
        ranking.add(usuario3);
        when(usuarioRepository.findByLigaIdLigaCumeOrderByPuntosAcumuladosDesc(1L)).thenReturn(ranking);

        List<Usuario> result = usuarioService.obtenerRankingLiga(1L);

        assertEquals(3, result.size());
        assertEquals(200, result.get(0).getPuntosAcumulados());
        assertEquals(150, result.get(1).getPuntosAcumulados());
        assertEquals(100, result.get(2).getPuntosAcumulados());
    }

    @Test
    void obtenerRankingLiga_idNulo_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.obtenerRankingLiga(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void obtenerRankingLiga_ligaNoExiste_debeFallar() {
        when(ligaCumeRepository.findById(666L)).thenReturn(Optional.empty());

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.obtenerRankingLiga(666L));
        assertTrue(ex.getMessage().contains("No existe ninguna liga"));
    }

    // ==================== TESTS PARA listarTodosLosUsuarios ====================

    @Test
    void listarTodosLosUsuarios_ok() {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(usuarioExistente);
        usuarios.add(new Usuario());
        usuarios.add(new Usuario());
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        List<Usuario> result = usuarioService.listarTodosLosUsuarios();

        assertEquals(3, result.size());
    }

    @Test
    void listarTodosLosUsuarios_vacio_ok() {
        when(usuarioRepository.findAll()).thenReturn(new ArrayList<>());

        List<Usuario> result = usuarioService.listarTodosLosUsuarios();

        assertEquals(0, result.size());
    }

    // ==================== TESTS PARA buscarUsuarioPorNombre ====================

    @Test
    void buscarUsuarioPorNombre_ok() {
        when(usuarioRepository.findByNombreUsuario("Usuario1")).thenReturn(Optional.of(usuarioExistente));

        Usuario result = usuarioService.buscarUsuarioPorNombre("Usuario1");

        assertNotNull(result);
        assertEquals("Usuario1", result.getNombreUsuario());
    }

    @Test
    void buscarUsuarioPorNombre_noExiste_retornaNull() {
        when(usuarioRepository.findByNombreUsuario("NoExiste")).thenReturn(Optional.empty());

        Usuario result = usuarioService.buscarUsuarioPorNombre("NoExiste");

        assertNull(result);
    }

    @Test
    void buscarUsuarioPorNombre_nombreNulo_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.buscarUsuarioPorNombre(null));
        assertTrue(ex.getMessage().toLowerCase().contains("nombre"));
    }

    @Test
    void buscarUsuarioPorNombre_nombreVacio_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.buscarUsuarioPorNombre(""));
        assertTrue(ex.getMessage().toLowerCase().contains("nombre"));
    }

    // ==================== TESTS PARA contarUsuariosPorLiga ====================

    @Test
    void contarUsuariosPorLiga_ok() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(usuarioExistente);
        usuarios.add(new Usuario());
        usuarios.add(new Usuario());
        usuarios.add(new Usuario());
        when(usuarioRepository.findByLigaIdLigaCume(1L)).thenReturn(usuarios);

        int result = usuarioService.contarUsuariosPorLiga(1L);

        assertEquals(4, result);
    }

    @Test
    void contarUsuariosPorLiga_sinUsuarios_retornaCero() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));
        when(usuarioRepository.findByLigaIdLigaCume(1L)).thenReturn(new ArrayList<>());

        int result = usuarioService.contarUsuariosPorLiga(1L);

        assertEquals(0, result);
    }

    @Test
    void contarUsuariosPorLiga_idNulo_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.contarUsuariosPorLiga(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void contarUsuariosPorLiga_ligaNoExiste_debeFallar() {
        when(ligaCumeRepository.findById(444L)).thenReturn(Optional.empty());

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.contarUsuariosPorLiga(444L));
        assertTrue(ex.getMessage().contains("No existe ninguna liga"));
    }

    // ==================== TESTS PARA resetearPuntos ====================

    @Test
    void resetearPuntos_ok() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        usuarioService.resetearPuntos(10L);

        assertEquals(0, usuarioExistente.getPuntosAcumulados());
        verify(usuarioRepository).save(usuarioExistente);
    }

    @Test
    void resetearPuntos_idNulo_debeFallar() {
        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.resetearPuntos(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void resetearPuntos_usuarioNoExiste_debeFallar() {
        when(usuarioRepository.findById(333L)).thenReturn(Optional.empty());

        UsuarioException ex = assertThrows(UsuarioException.class,
            () -> usuarioService.resetearPuntos(333L));
        assertTrue(ex.getMessage().contains("No existe ningún usuario"));
    }
}

