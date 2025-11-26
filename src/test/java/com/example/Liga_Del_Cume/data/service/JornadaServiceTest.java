package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.Alineacion;
import com.example.Liga_Del_Cume.data.model.EstadisticaJugadorPartido;
import com.example.Liga_Del_Cume.data.model.Jornada;
import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.model.Partido;
import com.example.Liga_Del_Cume.data.repository.JornadaRepository;
import com.example.Liga_Del_Cume.data.repository.LigaCumeRepository;
import com.example.Liga_Del_Cume.data.service.exceptions.JornadaException;
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
public class JornadaServiceTest {

    @Mock
    private JornadaRepository jornadaRepository;

    @Mock
    private LigaCumeRepository ligaCumeRepository;

    @InjectMocks
    private JornadaService jornadaService;

    private LigaCume ligaExistente;
    private Jornada jornadaExistente;

    @BeforeEach
    void setUp() {
        ligaExistente = new LigaCume();
        ligaExistente.setIdLigaCume(1L);

        jornadaExistente = new Jornada();
        jornadaExistente.setIdJornada(5L);
        jornadaExistente.setLiga(ligaExistente);
    }

    // ==================== TESTS PARA crearJornada ====================

    @Test
    void crearJornada_ok() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));

        Jornada jornadaGuardada = new Jornada();
        jornadaGuardada.setIdJornada(10L);
        jornadaGuardada.setLiga(ligaExistente);
        when(jornadaRepository.save(any(Jornada.class))).thenReturn(jornadaGuardada);

        Jornada result = jornadaService.crearJornada(ligaExistente);

        assertNotNull(result);
        assertEquals(10L, result.getIdJornada());
        assertEquals(ligaExistente, result.getLiga());
        verify(jornadaRepository).save(any(Jornada.class));
    }

    @Test
    void crearJornada_ligaNula_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.crearJornada(null));
        assertTrue(ex.getMessage().toLowerCase().contains("liga") && ex.getMessage().toLowerCase().contains("nula"));
    }

    @Test
    void crearJornada_ligaSinId_debeFallar() {
        LigaCume ligaSinId = new LigaCume();
        ligaSinId.setIdLigaCume(null);

        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.crearJornada(ligaSinId));
        assertTrue(ex.getMessage().toLowerCase().contains("id válido"));
    }

    @Test
    void crearJornada_ligaIdNegativo_debeFallar() {
        LigaCume ligaInvalida = new LigaCume();
        ligaInvalida.setIdLigaCume(-5L);

        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.crearJornada(ligaInvalida));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void crearJornada_ligaIdCero_debeFallar() {
        LigaCume ligaInvalida = new LigaCume();
        ligaInvalida.setIdLigaCume(0L);

        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.crearJornada(ligaInvalida));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void crearJornada_ligaNoExiste_debeFallar() {
        LigaCume ligaInexistente = new LigaCume();
        ligaInexistente.setIdLigaCume(999L);
        when(ligaCumeRepository.findById(999L)).thenReturn(Optional.empty());

        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.crearJornada(ligaInexistente));
        assertTrue(ex.getMessage().contains("No existe ninguna liga"));
        verify(jornadaRepository, never()).save(any(Jornada.class));
    }

    // ==================== TESTS PARA obtenerJornada ====================

    @Test
    void obtenerJornada_ok() {
        when(jornadaRepository.findById(5L)).thenReturn(Optional.of(jornadaExistente));

        Jornada result = jornadaService.obtenerJornada(5L);

        assertNotNull(result);
        assertEquals(5L, result.getIdJornada());
        assertEquals(ligaExistente, result.getLiga());
    }

    @Test
    void obtenerJornada_idNulo_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.obtenerJornada(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void obtenerJornada_idNegativo_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.obtenerJornada(-3L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void obtenerJornada_idCero_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.obtenerJornada(0L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void obtenerJornada_noExiste_debeFallar() {
        when(jornadaRepository.findById(888L)).thenReturn(Optional.empty());

        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.obtenerJornada(888L));
        assertTrue(ex.getMessage().contains("No existe ninguna jornada"));
    }

    // ==================== TESTS PARA listarJornadasPorLiga ====================

    @Test
    void listarJornadasPorLiga_ok() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));
        List<Jornada> jornadas = new ArrayList<>();
        jornadas.add(jornadaExistente);
        jornadas.add(new Jornada());
        when(jornadaRepository.findByLigaIdLigaCumeOrderByIdJornadaAsc(1L)).thenReturn(jornadas);

        List<Jornada> result = jornadaService.listarJornadasPorLiga(1L);

        assertEquals(2, result.size());
        verify(jornadaRepository).findByLigaIdLigaCumeOrderByIdJornadaAsc(1L);
    }

    @Test
    void listarJornadasPorLiga_ligaIdNulo_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.listarJornadasPorLiga(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id de la liga") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void listarJornadasPorLiga_ligaIdNegativo_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.listarJornadasPorLiga(-2L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void listarJornadasPorLiga_ligaIdCero_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.listarJornadasPorLiga(0L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void listarJornadasPorLiga_ligaNoExiste_debeFallar() {
        when(ligaCumeRepository.findById(777L)).thenReturn(Optional.empty());

        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.listarJornadasPorLiga(777L));
        assertTrue(ex.getMessage().contains("No existe ninguna liga"));
    }

    @Test
    void listarJornadasPorLiga_vacio_ok() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));
        when(jornadaRepository.findByLigaIdLigaCumeOrderByIdJornadaAsc(1L)).thenReturn(new ArrayList<>());

        List<Jornada> result = jornadaService.listarJornadasPorLiga(1L);

        assertEquals(0, result.size());
    }

    // ==================== TESTS PARA eliminarJornada ====================

    @Test
    void eliminarJornada_ok() {
        Jornada jornadaVacia = new Jornada();
        jornadaVacia.setIdJornada(7L);
        jornadaVacia.setPartidos(new ArrayList<>());
        jornadaVacia.setAlineaciones(new ArrayList<>());

        when(jornadaRepository.findById(7L)).thenReturn(Optional.of(jornadaVacia));

        jornadaService.eliminarJornada(7L);

        verify(jornadaRepository).deleteById(7L);
    }

    @Test
    void eliminarJornada_idNulo_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.eliminarJornada(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void eliminarJornada_idNegativo_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.eliminarJornada(-8L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void eliminarJornada_idCero_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.eliminarJornada(0L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void eliminarJornada_noExiste_debeFallar() {
        when(jornadaRepository.findById(555L)).thenReturn(Optional.empty());

        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.eliminarJornada(555L));
        assertTrue(ex.getMessage().contains("No existe ninguna jornada"));
        verify(jornadaRepository, never()).deleteById(anyLong());
    }

    @Test
    void eliminarJornada_conPartidosConEstadisticas_debeFallar() {
        Jornada jornadaConPartidos = new Jornada();
        jornadaConPartidos.setIdJornada(8L);

        Partido partido = new Partido();
        partido.setIdPartido(100L);
        List<EstadisticaJugadorPartido> estadisticas = new ArrayList<>();
        estadisticas.add(new EstadisticaJugadorPartido());
        partido.setEstadisticas(estadisticas);

        List<Partido> partidos = new ArrayList<>();
        partidos.add(partido);
        jornadaConPartidos.setPartidos(partidos);
        jornadaConPartidos.setAlineaciones(new ArrayList<>());

        when(jornadaRepository.findById(8L)).thenReturn(Optional.of(jornadaConPartidos));

        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.eliminarJornada(8L));
        assertTrue(ex.getMessage().toLowerCase().contains("estadísticas"));
        verify(jornadaRepository, never()).deleteById(anyLong());
    }

    @Test
    void eliminarJornada_conAlineaciones_debeFallar() {
        Jornada jornadaConAlineaciones = new Jornada();
        jornadaConAlineaciones.setIdJornada(9L);
        jornadaConAlineaciones.setPartidos(new ArrayList<>());

        List<Alineacion> alineaciones = new ArrayList<>();
        alineaciones.add(new Alineacion());
        jornadaConAlineaciones.setAlineaciones(alineaciones);

        when(jornadaRepository.findById(9L)).thenReturn(Optional.of(jornadaConAlineaciones));

        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.eliminarJornada(9L));
        assertTrue(ex.getMessage().toLowerCase().contains("alineaciones"));
        verify(jornadaRepository, never()).deleteById(anyLong());
    }

    @Test
    void eliminarJornada_conPartidosSinEstadisticas_ok() {
        Jornada jornadaConPartidosSinEstadisticas = new Jornada();
        jornadaConPartidosSinEstadisticas.setIdJornada(11L);

        Partido partidoSinEstadisticas = new Partido();
        partidoSinEstadisticas.setIdPartido(101L);
        partidoSinEstadisticas.setEstadisticas(new ArrayList<>());

        List<Partido> partidos = new ArrayList<>();
        partidos.add(partidoSinEstadisticas);
        jornadaConPartidosSinEstadisticas.setPartidos(partidos);
        jornadaConPartidosSinEstadisticas.setAlineaciones(new ArrayList<>());

        when(jornadaRepository.findById(11L)).thenReturn(Optional.of(jornadaConPartidosSinEstadisticas));

        jornadaService.eliminarJornada(11L);

        verify(jornadaRepository).deleteById(11L);
    }

    @Test
    void eliminarJornada_partidosNull_ok() {
        Jornada jornadaPartidosNull = new Jornada();
        jornadaPartidosNull.setIdJornada(12L);
        jornadaPartidosNull.setPartidos(null);
        jornadaPartidosNull.setAlineaciones(null);

        when(jornadaRepository.findById(12L)).thenReturn(Optional.of(jornadaPartidosNull));

        jornadaService.eliminarJornada(12L);

        verify(jornadaRepository).deleteById(12L);
    }

    // ==================== TESTS PARA listarTodasLasJornadas ====================

    @Test
    void listarTodasLasJornadas_ok() {
        List<Jornada> jornadas = new ArrayList<>();
        jornadas.add(new Jornada());
        jornadas.add(new Jornada());
        jornadas.add(new Jornada());
        when(jornadaRepository.findAll()).thenReturn(jornadas);

        List<Jornada> result = jornadaService.listarTodasLasJornadas();

        assertEquals(3, result.size());
        verify(jornadaRepository).findAll();
    }

    @Test
    void listarTodasLasJornadas_vacio_ok() {
        when(jornadaRepository.findAll()).thenReturn(new ArrayList<>());

        List<Jornada> result = jornadaService.listarTodasLasJornadas();

        assertEquals(0, result.size());
    }

    // ==================== TESTS PARA tienePartidos ====================

    @Test
    void tienePartidos_conPartidos_true() {
        Jornada jornadaConPartidos = new Jornada();
        jornadaConPartidos.setIdJornada(13L);
        List<Partido> partidos = new ArrayList<>();
        partidos.add(new Partido());
        jornadaConPartidos.setPartidos(partidos);

        when(jornadaRepository.findById(13L)).thenReturn(Optional.of(jornadaConPartidos));

        boolean result = jornadaService.tienePartidos(13L);

        assertTrue(result);
    }

    @Test
    void tienePartidos_sinPartidos_false() {
        Jornada jornadaSinPartidos = new Jornada();
        jornadaSinPartidos.setIdJornada(14L);
        jornadaSinPartidos.setPartidos(new ArrayList<>());

        when(jornadaRepository.findById(14L)).thenReturn(Optional.of(jornadaSinPartidos));

        boolean result = jornadaService.tienePartidos(14L);

        assertFalse(result);
    }

    @Test
    void tienePartidos_partidosNull_false() {
        Jornada jornadaPartidosNull = new Jornada();
        jornadaPartidosNull.setIdJornada(15L);
        jornadaPartidosNull.setPartidos(null);

        when(jornadaRepository.findById(15L)).thenReturn(Optional.of(jornadaPartidosNull));

        boolean result = jornadaService.tienePartidos(15L);

        assertFalse(result);
    }

    @Test
    void tienePartidos_idNulo_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.tienePartidos(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void tienePartidos_idNegativo_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.tienePartidos(-4L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void tienePartidos_idCero_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.tienePartidos(0L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void tienePartidos_jornadaNoExiste_debeFallar() {
        when(jornadaRepository.findById(666L)).thenReturn(Optional.empty());

        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.tienePartidos(666L));
        assertTrue(ex.getMessage().contains("No existe ninguna jornada"));
    }

    // ==================== TESTS PARA contarPartidos ====================

    @Test
    void contarPartidos_conPartidos_retornaCantidad() {
        Jornada jornadaConPartidos = new Jornada();
        jornadaConPartidos.setIdJornada(16L);
        List<Partido> partidos = new ArrayList<>();
        partidos.add(new Partido());
        partidos.add(new Partido());
        partidos.add(new Partido());
        jornadaConPartidos.setPartidos(partidos);

        when(jornadaRepository.findById(16L)).thenReturn(Optional.of(jornadaConPartidos));

        int result = jornadaService.contarPartidos(16L);

        assertEquals(3, result);
    }

    @Test
    void contarPartidos_sinPartidos_retornaCero() {
        Jornada jornadaSinPartidos = new Jornada();
        jornadaSinPartidos.setIdJornada(17L);
        jornadaSinPartidos.setPartidos(new ArrayList<>());

        when(jornadaRepository.findById(17L)).thenReturn(Optional.of(jornadaSinPartidos));

        int result = jornadaService.contarPartidos(17L);

        assertEquals(0, result);
    }

    @Test
    void contarPartidos_partidosNull_retornaCero() {
        Jornada jornadaPartidosNull = new Jornada();
        jornadaPartidosNull.setIdJornada(18L);
        jornadaPartidosNull.setPartidos(null);

        when(jornadaRepository.findById(18L)).thenReturn(Optional.of(jornadaPartidosNull));

        int result = jornadaService.contarPartidos(18L);

        assertEquals(0, result);
    }

    @Test
    void contarPartidos_idNulo_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.contarPartidos(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void contarPartidos_idNegativo_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.contarPartidos(-6L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void contarPartidos_jornadaNoExiste_debeFallar() {
        when(jornadaRepository.findById(444L)).thenReturn(Optional.empty());

        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.contarPartidos(444L));
        assertTrue(ex.getMessage().contains("No existe ninguna jornada"));
    }

    // ==================== TESTS PARA esJornadaCompletada ====================

    @Test
    void esJornadaCompletada_todosConEstadisticas_true() {
        Jornada jornadaCompleta = new Jornada();
        jornadaCompleta.setIdJornada(19L);

        Partido partido1 = new Partido();
        List<EstadisticaJugadorPartido> estadisticas1 = new ArrayList<>();
        estadisticas1.add(new EstadisticaJugadorPartido());
        partido1.setEstadisticas(estadisticas1);

        Partido partido2 = new Partido();
        List<EstadisticaJugadorPartido> estadisticas2 = new ArrayList<>();
        estadisticas2.add(new EstadisticaJugadorPartido());
        partido2.setEstadisticas(estadisticas2);

        List<Partido> partidos = new ArrayList<>();
        partidos.add(partido1);
        partidos.add(partido2);
        jornadaCompleta.setPartidos(partidos);

        when(jornadaRepository.findById(19L)).thenReturn(Optional.of(jornadaCompleta));

        boolean result = jornadaService.esJornadaCompletada(19L);

        assertTrue(result);
    }

    @Test
    void esJornadaCompletada_algunSinEstadisticas_false() {
        Jornada jornadaIncompleta = new Jornada();
        jornadaIncompleta.setIdJornada(20L);

        Partido partido1 = new Partido();
        List<EstadisticaJugadorPartido> estadisticas1 = new ArrayList<>();
        estadisticas1.add(new EstadisticaJugadorPartido());
        partido1.setEstadisticas(estadisticas1);

        Partido partido2 = new Partido();
        partido2.setEstadisticas(new ArrayList<>()); // Sin estadísticas

        List<Partido> partidos = new ArrayList<>();
        partidos.add(partido1);
        partidos.add(partido2);
        jornadaIncompleta.setPartidos(partidos);

        when(jornadaRepository.findById(20L)).thenReturn(Optional.of(jornadaIncompleta));

        boolean result = jornadaService.esJornadaCompletada(20L);

        assertFalse(result);
    }

    @Test
    void esJornadaCompletada_sinPartidos_false() {
        Jornada jornadaSinPartidos = new Jornada();
        jornadaSinPartidos.setIdJornada(21L);
        jornadaSinPartidos.setPartidos(new ArrayList<>());

        when(jornadaRepository.findById(21L)).thenReturn(Optional.of(jornadaSinPartidos));

        boolean result = jornadaService.esJornadaCompletada(21L);

        assertFalse(result);
    }

    @Test
    void esJornadaCompletada_partidosNull_false() {
        Jornada jornadaPartidosNull = new Jornada();
        jornadaPartidosNull.setIdJornada(22L);
        jornadaPartidosNull.setPartidos(null);

        when(jornadaRepository.findById(22L)).thenReturn(Optional.of(jornadaPartidosNull));

        boolean result = jornadaService.esJornadaCompletada(22L);

        assertFalse(result);
    }

    @Test
    void esJornadaCompletada_partidoConEstadisticasNull_false() {
        Jornada jornada = new Jornada();
        jornada.setIdJornada(23L);

        Partido partido = new Partido();
        partido.setEstadisticas(null); // Estadísticas null

        List<Partido> partidos = new ArrayList<>();
        partidos.add(partido);
        jornada.setPartidos(partidos);

        when(jornadaRepository.findById(23L)).thenReturn(Optional.of(jornada));

        boolean result = jornadaService.esJornadaCompletada(23L);

        assertFalse(result);
    }

    @Test
    void esJornadaCompletada_idNulo_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.esJornadaCompletada(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void esJornadaCompletada_idNegativo_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.esJornadaCompletada(-9L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void esJornadaCompletada_jornadaNoExiste_debeFallar() {
        when(jornadaRepository.findById(333L)).thenReturn(Optional.empty());

        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.esJornadaCompletada(333L));
        assertTrue(ex.getMessage().contains("No existe ninguna jornada"));
    }

    // ==================== TESTS PARA contarJornadasPorLiga ====================

    @Test
    void contarJornadasPorLiga_conJornadas_retornaCantidad() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));
        List<Jornada> jornadas = new ArrayList<>();
        jornadas.add(new Jornada());
        jornadas.add(new Jornada());
        jornadas.add(new Jornada());
        jornadas.add(new Jornada());
        when(jornadaRepository.findByLigaIdLigaCume(1L)).thenReturn(jornadas);

        int result = jornadaService.contarJornadasPorLiga(1L);

        assertEquals(4, result);
    }

    @Test
    void contarJornadasPorLiga_sinJornadas_retornaCero() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));
        when(jornadaRepository.findByLigaIdLigaCume(1L)).thenReturn(new ArrayList<>());

        int result = jornadaService.contarJornadasPorLiga(1L);

        assertEquals(0, result);
    }

    @Test
    void contarJornadasPorLiga_idNulo_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.contarJornadasPorLiga(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id de la liga") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void contarJornadasPorLiga_idNegativo_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.contarJornadasPorLiga(-10L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void contarJornadasPorLiga_idCero_debeFallar() {
        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.contarJornadasPorLiga(0L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void contarJornadasPorLiga_ligaNoExiste_debeFallar() {
        when(ligaCumeRepository.findById(111L)).thenReturn(Optional.empty());

        JornadaException ex = assertThrows(JornadaException.class,
            () -> jornadaService.contarJornadasPorLiga(111L));
        assertTrue(ex.getMessage().contains("No existe ninguna liga"));
    }
}

