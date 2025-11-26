package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.Equipo;
import com.example.Liga_Del_Cume.data.model.Jugador;
import com.example.Liga_Del_Cume.data.repository.EquipoRepository;
import com.example.Liga_Del_Cume.data.repository.JugadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JugadorServiceTest {

    @Mock
    private JugadorRepository jugadorRepository;

    @Mock
    private EquipoRepository equipoRepository;

    private JugadorService jugadorService;

    private Equipo equipoExistente;
    private Jugador jugadorExistente;

    @BeforeEach
    void setUp() {
        jugadorService = new JugadorService(jugadorRepository, equipoRepository);

        equipoExistente = new Equipo();
        equipoExistente.setIdEquipo(1L);
        equipoExistente.setNombreEquipo("Real Cume");
        equipoExistente.setJugadores(new ArrayList<>());

        jugadorExistente = new Jugador();
        jugadorExistente.setIdJugador(10L);
        jugadorExistente.setNombreJugador("Cristiano Ronaldo");
        jugadorExistente.setEsPortero(false);
        jugadorExistente.setEquipo(equipoExistente);
        jugadorExistente.setPrecioMercado(50000.0f);
    }

    // ==================== TESTS PARA agregarJugador ====================

    @Test
    void agregarJugador_ok() {
        when(equipoRepository.existsById(1L)).thenReturn(true);
        when(jugadorRepository.findByNombreJugador("Lionel Messi")).thenReturn(null);

        Jugador jugadorGuardado = new Jugador();
        jugadorGuardado.setIdJugador(15L);
        jugadorGuardado.setNombreJugador("Lionel Messi");
        when(jugadorRepository.save(any(Jugador.class))).thenReturn(jugadorGuardado);

        Jugador result = jugadorService.agregarJugador("Lionel Messi", false, equipoExistente, 45000.0f);

        assertNotNull(result);
        assertEquals(15L, result.getIdJugador());
        assertEquals("Lionel Messi", result.getNombreJugador());
        verify(jugadorRepository).save(any(Jugador.class));
    }

    @Test
    void agregarJugador_nombreNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.agregarJugador(null, false, equipoExistente, 10000.0f));
        assertTrue(ex.getMessage().toLowerCase().contains("nombre"));
    }

    @Test
    void agregarJugador_nombreVacio_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.agregarJugador("", false, equipoExistente, 10000.0f));
        assertTrue(ex.getMessage().toLowerCase().contains("nombre"));
    }

    @Test
    void agregarJugador_equipoNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.agregarJugador("Jugador Test", false, null, 10000.0f));
        assertTrue(ex.getMessage().toLowerCase().contains("equipo"));
    }

    @Test
    void agregarJugador_equipoNoExiste_debeFallar() {
        when(equipoRepository.existsById(1L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> jugadorService.agregarJugador("Jugador Test", false, equipoExistente, 10000.0f));
        assertTrue(ex.getMessage().toLowerCase().contains("no existe"));
    }

    @Test
    void agregarJugador_equipoConMaximoJugadores_debeFallar() {
        List<Jugador> jugadores = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            jugadores.add(new Jugador());
        }
        equipoExistente.setJugadores(jugadores);

        when(equipoRepository.existsById(1L)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.agregarJugador("Jugador26", false, equipoExistente, 10000.0f));
        assertTrue(ex.getMessage().toLowerCase().contains("máximo") || ex.getMessage().contains("25"));
    }

    @Test
    void agregarJugador_nombreDuplicado_debeFallar() {
        when(equipoRepository.existsById(1L)).thenReturn(true);
        when(jugadorRepository.findByNombreJugador("Duplicado")).thenReturn(jugadorExistente);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.agregarJugador("Duplicado", false, equipoExistente, 10000.0f));
        assertTrue(ex.getMessage().toLowerCase().contains("ya existe"));
    }

    @Test
    void agregarJugador_precioNegativo_debeFallar() {
        when(equipoRepository.existsById(1L)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.agregarJugador("Jugador Test", false, equipoExistente, -5000.0f));
        assertTrue(ex.getMessage().toLowerCase().contains("precio") && ex.getMessage().toLowerCase().contains("negativo"));
    }

    @Test
    void agregarJugador_portero_ok() {
        when(equipoRepository.existsById(1L)).thenReturn(true);
        when(jugadorRepository.findByNombreJugador("Iker Casillas")).thenReturn(null);

        Jugador porteroGuardado = new Jugador();
        porteroGuardado.setIdJugador(20L);
        porteroGuardado.setNombreJugador("Iker Casillas");
        porteroGuardado.setEsPortero(true);
        when(jugadorRepository.save(any(Jugador.class))).thenReturn(porteroGuardado);

        Jugador result = jugadorService.agregarJugador("Iker Casillas", true, equipoExistente, 30000.0f);

        assertNotNull(result);
        assertTrue(result.isEsPortero());
    }

    // ==================== TESTS PARA actualizarJugador ====================

    @Test
    void actualizarJugador_ok() {
        when(jugadorRepository.findById(10L)).thenReturn(Optional.of(jugadorExistente));
        when(jugadorRepository.findByNombreJugador("CR7 Updated")).thenReturn(null);
        when(jugadorRepository.save(any(Jugador.class))).thenAnswer(i -> i.getArgument(0));

        Jugador result = jugadorService.actualizarJugador(10L, "CR7 Updated", 55000.0f, true);

        assertEquals("CR7 Updated", result.getNombreJugador());
        assertEquals(55000.0f, result.getPrecioMercado());
        assertTrue(result.isEsPortero());
        verify(jugadorRepository).save(any(Jugador.class));
    }

    @Test
    void actualizarJugador_idNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.actualizarJugador(null, "Nombre", 10000.0f, false));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void actualizarJugador_jugadorNoExiste_debeFallar() {
        when(jugadorRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> jugadorService.actualizarJugador(999L, "Nombre", 10000.0f, false));
        assertTrue(ex.getMessage().toLowerCase().contains("no encontrado"));
    }

    @Test
    void actualizarJugador_nombreNulo_debeFallar() {
        when(jugadorRepository.findById(10L)).thenReturn(Optional.of(jugadorExistente));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.actualizarJugador(10L, null, 10000.0f, false));
        assertTrue(ex.getMessage().toLowerCase().contains("nombre"));
    }

    @Test
    void actualizarJugador_nombreVacio_debeFallar() {
        when(jugadorRepository.findById(10L)).thenReturn(Optional.of(jugadorExistente));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.actualizarJugador(10L, "", 10000.0f, false));
        assertTrue(ex.getMessage().toLowerCase().contains("nombre"));
    }

    @Test
    void actualizarJugador_nombreDuplicado_debeFallar() {
        Jugador otroJugador = new Jugador();
        otroJugador.setIdJugador(99L);
        otroJugador.setNombreJugador("Otro Jugador");

        when(jugadorRepository.findById(10L)).thenReturn(Optional.of(jugadorExistente));
        when(jugadorRepository.findByNombreJugador("Otro Jugador")).thenReturn(otroJugador);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.actualizarJugador(10L, "Otro Jugador", 10000.0f, false));
        assertTrue(ex.getMessage().toLowerCase().contains("ya existe"));
    }

    @Test
    void actualizarJugador_precioNulo_debeFallar() {
        when(jugadorRepository.findById(10L)).thenReturn(Optional.of(jugadorExistente));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.actualizarJugador(10L, "Nombre Valido", null, false));
        assertTrue(ex.getMessage().toLowerCase().contains("precio"));
    }

    @Test
    void actualizarJugador_precioNegativo_debeFallar() {
        when(jugadorRepository.findById(10L)).thenReturn(Optional.of(jugadorExistente));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.actualizarJugador(10L, "Nombre Valido", -5000.0f, false));
        assertTrue(ex.getMessage().toLowerCase().contains("precio") && ex.getMessage().toLowerCase().contains("negativo"));
    }

    @Test
    void actualizarJugador_esPorteroNulo_debeFallar() {
        when(jugadorRepository.findById(10L)).thenReturn(Optional.of(jugadorExistente));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.actualizarJugador(10L, "Nombre Valido", 10000.0f, null));
        assertTrue(ex.getMessage().toLowerCase().contains("esportero"));
    }

    @Test
    void actualizarJugador_mismoNombre_ok() {
        when(jugadorRepository.findById(10L)).thenReturn(Optional.of(jugadorExistente));
        when(jugadorRepository.findByNombreJugador("Cristiano Ronaldo")).thenReturn(jugadorExistente);
        when(jugadorRepository.save(any(Jugador.class))).thenAnswer(i -> i.getArgument(0));

        Jugador result = jugadorService.actualizarJugador(10L, "Cristiano Ronaldo", 60000.0f, false);

        assertEquals("Cristiano Ronaldo", result.getNombreJugador());
        assertEquals(60000.0f, result.getPrecioMercado());
    }

    // ==================== TESTS PARA eliminarJugador ====================

    @Test
    void eliminarJugador_ok() {
        when(jugadorRepository.existsById(10L)).thenReturn(true);

        jugadorService.eliminarJugador(10L);

        verify(jugadorRepository).deleteById(10L);
    }

    @Test
    void eliminarJugador_idNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.eliminarJugador(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void eliminarJugador_jugadorNoExiste_debeFallar() {
        when(jugadorRepository.existsById(888L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> jugadorService.eliminarJugador(888L));
        assertTrue(ex.getMessage().toLowerCase().contains("no encontrado"));
    }

    // ==================== TESTS PARA listarTodosLosJugadores ====================

    @Test
    void listarTodosLosJugadores_ok() {
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(jugadorExistente);
        jugadores.add(new Jugador());
        when(jugadorRepository.findAll()).thenReturn(jugadores);

        List<Jugador> result = jugadorService.listarTodosLosJugadores();

        assertEquals(2, result.size());
        verify(jugadorRepository).findAll();
    }

    @Test
    void listarTodosLosJugadores_vacio_ok() {
        when(jugadorRepository.findAll()).thenReturn(new ArrayList<>());

        List<Jugador> result = jugadorService.listarTodosLosJugadores();

        assertEquals(0, result.size());
    }

    // ==================== TESTS PARA listarJugadoresPorEquipo ====================

    @Test
    void listarJugadoresPorEquipo_ok() {
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(jugadorExistente);
        when(equipoRepository.findByNombreEquipo("Real Cume")).thenReturn(equipoExistente);
        when(jugadorRepository.findByEquipoNombreEquipo("Real Cume")).thenReturn(jugadores);

        List<Jugador> result = jugadorService.listarJugadoresPorEquipo("Real Cume");

        assertEquals(1, result.size());
    }

    @Test
    void listarJugadoresPorEquipo_nombreNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.listarJugadoresPorEquipo(null));
        assertTrue(ex.getMessage().toLowerCase().contains("nombre"));
    }

    @Test
    void listarJugadoresPorEquipo_nombreVacio_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.listarJugadoresPorEquipo(""));
        assertTrue(ex.getMessage().toLowerCase().contains("nombre"));
    }

    @Test
    void listarJugadoresPorEquipo_equipoNoExiste_debeFallar() {
        when(equipoRepository.findByNombreEquipo("NoExiste")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> jugadorService.listarJugadoresPorEquipo("NoExiste"));
        assertTrue(ex.getMessage().toLowerCase().contains("no existe"));
    }

    @Test
    void listarJugadoresPorEquipo_sinJugadores_debeFallar() {
        when(equipoRepository.findByNombreEquipo("Real Cume")).thenReturn(equipoExistente);
        when(jugadorRepository.findByEquipoNombreEquipo("Real Cume")).thenReturn(new ArrayList<>());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> jugadorService.listarJugadoresPorEquipo("Real Cume"));
        assertTrue(ex.getMessage().toLowerCase().contains("no existen jugadores"));
    }

    // ==================== TESTS PARA listarPorteros ====================

    @Test
    void listarPorteros_ok() {
        List<Jugador> porteros = new ArrayList<>();
        Jugador portero = new Jugador();
        portero.setEsPortero(true);
        porteros.add(portero);
        when(jugadorRepository.findByEsPortero(true)).thenReturn(porteros);

        List<Jugador> result = jugadorService.listarPorteros();

        assertEquals(1, result.size());
        verify(jugadorRepository).findByEsPortero(true);
    }

    @Test
    void listarPorteros_vacio_ok() {
        when(jugadorRepository.findByEsPortero(true)).thenReturn(new ArrayList<>());

        List<Jugador> result = jugadorService.listarPorteros();

        assertEquals(0, result.size());
    }

    // ==================== TESTS PARA listarJugadoresDeCampo ====================

    @Test
    void listarJugadoresDeCampo_ok() {
        List<Jugador> jugadoresCampo = new ArrayList<>();
        jugadoresCampo.add(jugadorExistente);
        when(jugadorRepository.findByEsPortero(false)).thenReturn(jugadoresCampo);

        List<Jugador> result = jugadorService.listarJugadoresDeCampo();

        assertEquals(1, result.size());
        verify(jugadorRepository).findByEsPortero(false);
    }

    // ==================== TESTS PARA buscarPorNombre ====================

    @Test
    void buscarPorNombre_ok() {
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(jugadorExistente);
        when(jugadorRepository.findByNombreJugadorContainingIgnoreCase("Cristiano")).thenReturn(jugadores);

        List<Jugador> result = jugadorService.buscarPorNombre("Cristiano");

        assertEquals(1, result.size());
    }

    @Test
    void buscarPorNombre_nombreNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.buscarPorNombre(null));
        assertTrue(ex.getMessage().toLowerCase().contains("nombre"));
    }

    @Test
    void buscarPorNombre_nombreVacio_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.buscarPorNombre(""));
        assertTrue(ex.getMessage().toLowerCase().contains("nombre"));
    }

    @Test
    void buscarPorNombre_vacio_ok() {
        when(jugadorRepository.findByNombreJugadorContainingIgnoreCase("NoExiste")).thenReturn(new ArrayList<>());

        List<Jugador> result = jugadorService.buscarPorNombre("NoExiste");

        assertEquals(0, result.size());
    }

    // ==================== TESTS PARA buscarPorEquipo ====================

    @Test
    void buscarPorEquipo_ok() {
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(jugadorExistente);
        when(equipoRepository.findByNombreEquipo("Real Cume")).thenReturn(equipoExistente);
        when(jugadorRepository.findByEquipoNombreEquipo("Real Cume")).thenReturn(jugadores);

        List<Jugador> result = jugadorService.buscarPorEquipo("Real Cume");

        assertEquals(1, result.size());
    }

    @Test
    void buscarPorEquipo_nombreNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.buscarPorEquipo(null));
        assertTrue(ex.getMessage().toLowerCase().contains("nombre"));
    }

    @Test
    void buscarPorEquipo_nombreVacio_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.buscarPorEquipo(""));
        assertTrue(ex.getMessage().toLowerCase().contains("nombre"));
    }

    @Test
    void buscarPorEquipo_equipoNoExiste_debeFallar() {
        when(equipoRepository.findByNombreEquipo("NoExiste")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> jugadorService.buscarPorEquipo("NoExiste"));
        assertTrue(ex.getMessage().toLowerCase().contains("no existe"));
    }

    // ==================== TESTS PARA obtenerJugador ====================

    @Test
    void obtenerJugador_ok() {
        when(jugadorRepository.findById(10L)).thenReturn(Optional.of(jugadorExistente));

        Jugador result = jugadorService.obtenerJugador(10L);

        assertNotNull(result);
        assertEquals(10L, result.getIdJugador());
    }

    @Test
    void obtenerJugador_idNulo_debeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> jugadorService.obtenerJugador(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void obtenerJugador_noExiste_debeFallar() {
        when(jugadorRepository.findById(777L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> jugadorService.obtenerJugador(777L));
        assertTrue(ex.getMessage().toLowerCase().contains("no encontrado"));
    }

    // ==================== TESTS PARA buscarJugadoresOrdenadosPorPuntos ====================

    @Test
    void buscarJugadoresOrdenadosPorPuntos_ok() {
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(jugadorExistente);
        when(jugadorRepository.findAllByOrderByEstadisticasPuntosJornadaDesc()).thenReturn(jugadores);

        List<Jugador> result = jugadorService.buscarJugadoresOrdenadosPorPuntos();

        assertEquals(1, result.size());
    }

    // ==================== TESTS PARA buscarPorPrecioMayorAMenor ====================

    @Test
    void buscarPorPrecioMayorAMenor_ok() {
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(jugadorExistente);
        when(jugadorRepository.findByEsPorteroOrderByPrecioMercadoDesc(false)).thenReturn(jugadores);

        List<Jugador> result = jugadorService.buscarPorPrecioMayorAMenor();

        assertEquals(1, result.size());
    }

    // ==================== TESTS PARA buscarJugadoresPorGolesDesc ====================

    @Test
    void buscarJugadoresPorGolesDesc_ok() {
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(jugadorExistente);
        when(jugadorRepository.findJugadoresOrdenadosPorGoles()).thenReturn(jugadores);

        List<Jugador> result = jugadorService.buscarJugadoresPorGolesDesc();

        assertEquals(1, result.size());
    }

    // ==================== TESTS PARA buscarPorterosPorPrecioMercado ====================

    @Test
    void buscarPorterosPorPrecioMercado_ok() {
        List<Jugador> porteros = new ArrayList<>();
        Jugador portero = new Jugador();
        portero.setEsPortero(true);
        porteros.add(portero);
        when(jugadorRepository.findByEsPorteroOrderByPrecioMercadoDesc(true)).thenReturn(porteros);

        List<Jugador> result = jugadorService.buscarPorterosPorPrecioMercado();

        assertEquals(1, result.size());
    }

    // ==================== TESTS PARA buscarPorterosPorPuntosDesc ====================

    @Test
    void buscarPorterosPorPuntosDesc_ok() {
        List<Jugador> porteros = new ArrayList<>();
        Jugador portero = new Jugador();
        portero.setEsPortero(true);
        porteros.add(portero);
        when(jugadorRepository.findByEsPortero(true)).thenReturn(porteros);

        List<Jugador> result = jugadorService.buscarPorterosPorPuntosDesc();

        assertEquals(1, result.size());
    }

    // ==================== TESTS PARA buscarPorteros ====================

    @Test
    void buscarPorteros_ok() {
        List<Jugador> porteros = new ArrayList<>();
        Jugador portero = new Jugador();
        portero.setEsPortero(true);
        porteros.add(portero);
        when(jugadorRepository.findByEsPortero(true)).thenReturn(porteros);

        List<Jugador> result = jugadorService.buscarPorteros();

        assertEquals(1, result.size());
    }
}

