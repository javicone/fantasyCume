package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.Equipo;
import com.example.Liga_Del_Cume.data.model.Jornada;
import com.example.Liga_Del_Cume.data.model.Partido;
import com.example.Liga_Del_Cume.data.repository.EquipoRepository;
import com.example.Liga_Del_Cume.data.repository.JornadaRepository;
import com.example.Liga_Del_Cume.data.repository.PartidoRepository;
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
public class PartidoServiceTest {

    @Mock
    private PartidoRepository partidoRepository;

    @Mock
    private EquipoRepository equipoRepository;

    @Mock
    private JornadaRepository jornadaRepository;

    @InjectMocks
    private PartidoService partidoService;

    private Equipo equipoLocal;
    private Equipo equipoVisitante;
    private Jornada jornadaExistente;
    private Partido partidoExistente;

    @BeforeEach
    void setUp() {
        equipoLocal = new Equipo();
        equipoLocal.setIdEquipo(1L);
        equipoLocal.setNombreEquipo("Real Cume");

        equipoVisitante = new Equipo();
        equipoVisitante.setIdEquipo(2L);
        equipoVisitante.setNombreEquipo("Barcelona Cume");

        jornadaExistente = new Jornada();
        jornadaExistente.setIdJornada(5L);

        partidoExistente = new Partido();
        partidoExistente.setIdPartido(10L);
        partidoExistente.setEquipoLocal(equipoLocal);
        partidoExistente.setEquipoVisitante(equipoVisitante);
        partidoExistente.setGolesLocal(2);
        partidoExistente.setGolesVisitante(1);
        partidoExistente.setJornada(jornadaExistente);
    }

    // ==================== TESTS PARA agregarPartido ====================

    @Test
    void agregarPartido_ok() {
        when(equipoRepository.existsById(1L)).thenReturn(true);
        when(equipoRepository.existsById(2L)).thenReturn(true);
        when(jornadaRepository.existsById(5L)).thenReturn(true);
        when(partidoRepository.findByJornadaIdJornada(5L)).thenReturn(new ArrayList<>());

        Partido partidoGuardado = new Partido();
        partidoGuardado.setIdPartido(15L);
        when(partidoRepository.save(any(Partido.class))).thenReturn(partidoGuardado);

        Partido result = partidoService.agregarPartido(equipoLocal, equipoVisitante, 3, 2, jornadaExistente);

        assertNotNull(result);
        assertEquals(15L, result.getIdPartido());
        verify(partidoRepository).save(any(Partido.class));
    }

    @Test
    void agregarPartido_equipoLocalNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> partidoService.agregarPartido(null, equipoVisitante, 1, 0, jornadaExistente));
        assertTrue(ex.getMessage().toLowerCase().contains("equipo local"));
    }

    @Test
    void agregarPartido_equipoVisitanteNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> partidoService.agregarPartido(equipoLocal, null, 1, 0, jornadaExistente));
        assertTrue(ex.getMessage().toLowerCase().contains("equipo visitante"));
    }

    @Test
    void agregarPartido_mismoEquipo_debeFallar() {
        when(equipoRepository.existsById(1L)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> partidoService.agregarPartido(equipoLocal, equipoLocal, 1, 0, jornadaExistente));
        assertTrue(ex.getMessage().toLowerCase().contains("no puede jugar contra sí mismo"));
    }

    @Test
    void agregarPartido_equipoLocalNoExiste_debeFallar() {
        when(equipoRepository.existsById(1L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> partidoService.agregarPartido(equipoLocal, equipoVisitante, 1, 0, jornadaExistente));
        assertTrue(ex.getMessage().toLowerCase().contains("equipo local no existe"));
    }

    @Test
    void agregarPartido_equipoVisitanteNoExiste_debeFallar() {
        when(equipoRepository.existsById(1L)).thenReturn(true);
        when(equipoRepository.existsById(2L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> partidoService.agregarPartido(equipoLocal, equipoVisitante, 1, 0, jornadaExistente));
        assertTrue(ex.getMessage().toLowerCase().contains("equipo visitante no existe"));
    }

    @Test
    void agregarPartido_jornadaNula_debeFallar() {
        when(equipoRepository.existsById(1L)).thenReturn(true);
        when(equipoRepository.existsById(2L)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> partidoService.agregarPartido(equipoLocal, equipoVisitante, 1, 0, null));
        assertTrue(ex.getMessage().toLowerCase().contains("jornada"));
    }

    @Test
    void agregarPartido_jornadaNoExiste_debeFallar() {
        when(equipoRepository.existsById(1L)).thenReturn(true);
        when(equipoRepository.existsById(2L)).thenReturn(true);
        when(jornadaRepository.existsById(5L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> partidoService.agregarPartido(equipoLocal, equipoVisitante, 1, 0, jornadaExistente));
        assertTrue(ex.getMessage().toLowerCase().contains("jornada no existe"));
    }

    @Test
    void agregarPartido_golesLocalNegativos_debeFallar() {
        when(equipoRepository.existsById(1L)).thenReturn(true);
        when(equipoRepository.existsById(2L)).thenReturn(true);
        when(jornadaRepository.existsById(5L)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> partidoService.agregarPartido(equipoLocal, equipoVisitante, -1, 0, jornadaExistente));
        assertTrue(ex.getMessage().toLowerCase().contains("goles") && ex.getMessage().toLowerCase().contains("local"));
    }

    @Test
    void agregarPartido_golesVisitanteNegativos_debeFallar() {
        when(equipoRepository.existsById(1L)).thenReturn(true);
        when(equipoRepository.existsById(2L)).thenReturn(true);
        when(jornadaRepository.existsById(5L)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> partidoService.agregarPartido(equipoLocal, equipoVisitante, 0, -2, jornadaExistente));
        assertTrue(ex.getMessage().toLowerCase().contains("goles") && ex.getMessage().toLowerCase().contains("visitante"));
    }

    @Test
    void agregarPartido_partidoDuplicado_debeFallar() {
        when(equipoRepository.existsById(1L)).thenReturn(true);
        when(equipoRepository.existsById(2L)).thenReturn(true);
        when(jornadaRepository.existsById(5L)).thenReturn(true);

        List<Partido> partidos = new ArrayList<>();
        partidos.add(partidoExistente);
        when(partidoRepository.findByJornadaIdJornada(5L)).thenReturn(partidos);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> partidoService.agregarPartido(equipoLocal, equipoVisitante, 1, 1, jornadaExistente));
        assertTrue(ex.getMessage().toLowerCase().contains("ya existe"));
        verify(partidoRepository, never()).save(any(Partido.class));
    }

    @Test
    void agregarPartido_equiposInvertidos_debeFallar() {
        when(equipoRepository.existsById(1L)).thenReturn(true);
        when(equipoRepository.existsById(2L)).thenReturn(true);
        when(jornadaRepository.existsById(5L)).thenReturn(true);

        List<Partido> partidos = new ArrayList<>();
        partidos.add(partidoExistente);
        when(partidoRepository.findByJornadaIdJornada(5L)).thenReturn(partidos);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> partidoService.agregarPartido(equipoVisitante, equipoLocal, 1, 1, jornadaExistente));
        assertTrue(ex.getMessage().toLowerCase().contains("ya existe"));
    }

    @Test
    void agregarPartido_golesAmbosEquiposCero_ok() {
        when(equipoRepository.existsById(1L)).thenReturn(true);
        when(equipoRepository.existsById(2L)).thenReturn(true);
        when(jornadaRepository.existsById(5L)).thenReturn(true);
        when(partidoRepository.findByJornadaIdJornada(5L)).thenReturn(new ArrayList<>());

        Partido partidoGuardado = new Partido();
        partidoGuardado.setIdPartido(20L);
        when(partidoRepository.save(any(Partido.class))).thenReturn(partidoGuardado);

        Partido result = partidoService.agregarPartido(equipoLocal, equipoVisitante, 0, 0, jornadaExistente);

        assertNotNull(result);
    }

    // ==================== TESTS PARA modificarResultado ====================

    @Test
    void modificarResultado_ok() {
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(partidoRepository.save(any(Partido.class))).thenAnswer(i -> i.getArgument(0));

        Partido result = partidoService.modificarResultado(10L, 5, 3);

        assertEquals(5, result.getGolesLocal());
        assertEquals(3, result.getGolesVisitante());
        verify(partidoRepository).save(any(Partido.class));
    }

    @Test
    void modificarResultado_idNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> partidoService.modificarResultado(null, 1, 1));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void modificarResultado_partidoNoExiste_debeFallar() {
        when(partidoRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> partidoService.modificarResultado(999L, 1, 1));
        assertTrue(ex.getMessage().toLowerCase().contains("no encontrado"));
    }

    @Test
    void modificarResultado_soloGolesLocal_ok() {
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(partidoRepository.save(any(Partido.class))).thenAnswer(i -> i.getArgument(0));

        Partido result = partidoService.modificarResultado(10L, 4, null);

        assertEquals(4, result.getGolesLocal());
        assertEquals(1, result.getGolesVisitante()); // Sin cambio
    }

    @Test
    void modificarResultado_soloGolesVisitante_ok() {
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(partidoRepository.save(any(Partido.class))).thenAnswer(i -> i.getArgument(0));

        Partido result = partidoService.modificarResultado(10L, null, 4);

        assertEquals(2, result.getGolesLocal()); // Sin cambio
        assertEquals(4, result.getGolesVisitante());
    }

    @Test
    void modificarResultado_golesLocalNegativos_debeFallar() {
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> partidoService.modificarResultado(10L, -2, 1));
        assertTrue(ex.getMessage().toLowerCase().contains("goles") && ex.getMessage().toLowerCase().contains("local"));
    }

    @Test
    void modificarResultado_golesVisitanteNegativos_debeFallar() {
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> partidoService.modificarResultado(10L, 1, -3));
        assertTrue(ex.getMessage().toLowerCase().contains("goles") && ex.getMessage().toLowerCase().contains("visitante"));
    }

    @Test
    void modificarResultado_ambosNull_ok() {
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(partidoRepository.save(any(Partido.class))).thenAnswer(i -> i.getArgument(0));

        Partido result = partidoService.modificarResultado(10L, null, null);

        assertEquals(2, result.getGolesLocal());
        assertEquals(1, result.getGolesVisitante());
    }

    // ==================== TESTS PARA obtenerPartidosPorJornada ====================

    @Test
    void obtenerPartidosPorJornada_ok() {
        when(jornadaRepository.existsById(5L)).thenReturn(true);
        List<Partido> partidos = new ArrayList<>();
        partidos.add(partidoExistente);
        partidos.add(new Partido());
        when(partidoRepository.findByJornadaIdJornada(5L)).thenReturn(partidos);

        List<Partido> result = partidoService.obtenerPartidosPorJornada(5L);

        assertEquals(2, result.size());
    }

    @Test
    void obtenerPartidosPorJornada_idNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> partidoService.obtenerPartidosPorJornada(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void obtenerPartidosPorJornada_jornadaNoExiste_debeFallar() {
        when(jornadaRepository.existsById(888L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> partidoService.obtenerPartidosPorJornada(888L));
        assertTrue(ex.getMessage().toLowerCase().contains("no encontrada"));
    }

    @Test
    void obtenerPartidosPorJornada_vacio_ok() {
        when(jornadaRepository.existsById(5L)).thenReturn(true);
        when(partidoRepository.findByJornadaIdJornada(5L)).thenReturn(new ArrayList<>());

        List<Partido> result = partidoService.obtenerPartidosPorJornada(5L);

        assertEquals(0, result.size());
    }

    // ==================== TESTS PARA verResultadosJornada ====================

    @Test
    void verResultadosJornada_ok() {
        when(jornadaRepository.existsById(5L)).thenReturn(true);
        List<Partido> partidos = new ArrayList<>();
        partidos.add(partidoExistente);
        when(partidoRepository.findByJornadaIdJornada(5L)).thenReturn(partidos);

        List<Partido> result = partidoService.verResultadosJornada(5L);

        assertEquals(1, result.size());
    }

    @Test
    void verResultadosJornada_idNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> partidoService.verResultadosJornada(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void verResultadosJornada_jornadaNoExiste_debeFallar() {
        when(jornadaRepository.existsById(777L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> partidoService.verResultadosJornada(777L));
        assertTrue(ex.getMessage().toLowerCase().contains("no encontrada"));
    }

    // ==================== TESTS PARA obtenerPartido ====================

    @Test
    void obtenerPartido_ok() {
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));

        Partido result = partidoService.obtenerPartido(10L);

        assertNotNull(result);
        assertEquals(10L, result.getIdPartido());
    }

    @Test
    void obtenerPartido_idNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> partidoService.obtenerPartido(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void obtenerPartido_noExiste_debeFallar() {
        when(partidoRepository.findById(555L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> partidoService.obtenerPartido(555L));
        assertTrue(ex.getMessage().toLowerCase().contains("no encontrado"));
    }

    // ==================== TESTS PARA listarTodosLosPartidos ====================

    @Test
    void listarTodosLosPartidos_ok() {
        List<Partido> partidos = new ArrayList<>();
        partidos.add(partidoExistente);
        partidos.add(new Partido());
        partidos.add(new Partido());
        when(partidoRepository.findAll()).thenReturn(partidos);

        List<Partido> result = partidoService.listarTodosLosPartidos();

        assertEquals(3, result.size());
    }

    @Test
    void listarTodosLosPartidos_vacio_ok() {
        when(partidoRepository.findAll()).thenReturn(new ArrayList<>());

        List<Partido> result = partidoService.listarTodosLosPartidos();

        assertEquals(0, result.size());
    }

    // ==================== TESTS PARA obtenerPartidosDeEquipo ====================

    @Test
    void obtenerPartidosDeEquipo_ok() {
        when(equipoRepository.findByNombreEquipo("Real Cume")).thenReturn(equipoLocal);
        List<Partido> partidos = new ArrayList<>();
        partidos.add(partidoExistente);
        when(partidoRepository.findByEquipoLocalNombreEquipoOrEquipoVisitanteNombreEquipo("Real Cume", "Real Cume"))
            .thenReturn(partidos);

        List<Partido> result = partidoService.obtenerPartidosDeEquipo("Real Cume");

        assertEquals(1, result.size());
    }

    @Test
    void obtenerPartidosDeEquipo_idNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> partidoService.obtenerPartidosDeEquipo(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void obtenerPartidosDeEquipo_equipoNoExiste_debeFallar() {
        when(equipoRepository.findByNombreEquipo("NoExiste")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> partidoService.obtenerPartidosDeEquipo("NoExiste"));
        assertTrue(ex.getMessage().toLowerCase().contains("no encontrado"));
    }

    @Test
    void obtenerPartidosDeEquipo_vacio_ok() {
        when(equipoRepository.findByNombreEquipo("Real Cume")).thenReturn(equipoLocal);
        when(partidoRepository.findByEquipoLocalNombreEquipoOrEquipoVisitanteNombreEquipo("Real Cume", "Real Cume"))
            .thenReturn(new ArrayList<>());

        List<Partido> result = partidoService.obtenerPartidosDeEquipo("Real Cume");

        assertEquals(0, result.size());
    }

    // ==================== TESTS PARA eliminarPartido ====================

    @Test
    void eliminarPartido_ok() {
        when(partidoRepository.existsById(10L)).thenReturn(true);

        partidoService.eliminarPartido(10L);

        verify(partidoRepository).deleteById(10L);
    }

    @Test
    void eliminarPartido_idNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> partidoService.eliminarPartido(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void eliminarPartido_partidoNoExiste_debeFallar() {
        when(partidoRepository.existsById(666L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> partidoService.eliminarPartido(666L));
        assertTrue(ex.getMessage().toLowerCase().contains("no encontrado"));
    }
}

