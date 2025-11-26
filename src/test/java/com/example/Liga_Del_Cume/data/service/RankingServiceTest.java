package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.Alineacion;
import com.example.Liga_Del_Cume.data.model.Jornada;
import com.example.Liga_Del_Cume.data.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RankingServiceTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private AlineacionService alineacionService;

    @InjectMocks
    private RankingService rankingService;

    private Usuario usuario1;
    private Usuario usuario2;
    private Usuario usuario3;
    private Alineacion alineacion1;
    private Alineacion alineacion2;
    private Jornada jornada;

    @BeforeEach
    void setUp() {
        usuario1 = new Usuario();
        usuario1.setIdUsuario(1L);
        usuario1.setNombreUsuario("Usuario1");
        usuario1.setPuntosAcumulados(150);

        usuario2 = new Usuario();
        usuario2.setIdUsuario(2L);
        usuario2.setNombreUsuario("Usuario2");
        usuario2.setPuntosAcumulados(200);

        usuario3 = new Usuario();
        usuario3.setIdUsuario(3L);
        usuario3.setNombreUsuario("Usuario3");
        usuario3.setPuntosAcumulados(100);

        jornada = new Jornada();
        jornada.setIdJornada(5L);

        alineacion1 = new Alineacion();
        alineacion1.setIdAlineacion(10L);
        alineacion1.setUsuario(usuario1);
        alineacion1.setJornada(jornada);
        alineacion1.setPuntosTotalesJornada(80);
        alineacion1.setJugadores(new ArrayList<>());

        alineacion2 = new Alineacion();
        alineacion2.setIdAlineacion(11L);
        alineacion2.setUsuario(usuario2);
        alineacion2.setJornada(jornada);
        alineacion2.setPuntosTotalesJornada(120);
        alineacion2.setJugadores(new ArrayList<>());
    }

    // ==================== TESTS PARA obtenerRankingGeneral ====================

    @Test
    void obtenerRankingGeneral_ok() {
        List<Usuario> ranking = new ArrayList<>();
        ranking.add(usuario2); // 200 puntos
        ranking.add(usuario1); // 150 puntos
        ranking.add(usuario3); // 100 puntos
        when(usuarioService.obtenerRankingLiga(1L)).thenReturn(ranking);

        List<Usuario> result = rankingService.obtenerRankingGeneral(1L);

        assertEquals(3, result.size());
        assertEquals("Usuario2", result.get(0).getNombreUsuario());
        assertEquals("Usuario1", result.get(1).getNombreUsuario());
        assertEquals("Usuario3", result.get(2).getNombreUsuario());
    }

    @Test
    void obtenerRankingGeneral_idNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> rankingService.obtenerRankingGeneral(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id de la liga"));
    }

    @Test
    void obtenerRankingGeneral_sinUsuarios_debeFallar() {
        when(usuarioService.obtenerRankingLiga(1L)).thenReturn(new ArrayList<>());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> rankingService.obtenerRankingGeneral(1L));
        assertTrue(ex.getMessage().toLowerCase().contains("no hay usuarios"));
    }

    // ==================== TESTS PARA obtenerRankingJornada ====================

    @Test
    void obtenerRankingJornada_ok() {
        List<Alineacion> alineaciones = new ArrayList<>();
        alineaciones.add(alineacion2); // 120 puntos
        alineaciones.add(alineacion1); // 80 puntos
        when(alineacionService.listarAlineacionesPorJornada(5L)).thenReturn(alineaciones);

        List<Map<String, Object>> result = rankingService.obtenerRankingJornada(5L);

        assertEquals(2, result.size());
        assertEquals("Usuario2", result.get(0).get("usuario"));
        assertEquals(120, result.get(0).get("puntos"));
        assertEquals("Usuario1", result.get(1).get("usuario"));
        assertEquals(80, result.get(1).get("puntos"));
    }

    @Test
    void obtenerRankingJornada_idNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> rankingService.obtenerRankingJornada(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id de la jornada"));
    }

    @Test
    void obtenerRankingJornada_sinAlineaciones_debeFallar() {
        when(alineacionService.listarAlineacionesPorJornada(5L)).thenReturn(new ArrayList<>());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> rankingService.obtenerRankingJornada(5L));
        assertTrue(ex.getMessage().toLowerCase().contains("no hay alineaciones"));
    }

    @Test
    void obtenerRankingJornada_ordenadoDescendente() {
        Alineacion alineacion3 = new Alineacion();
        alineacion3.setUsuario(usuario3);
        alineacion3.setPuntosTotalesJornada(50);
        alineacion3.setJugadores(new ArrayList<>());

        List<Alineacion> alineaciones = new ArrayList<>();
        alineaciones.add(alineacion1); // 80 puntos
        alineaciones.add(alineacion3); // 50 puntos
        alineaciones.add(alineacion2); // 120 puntos
        when(alineacionService.listarAlineacionesPorJornada(5L)).thenReturn(alineaciones);

        List<Map<String, Object>> result = rankingService.obtenerRankingJornada(5L);

        assertEquals(3, result.size());
        assertEquals(120, result.get(0).get("puntos"));
        assertEquals(80, result.get(1).get("puntos"));
        assertEquals(50, result.get(2).get("puntos"));
    }

    // ==================== TESTS PARA obtenerDetallePuntuacionJornada ====================

    @Test
    void obtenerDetallePuntuacionJornada_ok() {
        when(alineacionService.consultarAlineacion(1L, 5L)).thenReturn(alineacion1);

        Map<String, Object> result = rankingService.obtenerDetallePuntuacionJornada(1L, 5L);

        assertNotNull(result);
        assertEquals("Usuario1", result.get("usuario"));
        assertEquals(5L, result.get("jornada"));
        assertEquals(80, result.get("puntosTotales"));
        assertNotNull(result.get("jugadores"));
    }

    @Test
    void obtenerDetallePuntuacionJornada_usuarioIdNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> rankingService.obtenerDetallePuntuacionJornada(null, 5L));
        assertTrue(ex.getMessage().toLowerCase().contains("id del usuario"));
    }

    @Test
    void obtenerDetallePuntuacionJornada_jornadaIdNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> rankingService.obtenerDetallePuntuacionJornada(1L, null));
        assertTrue(ex.getMessage().toLowerCase().contains("id de la jornada"));
    }

    @Test
    void obtenerDetallePuntuacionJornada_alineacionNoExiste_debeFallar() {
        when(alineacionService.consultarAlineacion(1L, 5L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> rankingService.obtenerDetallePuntuacionJornada(1L, 5L));
        assertTrue(ex.getMessage().toLowerCase().contains("no se encontró alineación"));
    }

    // ==================== TESTS PARA actualizarPuntosAcumulados ====================

    @Test
    void actualizarPuntosAcumulados_ok() {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(usuario1);
        usuarios.add(usuario2);
        when(usuarioService.listarUsuariosPorLiga(1L)).thenReturn(usuarios);

        List<Alineacion> alineacionesUsuario1 = new ArrayList<>();
        alineacionesUsuario1.add(alineacion1); // 80 puntos
        when(alineacionService.listarAlineacionesPorUsuario(1L)).thenReturn(alineacionesUsuario1);

        List<Alineacion> alineacionesUsuario2 = new ArrayList<>();
        alineacionesUsuario2.add(alineacion2); // 120 puntos
        when(alineacionService.listarAlineacionesPorUsuario(2L)).thenReturn(alineacionesUsuario2);

        when(usuarioService.modificarUsuario(anyLong(), isNull(), anyInt())).thenReturn(usuario1);

        rankingService.actualizarPuntosAcumulados(1L);

        verify(usuarioService).modificarUsuario(1L, null, 80);
        verify(usuarioService).modificarUsuario(2L, null, 120);
    }

    @Test
    void actualizarPuntosAcumulados_idNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> rankingService.actualizarPuntosAcumulados(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id de la liga"));
    }

    @Test
    void actualizarPuntosAcumulados_sinUsuarios_debeFallar() {
        when(usuarioService.listarUsuariosPorLiga(1L)).thenReturn(new ArrayList<>());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> rankingService.actualizarPuntosAcumulados(1L));
        assertTrue(ex.getMessage().toLowerCase().contains("no hay usuarios"));
    }

    @Test
    void actualizarPuntosAcumulados_usuarioSinAlineaciones() {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(usuario1);
        when(usuarioService.listarUsuariosPorLiga(1L)).thenReturn(usuarios);

        when(alineacionService.listarAlineacionesPorUsuario(1L)).thenReturn(new ArrayList<>());
        when(usuarioService.modificarUsuario(1L, null, 0)).thenReturn(usuario1);

        rankingService.actualizarPuntosAcumulados(1L);

        verify(usuarioService).modificarUsuario(1L, null, 0);
    }

    @Test
    void actualizarPuntosAcumulados_multipleAlineaciones() {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(usuario1);
        when(usuarioService.listarUsuariosPorLiga(1L)).thenReturn(usuarios);

        Alineacion alineacion3 = new Alineacion();
        alineacion3.setPuntosTotalesJornada(70);

        List<Alineacion> alineaciones = new ArrayList<>();
        alineaciones.add(alineacion1); // 80 puntos
        alineaciones.add(alineacion3); // 70 puntos
        when(alineacionService.listarAlineacionesPorUsuario(1L)).thenReturn(alineaciones);

        when(usuarioService.modificarUsuario(1L, null, 150)).thenReturn(usuario1);

        rankingService.actualizarPuntosAcumulados(1L);

        verify(usuarioService).modificarUsuario(1L, null, 150);
    }

    // ==================== TESTS PARA obtenerPosicionUsuario ====================

    @Test
    void obtenerPosicionUsuario_primeraPos() {
        List<Usuario> ranking = new ArrayList<>();
        ranking.add(usuario2); // 1º
        ranking.add(usuario1); // 2º
        ranking.add(usuario3); // 3º
        when(usuarioService.obtenerRankingLiga(1L)).thenReturn(ranking);

        int posicion = rankingService.obtenerPosicionUsuario(1L, 2L);

        assertEquals(1, posicion);
    }

    @Test
    void obtenerPosicionUsuario_segundaPos() {
        List<Usuario> ranking = new ArrayList<>();
        ranking.add(usuario2); // 1º
        ranking.add(usuario1); // 2º
        ranking.add(usuario3); // 3º
        when(usuarioService.obtenerRankingLiga(1L)).thenReturn(ranking);

        int posicion = rankingService.obtenerPosicionUsuario(1L, 1L);

        assertEquals(2, posicion);
    }

    @Test
    void obtenerPosicionUsuario_ultimaPos() {
        List<Usuario> ranking = new ArrayList<>();
        ranking.add(usuario2); // 1º
        ranking.add(usuario1); // 2º
        ranking.add(usuario3); // 3º
        when(usuarioService.obtenerRankingLiga(1L)).thenReturn(ranking);

        int posicion = rankingService.obtenerPosicionUsuario(1L, 3L);

        assertEquals(3, posicion);
    }

    @Test
    void obtenerPosicionUsuario_ligaIdNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> rankingService.obtenerPosicionUsuario(null, 1L));
        assertTrue(ex.getMessage().toLowerCase().contains("id de la liga"));
    }

    @Test
    void obtenerPosicionUsuario_usuarioIdNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> rankingService.obtenerPosicionUsuario(1L, null));
        assertTrue(ex.getMessage().toLowerCase().contains("id del usuario"));
    }

    @Test
    void obtenerPosicionUsuario_usuarioNoExiste_debeFallar() {
        List<Usuario> ranking = new ArrayList<>();
        ranking.add(usuario1);
        ranking.add(usuario2);
        when(usuarioService.obtenerRankingLiga(1L)).thenReturn(ranking);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> rankingService.obtenerPosicionUsuario(1L, 999L));
        assertTrue(ex.getMessage().toLowerCase().contains("no encontrado"));
    }

    // ==================== TESTS PARA obtenerTopUsuarios ====================

    @Test
    void obtenerTopUsuarios_top3() {
        List<Usuario> ranking = new ArrayList<>();
        ranking.add(usuario2);
        ranking.add(usuario1);
        ranking.add(usuario3);
        when(usuarioService.obtenerRankingLiga(1L)).thenReturn(ranking);

        List<Usuario> result = rankingService.obtenerTopUsuarios(1L, 3);

        assertEquals(3, result.size());
        assertEquals("Usuario2", result.get(0).getNombreUsuario());
    }

    @Test
    void obtenerTopUsuarios_top1() {
        List<Usuario> ranking = new ArrayList<>();
        ranking.add(usuario2);
        ranking.add(usuario1);
        ranking.add(usuario3);
        when(usuarioService.obtenerRankingLiga(1L)).thenReturn(ranking);

        List<Usuario> result = rankingService.obtenerTopUsuarios(1L, 1);

        assertEquals(1, result.size());
        assertEquals("Usuario2", result.get(0).getNombreUsuario());
    }

    @Test
    void obtenerTopUsuarios_topMayorQueTotal() {
        List<Usuario> ranking = new ArrayList<>();
        ranking.add(usuario2);
        ranking.add(usuario1);
        when(usuarioService.obtenerRankingLiga(1L)).thenReturn(ranking);

        List<Usuario> result = rankingService.obtenerTopUsuarios(1L, 10);

        assertEquals(2, result.size());
    }

    @Test
    void obtenerTopUsuarios_ligaIdNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> rankingService.obtenerTopUsuarios(null, 3));
        assertTrue(ex.getMessage().toLowerCase().contains("id de la liga"));
    }

    @Test
    void obtenerTopUsuarios_topNCero_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> rankingService.obtenerTopUsuarios(1L, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void obtenerTopUsuarios_topNNegativo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> rankingService.obtenerTopUsuarios(1L, -5));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }
}

