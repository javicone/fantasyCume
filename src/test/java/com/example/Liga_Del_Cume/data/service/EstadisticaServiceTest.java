package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.EstadisticaJugadorPartido;
import com.example.Liga_Del_Cume.data.model.Jornada;
import com.example.Liga_Del_Cume.data.model.Jugador;
import com.example.Liga_Del_Cume.data.model.Partido;
import com.example.Liga_Del_Cume.data.repository.EstadisticaJugadorPartidoRepository;
import com.example.Liga_Del_Cume.data.repository.JornadaRepository;
import com.example.Liga_Del_Cume.data.repository.JugadorRepository;
import com.example.Liga_Del_Cume.data.repository.PartidoRepository;
import com.example.Liga_Del_Cume.data.service.exceptions.EstadisticaException;
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
public class EstadisticaServiceTest {

    @Mock
    private EstadisticaJugadorPartidoRepository estadisticaRepository;

    @Mock
    private JugadorRepository jugadorRepository;

    @Mock
    private PartidoRepository partidoRepository;

    @Mock
    private JornadaRepository jornadaRepository;

    @InjectMocks
    private EstadisticaService estadisticaService;

    private Jugador jugadorExistente;
    private Partido partidoExistente;
    private Jornada jornadaExistente;

    @BeforeEach
    void setUp() {
        jugadorExistente = new Jugador();
        jugadorExistente.setIdJugador(1L);
        jugadorExistente.setNombreJugador("Cristiano Ronaldo");

        partidoExistente = new Partido();
        partidoExistente.setIdPartido(10L);

        jornadaExistente = new Jornada();
        jornadaExistente.setIdJornada(5L);
    }

    // ==================== TESTS PARA añadirEstadistica ====================

    @Test
    void añadirEstadistica_ok() {
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(1L, 10L)).thenReturn(null);

        EstadisticaJugadorPartido estadisticaGuardada = new EstadisticaJugadorPartido();
        estadisticaGuardada.setJugador(jugadorExistente);
        estadisticaGuardada.setPartido(partidoExistente);
        estadisticaGuardada.setGolesAnotados(2);
        when(estadisticaRepository.save(any(EstadisticaJugadorPartido.class))).thenReturn(estadisticaGuardada);

        EstadisticaJugadorPartido result = estadisticaService.añadirEstadistica(
            jugadorExistente, partidoExistente, 2, 1, 0, false, true, 0, 10
        );

        assertNotNull(result);
        assertEquals(2, result.getGolesAnotados());
        verify(estadisticaRepository).save(any(EstadisticaJugadorPartido.class));
    }

    @Test
    void añadirEstadistica_jugadorNulo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.añadirEstadistica(null, partidoExistente, 0, 0, 0, false, true, 0, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("jugador") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void añadirEstadistica_partidoNulo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.añadirEstadistica(jugadorExistente, null, 0, 0, 0, false, true, 0, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("partido") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void añadirEstadistica_jugadorNoExiste_debeFallar() {
        Jugador jugadorInexistente = new Jugador();
        jugadorInexistente.setIdJugador(999L);
        when(jugadorRepository.findById(999L)).thenReturn(Optional.empty());

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.añadirEstadistica(jugadorInexistente, partidoExistente, 0, 0, 0, false, true, 0, 0));
        assertTrue(ex.getMessage().contains("No existe ningún jugador"));
    }

    @Test
    void añadirEstadistica_partidoNoExiste_debeFallar() {
        Partido partidoInexistente = new Partido();
        partidoInexistente.setIdPartido(888L);
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(888L)).thenReturn(Optional.empty());

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.añadirEstadistica(jugadorExistente, partidoInexistente, 0, 0, 0, false, true, 0, 0));
        assertTrue(ex.getMessage().contains("No existe ningún partido"));
    }

    @Test
    void añadirEstadistica_golesNegativos_debeFallar() {
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.añadirEstadistica(jugadorExistente, partidoExistente, -1, 0, 0, false, true, 0, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("goles") && ex.getMessage().toLowerCase().contains("negativo"));
    }

    @Test
    void añadirEstadistica_asistenciasNegativas_debeFallar() {
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.añadirEstadistica(jugadorExistente, partidoExistente, 0, -2, 0, false, true, 0, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("asistencias") && ex.getMessage().toLowerCase().contains("negativo"));
    }

    @Test
    void añadirEstadistica_tarjetasAmarillasNegativas_debeFallar() {
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.añadirEstadistica(jugadorExistente, partidoExistente, 0, 0, -1, false, true, 0, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("tarjetas amarillas") && ex.getMessage().toLowerCase().contains("negativo"));
    }

    @Test
    void añadirEstadistica_golesRecibidosNegativos_debeFallar() {
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.añadirEstadistica(jugadorExistente, partidoExistente, 0, 0, 0, false, true, -3, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("goles recibidos") && ex.getMessage().toLowerCase().contains("negativo"));
    }

    @Test
    void añadirEstadistica_tarjetasAmarillasExcesivas_debeFallar() {
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.añadirEstadistica(jugadorExistente, partidoExistente, 0, 0, 3, false, true, 0, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("más de 2 tarjetas amarillas"));
    }

    @Test
    void añadirEstadistica_estadisticaDuplicada_debeFallar() {
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));

        EstadisticaJugadorPartido existente = new EstadisticaJugadorPartido();
        existente.setJugador(jugadorExistente);
        existente.setPartido(partidoExistente);
        when(estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(1L, 10L)).thenReturn(existente);

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.añadirEstadistica(jugadorExistente, partidoExistente, 0, 0, 0, false, true, 0, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("ya existe"));
        verify(estadisticaRepository, never()).save(any(EstadisticaJugadorPartido.class));
    }

    @Test
    void añadirEstadistica_jugadorSinId_ok() {
        Jugador jugadorSinId = new Jugador();
        jugadorSinId.setIdJugador(null);
        jugadorSinId.setNombreJugador("Nuevo");

        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(null, 10L)).thenReturn(null);

        EstadisticaJugadorPartido guardada = new EstadisticaJugadorPartido();
        when(estadisticaRepository.save(any(EstadisticaJugadorPartido.class))).thenReturn(guardada);

        EstadisticaJugadorPartido result = estadisticaService.añadirEstadistica(
            jugadorSinId, partidoExistente, 0, 0, 0, false, true, 0, 0
        );

        assertNotNull(result);
        verify(estadisticaRepository).save(any(EstadisticaJugadorPartido.class));
    }

    @Test
    void añadirEstadistica_partidoSinId_ok() {
        Partido partidoSinId = new Partido();
        partidoSinId.setIdPartido(null);

        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(1L, null)).thenReturn(null);

        EstadisticaJugadorPartido guardada = new EstadisticaJugadorPartido();
        when(estadisticaRepository.save(any(EstadisticaJugadorPartido.class))).thenReturn(guardada);

        EstadisticaJugadorPartido result = estadisticaService.añadirEstadistica(
            jugadorExistente, partidoSinId, 0, 0, 0, false, true, 0, 0
        );

        assertNotNull(result);
        verify(estadisticaRepository).save(any(EstadisticaJugadorPartido.class));
    }

    // ==================== TESTS PARA modificarEstadistica ====================

    @Test
    void modificarEstadistica_okCambiarGoles() {
        EstadisticaJugadorPartido existente = new EstadisticaJugadorPartido();
        existente.setJugador(jugadorExistente);
        existente.setPartido(partidoExistente);
        existente.setGolesAnotados(0);

        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(1L, 10L)).thenReturn(existente);
        when(estadisticaRepository.save(any(EstadisticaJugadorPartido.class))).thenAnswer(i -> i.getArgument(0));

        EstadisticaJugadorPartido result = estadisticaService.modificarEstadistica(
            1L, 10L, 3, null, null, null, null, null, null
        );

        assertEquals(3, result.getGolesAnotados());
        verify(estadisticaRepository).save(any(EstadisticaJugadorPartido.class));
    }

    @Test
    void modificarEstadistica_jugadorIdNulo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.modificarEstadistica(null, 10L, 1, null, null, null, null, null, null));
        assertTrue(ex.getMessage().toLowerCase().contains("id del jugador") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void modificarEstadistica_partidoIdNulo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.modificarEstadistica(1L, null, 1, null, null, null, null, null, null));
        assertTrue(ex.getMessage().toLowerCase().contains("id del partido") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void modificarEstadistica_jugadorIdNegativo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.modificarEstadistica(-5L, 10L, 1, null, null, null, null, null, null));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void modificarEstadistica_jugadorIdCero_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.modificarEstadistica(0L, 10L, 1, null, null, null, null, null, null));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void modificarEstadistica_partidoIdNegativo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.modificarEstadistica(1L, -3L, 1, null, null, null, null, null, null));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void modificarEstadistica_partidoIdCero_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.modificarEstadistica(1L, 0L, 1, null, null, null, null, null, null));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void modificarEstadistica_jugadorNoExiste_debeFallar() {
        when(jugadorRepository.findById(999L)).thenReturn(Optional.empty());

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.modificarEstadistica(999L, 10L, 1, null, null, null, null, null, null));
        assertTrue(ex.getMessage().contains("No existe ningún jugador"));
    }

    @Test
    void modificarEstadistica_partidoNoExiste_debeFallar() {
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(777L)).thenReturn(Optional.empty());

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.modificarEstadistica(1L, 777L, 1, null, null, null, null, null, null));
        assertTrue(ex.getMessage().contains("No existe ningún partido"));
    }

    @Test
    void modificarEstadistica_estadisticaNoExiste_debeFallar() {
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(1L, 10L)).thenReturn(null);

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.modificarEstadistica(1L, 10L, 1, null, null, null, null, null, null));
        assertTrue(ex.getMessage().toLowerCase().contains("no existe una estadística"));
        verify(estadisticaRepository, never()).save(any(EstadisticaJugadorPartido.class));
    }

    @Test
    void modificarEstadistica_golesNegativos_debeFallar() {
        EstadisticaJugadorPartido existente = new EstadisticaJugadorPartido();
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(1L, 10L)).thenReturn(existente);

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.modificarEstadistica(1L, 10L, -2, null, null, null, null, null, null));
        assertTrue(ex.getMessage().toLowerCase().contains("goles") && ex.getMessage().toLowerCase().contains("negativo"));
    }

    @Test
    void modificarEstadistica_asistenciasNegativas_debeFallar() {
        EstadisticaJugadorPartido existente = new EstadisticaJugadorPartido();
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(1L, 10L)).thenReturn(existente);

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.modificarEstadistica(1L, 10L, null, -1, null, null, null, null, null));
        assertTrue(ex.getMessage().toLowerCase().contains("asistencias") && ex.getMessage().toLowerCase().contains("negativo"));
    }

    @Test
    void modificarEstadistica_tarjetasAmarillasNegativas_debeFallar() {
        EstadisticaJugadorPartido existente = new EstadisticaJugadorPartido();
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(1L, 10L)).thenReturn(existente);

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.modificarEstadistica(1L, 10L, null, null, -1, null, null, null, null));
        assertTrue(ex.getMessage().toLowerCase().contains("tarjetas amarillas") && ex.getMessage().toLowerCase().contains("negativo"));
    }

    @Test
    void modificarEstadistica_tarjetasAmarillasExcesivas_debeFallar() {
        EstadisticaJugadorPartido existente = new EstadisticaJugadorPartido();
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(1L, 10L)).thenReturn(existente);

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.modificarEstadistica(1L, 10L, null, null, 4, null, null, null, null));
        assertTrue(ex.getMessage().toLowerCase().contains("más de 2 tarjetas amarillas"));
    }

    @Test
    void modificarEstadistica_golesRecibidosNegativos_debeFallar() {
        EstadisticaJugadorPartido existente = new EstadisticaJugadorPartido();
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(1L, 10L)).thenReturn(existente);

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.modificarEstadistica(1L, 10L, null, null, null, null, null, -5, null));
        assertTrue(ex.getMessage().toLowerCase().contains("goles recibidos") && ex.getMessage().toLowerCase().contains("negativo"));
    }

    @Test
    void modificarEstadistica_sinCambios_debeFallar() {
        EstadisticaJugadorPartido existente = new EstadisticaJugadorPartido();
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(1L, 10L)).thenReturn(existente);

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.modificarEstadistica(1L, 10L, null, null, null, null, null, null, null));
        assertTrue(ex.getMessage().toLowerCase().contains("debe proporcionar"));
    }

    @Test
    void modificarEstadistica_cambiarTodosLosCampos_ok() {
        EstadisticaJugadorPartido existente = new EstadisticaJugadorPartido();
        existente.setJugador(jugadorExistente);
        existente.setPartido(partidoExistente);

        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(1L, 10L)).thenReturn(existente);
        when(estadisticaRepository.save(any(EstadisticaJugadorPartido.class))).thenAnswer(i -> i.getArgument(0));

        EstadisticaJugadorPartido result = estadisticaService.modificarEstadistica(
            1L, 10L, 2, 3, 1, true, false, 2, 15
        );

        assertEquals(2, result.getGolesAnotados());
        assertEquals(3, result.getAsistencias());
        assertEquals(1, result.getTarjetaAmarillas());
        assertTrue(result.isTarjetaRojas());
        assertFalse(result.isMinMinutosJugados());
        assertEquals(2, result.getGolesRecibidos());
        assertEquals(15, result.getPuntosJornada());
        verify(estadisticaRepository).save(any(EstadisticaJugadorPartido.class));
    }

    // ==================== TESTS PARA obtenerEstadisticasJugador ====================

    @Test
    void obtenerEstadisticasJugador_ok() {
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        List<EstadisticaJugadorPartido> estadisticas = new ArrayList<>();
        estadisticas.add(new EstadisticaJugadorPartido());
        estadisticas.add(new EstadisticaJugadorPartido());
        when(estadisticaRepository.findByJugadorIdJugador(1L)).thenReturn(estadisticas);

        List<EstadisticaJugadorPartido> result = estadisticaService.obtenerEstadisticasJugador(1L);

        assertEquals(2, result.size());
        verify(estadisticaRepository).findByJugadorIdJugador(1L);
    }

    @Test
    void obtenerEstadisticasJugador_idNulo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.obtenerEstadisticasJugador(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id del jugador") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void obtenerEstadisticasJugador_idNegativo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.obtenerEstadisticasJugador(-7L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void obtenerEstadisticasJugador_idCero_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.obtenerEstadisticasJugador(0L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void obtenerEstadisticasJugador_jugadorNoExiste_debeFallar() {
        when(jugadorRepository.findById(555L)).thenReturn(Optional.empty());

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.obtenerEstadisticasJugador(555L));
        assertTrue(ex.getMessage().contains("No existe ningún jugador"));
    }

    @Test
    void obtenerEstadisticasJugador_vacio_ok() {
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(estadisticaRepository.findByJugadorIdJugador(1L)).thenReturn(new ArrayList<>());

        List<EstadisticaJugadorPartido> result = estadisticaService.obtenerEstadisticasJugador(1L);

        assertEquals(0, result.size());
    }

    // ==================== TESTS PARA obtenerEstadisticasPartido ====================

    @Test
    void obtenerEstadisticasPartido_ok() {
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        List<EstadisticaJugadorPartido> estadisticas = new ArrayList<>();
        estadisticas.add(new EstadisticaJugadorPartido());
        when(estadisticaRepository.findByPartidoIdPartido(10L)).thenReturn(estadisticas);

        List<EstadisticaJugadorPartido> result = estadisticaService.obtenerEstadisticasPartido(10L);

        assertEquals(1, result.size());
        verify(estadisticaRepository).findByPartidoIdPartido(10L);
    }

    @Test
    void obtenerEstadisticasPartido_idNulo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.obtenerEstadisticasPartido(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id del partido") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void obtenerEstadisticasPartido_idNegativo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.obtenerEstadisticasPartido(-2L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void obtenerEstadisticasPartido_idCero_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.obtenerEstadisticasPartido(0L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void obtenerEstadisticasPartido_partidoNoExiste_debeFallar() {
        when(partidoRepository.findById(666L)).thenReturn(Optional.empty());

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.obtenerEstadisticasPartido(666L));
        assertTrue(ex.getMessage().contains("No existe ningún partido"));
    }

    @Test
    void obtenerEstadisticasPartido_vacio_ok() {
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(estadisticaRepository.findByPartidoIdPartido(10L)).thenReturn(new ArrayList<>());

        List<EstadisticaJugadorPartido> result = estadisticaService.obtenerEstadisticasPartido(10L);

        assertEquals(0, result.size());
    }

    // ==================== TESTS PARA obtenerEstadistica ====================

    @Test
    void obtenerEstadistica_ok() {
        EstadisticaJugadorPartido estadistica = new EstadisticaJugadorPartido();
        estadistica.setJugador(jugadorExistente);
        estadistica.setPartido(partidoExistente);

        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(1L, 10L)).thenReturn(estadistica);

        EstadisticaJugadorPartido result = estadisticaService.obtenerEstadistica(1L, 10L);

        assertNotNull(result);
        assertEquals(jugadorExistente, result.getJugador());
        assertEquals(partidoExistente, result.getPartido());
    }

    @Test
    void obtenerEstadistica_jugadorIdNulo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.obtenerEstadistica(null, 10L));
        assertTrue(ex.getMessage().toLowerCase().contains("id del jugador") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void obtenerEstadistica_partidoIdNulo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.obtenerEstadistica(1L, null));
        assertTrue(ex.getMessage().toLowerCase().contains("id del partido") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void obtenerEstadistica_jugadorIdNegativo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.obtenerEstadistica(-1L, 10L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void obtenerEstadistica_partidoIdNegativo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.obtenerEstadistica(1L, -10L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void obtenerEstadistica_noExiste_debeFallar() {
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(1L, 10L)).thenReturn(null);

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.obtenerEstadistica(1L, 10L));
        assertTrue(ex.getMessage().toLowerCase().contains("no existe una estadística"));
    }

    // ==================== TESTS PARA obtenerEstadisticasJornada ====================

    @Test
    void obtenerEstadisticasJornada_ok() {
        when(jornadaRepository.findById(5L)).thenReturn(Optional.of(jornadaExistente));
        List<EstadisticaJugadorPartido> estadisticas = new ArrayList<>();
        estadisticas.add(new EstadisticaJugadorPartido());
        when(estadisticaRepository.findByPartidoJornadaIdJornada(5L)).thenReturn(estadisticas);

        List<EstadisticaJugadorPartido> result = estadisticaService.obtenerEstadisticasJornada(5L);

        assertEquals(1, result.size());
        verify(estadisticaRepository).findByPartidoJornadaIdJornada(5L);
    }

    @Test
    void obtenerEstadisticasJornada_idNulo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.obtenerEstadisticasJornada(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id de la jornada") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void obtenerEstadisticasJornada_idNegativo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.obtenerEstadisticasJornada(-8L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void obtenerEstadisticasJornada_idCero_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.obtenerEstadisticasJornada(0L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void obtenerEstadisticasJornada_jornadaNoExiste_debeFallar() {
        when(jornadaRepository.findById(333L)).thenReturn(Optional.empty());

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.obtenerEstadisticasJornada(333L));
        assertTrue(ex.getMessage().contains("No existe ninguna jornada"));
    }

    @Test
    void obtenerEstadisticasJornada_vacio_ok() {
        when(jornadaRepository.findById(5L)).thenReturn(Optional.of(jornadaExistente));
        when(estadisticaRepository.findByPartidoJornadaIdJornada(5L)).thenReturn(new ArrayList<>());

        List<EstadisticaJugadorPartido> result = estadisticaService.obtenerEstadisticasJornada(5L);

        assertEquals(0, result.size());
    }

    // ==================== TESTS PARA eliminarEstadistica ====================

    @Test
    void eliminarEstadistica_ok() {
        EstadisticaJugadorPartido estadistica = new EstadisticaJugadorPartido();
        estadistica.setJugador(jugadorExistente);
        estadistica.setPartido(partidoExistente);

        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(1L, 10L)).thenReturn(estadistica);

        estadisticaService.eliminarEstadistica(1L, 10L);

        verify(estadisticaRepository).delete(estadistica);
    }

    @Test
    void eliminarEstadistica_jugadorIdNulo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.eliminarEstadistica(null, 10L));
        assertTrue(ex.getMessage().toLowerCase().contains("id del jugador") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void eliminarEstadistica_partidoIdNulo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.eliminarEstadistica(1L, null));
        assertTrue(ex.getMessage().toLowerCase().contains("id del partido") && ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void eliminarEstadistica_jugadorIdNegativo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.eliminarEstadistica(-3L, 10L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void eliminarEstadistica_partidoIdNegativo_debeFallar() {
        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.eliminarEstadistica(1L, -7L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void eliminarEstadistica_noExiste_debeFallar() {
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partidoExistente));
        when(estadisticaRepository.findByJugadorIdJugadorAndPartidoIdPartido(1L, 10L)).thenReturn(null);

        EstadisticaException ex = assertThrows(EstadisticaException.class,
            () -> estadisticaService.eliminarEstadistica(1L, 10L));
        assertTrue(ex.getMessage().toLowerCase().contains("no existe una estadística"));
        verify(estadisticaRepository, never()).delete(any(EstadisticaJugadorPartido.class));
    }

    // ==================== TESTS PARA listarTodasLasEstadisticas ====================

    @Test
    void listarTodasLasEstadisticas_ok() {
        List<EstadisticaJugadorPartido> estadisticas = new ArrayList<>();
        estadisticas.add(new EstadisticaJugadorPartido());
        estadisticas.add(new EstadisticaJugadorPartido());
        estadisticas.add(new EstadisticaJugadorPartido());
        when(estadisticaRepository.findAll()).thenReturn(estadisticas);

        List<EstadisticaJugadorPartido> result = estadisticaService.listarTodasLasEstadisticas();

        assertEquals(3, result.size());
        verify(estadisticaRepository).findAll();
    }

    @Test
    void listarTodasLasEstadisticas_vacio_ok() {
        when(estadisticaRepository.findAll()).thenReturn(new ArrayList<>());

        List<EstadisticaJugadorPartido> result = estadisticaService.listarTodasLasEstadisticas();

        assertEquals(0, result.size());
    }
}

