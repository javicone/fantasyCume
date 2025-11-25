package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.*;
import com.example.Liga_Del_Cume.data.repository.AlineacionRepository;
import com.example.Liga_Del_Cume.data.repository.JornadaRepository;
import com.example.Liga_Del_Cume.data.repository.UsuarioRepository;
import com.example.Liga_Del_Cume.data.service.exceptions.AlineacionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests completos para AlineacionService
 * Cubre todos los métodos con casos de éxito y error
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de AlineacionService")
class AlineacionServiceTest {

    @Mock
    private AlineacionRepository alineacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JornadaRepository jornadaRepository;

    @Mock
    private EstadisticaService estadisticaService;

    @InjectMocks
    private AlineacionService alineacionService;

    private Usuario usuario;
    private Jornada jornada;
    private List<Jugador> jugadores;
    private Alineacion alineacion;
    private Jugador jugador1;
    private Jugador jugador2;
    private Jugador jugador3;

    @BeforeEach
    void setUp() {
        // Usuario de prueba
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombreUsuario("TestUser");

        // Jornada de prueba (sin evaluar)
        jornada = new Jornada();
        jornada.setIdJornada(1L);
        jornada.setPartidos(new ArrayList<>());

        // Jugadores de prueba
        jugador1 = new Jugador();
        jugador1.setIdJugador(1L);
        jugador1.setNombreJugador("Jugador 1");

        jugador2 = new Jugador();
        jugador2.setIdJugador(2L);
        jugador2.setNombreJugador("Jugador 2");

        jugador3 = new Jugador();
        jugador3.setIdJugador(3L);
        jugador3.setNombreJugador("Jugador 3");

        jugadores = Arrays.asList(jugador1, jugador2, jugador3);

        // Alineación de prueba
        alineacion = new Alineacion();
        alineacion.setIdAlineacion(1L);
        alineacion.setUsuario(usuario);
        alineacion.setJornada(jornada);
        alineacion.setJugadores(jugadores);
        alineacion.setPuntosTotalesJornada(0);
    }

    // ==================== TESTS DE crearAlineacion ====================

    @Nested
    @DisplayName("Tests de crearAlineacion()")
    class CrearAlineacionTests {

        @Test
        @DisplayName("✅ Debe crear alineación correctamente con datos válidos")
        void testCrearAlineacion_Exito() {
            // Arrange
            when(alineacionRepository.existsByUsuarioIdUsuarioAndJornadaIdJornada(anyLong(), anyLong()))
                    .thenReturn(false);
            when(alineacionRepository.save(any(Alineacion.class))).thenReturn(alineacion);

            // Act
            Alineacion resultado = alineacionService.crearAlineacion(usuario, jornada, jugadores);

            // Assert
            assertNotNull(resultado);
            assertEquals(usuario, resultado.getUsuario());
            assertEquals(jornada, resultado.getJornada());
            assertEquals(3, resultado.getJugadores().size());
            verify(alineacionRepository).save(any(Alineacion.class));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando usuario es nulo")
        void testCrearAlineacion_UsuarioNulo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.crearAlineacion(null, jornada, jugadores);
            });

            assertEquals("El usuario no puede ser nulo", exception.getMessage());
            verify(alineacionRepository, never()).save(any());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando jornada es nula")
        void testCrearAlineacion_JornadaNula() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.crearAlineacion(usuario, null, jugadores);
            });

            assertEquals("La jornada no puede ser nula", exception.getMessage());
            verify(alineacionRepository, never()).save(any());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando lista de jugadores es nula")
        void testCrearAlineacion_JugadoresNulos() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.crearAlineacion(usuario, jornada, null);
            });

            assertEquals("Debe seleccionar al menos un jugador para la alineación", exception.getMessage());
            verify(alineacionRepository, never()).save(any());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando lista de jugadores está vacía")
        void testCrearAlineacion_JugadoresVacios() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.crearAlineacion(usuario, jornada, new ArrayList<>());
            });

            assertEquals("Debe seleccionar al menos un jugador para la alineación", exception.getMessage());
            verify(alineacionRepository, never()).save(any());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando hay un jugador nulo en la lista")
        void testCrearAlineacion_JugadorNuloEnLista() {
            // Arrange
            List<Jugador> jugadoresConNulo = Arrays.asList(jugador1, null, jugador2);

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.crearAlineacion(usuario, jornada, jugadoresConNulo);
            });

            assertEquals("No se pueden incluir jugadores nulos en la alineación", exception.getMessage());
            verify(alineacionRepository, never()).save(any());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando hay jugadores duplicados")
        void testCrearAlineacion_JugadoresDuplicados() {
            // Arrange
            List<Jugador> jugadoresDuplicados = Arrays.asList(jugador1, jugador2, jugador1); // jugador1 duplicado

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.crearAlineacion(usuario, jornada, jugadoresDuplicados);
            });

            assertTrue(exception.getMessage().contains("está duplicado"));
            assertTrue(exception.getMessage().contains("Jugador 1"));
            verify(alineacionRepository, never()).save(any());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando jornada ya está evaluada")
        void testCrearAlineacion_JornadaEvaluada() {
            // Arrange - Jornada con partidos y estadísticas (evaluada)
            Partido partido = new Partido();
            partido.setIdPartido(1L);

            EstadisticaJugadorPartido estadistica = new EstadisticaJugadorPartido();
            estadistica.setPuntosJornada(10); // Puntos > 0 significa evaluada

            partido.setEstadisticas(Arrays.asList(estadistica));
            jornada.setPartidos(Arrays.asList(partido));

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.crearAlineacion(usuario, jornada, jugadores);
            });

            assertTrue(exception.getMessage().contains("ya ha sido evaluada"));
            verify(alineacionRepository, never()).save(any());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando usuario ya tiene alineación en esa jornada")
        void testCrearAlineacion_AlineacionDuplicada() {
            // Arrange
            when(alineacionRepository.existsByUsuarioIdUsuarioAndJornadaIdJornada(1L, 1L))
                    .thenReturn(true);

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.crearAlineacion(usuario, jornada, jugadores);
            });

            assertTrue(exception.getMessage().contains("ya tiene una alineación creada"));
            verify(alineacionRepository, never()).save(any());
        }
    }

    // ==================== TESTS DE consultarAlineacion ====================

    @Nested
    @DisplayName("Tests de consultarAlineacion()")
    class ConsultarAlineacionTests {

        @Test
        @DisplayName("✅ Debe consultar alineación correctamente con IDs válidos")
        void testConsultarAlineacion_Exito() {
            // Arrange
            Partido partido = new Partido();
            jornada.setPartidos(Arrays.asList(partido));

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(jornadaRepository.findById(1L)).thenReturn(Optional.of(jornada));
            when(alineacionRepository.findByUsuarioIdUsuarioAndJornadaIdJornada(1L, 1L))
                    .thenReturn(Optional.of(alineacion));

            // Act
            Alineacion resultado = alineacionService.consultarAlineacion(1L, 1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(alineacion, resultado);
            verify(usuarioRepository).findById(1L);
            verify(jornadaRepository).findById(1L);
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando usuarioId es nulo")
        void testConsultarAlineacion_UsuarioIdNulo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.consultarAlineacion(null, 1L);
            });

            assertEquals("El ID del usuario no puede ser nulo", exception.getMessage());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando jornadaId es nulo")
        void testConsultarAlineacion_JornadaIdNulo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.consultarAlineacion(1L, null);
            });

            assertEquals("El ID de la jornada no puede ser nulo", exception.getMessage());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando usuarioId es negativo")
        void testConsultarAlineacion_UsuarioIdNegativo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.consultarAlineacion(-1L, 1L);
            });

            assertTrue(exception.getMessage().contains("debe ser un valor positivo"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando usuarioId es cero")
        void testConsultarAlineacion_UsuarioIdCero() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.consultarAlineacion(0L, 1L);
            });

            assertTrue(exception.getMessage().contains("debe ser un valor positivo"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando jornadaId es negativo")
        void testConsultarAlineacion_JornadaIdNegativo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.consultarAlineacion(1L, -1L);
            });

            assertTrue(exception.getMessage().contains("debe ser un valor positivo"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando usuario no existe")
        void testConsultarAlineacion_UsuarioNoExiste() {
            // Arrange
            when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.consultarAlineacion(1L, 1L);
            });

            assertTrue(exception.getMessage().contains("No existe ningún usuario con ID"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando jornada no existe")
        void testConsultarAlineacion_JornadaNoExiste() {
            // Arrange
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(jornadaRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.consultarAlineacion(1L, 1L);
            });

            assertTrue(exception.getMessage().contains("No existe ninguna jornada con ID"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando jornada no tiene partidos")
        void testConsultarAlineacion_JornadaSinPartidos() {
            // Arrange
            jornada.setPartidos(new ArrayList<>()); // Sin partidos

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(jornadaRepository.findById(1L)).thenReturn(Optional.of(jornada));

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.consultarAlineacion(1L, 1L);
            });

            assertTrue(exception.getMessage().contains("no tiene partidos asignados"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando alineación no existe")
        void testConsultarAlineacion_AlineacionNoExiste() {
            // Arrange
            Partido partido = new Partido();
            jornada.setPartidos(Arrays.asList(partido));

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(jornadaRepository.findById(1L)).thenReturn(Optional.of(jornada));
            when(alineacionRepository.findByUsuarioIdUsuarioAndJornadaIdJornada(1L, 1L))
                    .thenReturn(Optional.empty());

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.consultarAlineacion(1L, 1L);
            });

            assertTrue(exception.getMessage().contains("no tiene una alineación creada"));
        }
    }

    // ==================== TESTS DE calcularPuntosAlineacion ====================

    @Nested
    @DisplayName("Tests de calcularPuntosAlineacion()")
    class CalcularPuntosAlineacionTests {

        @Test
        @DisplayName("✅ Debe calcular puntos correctamente cuando jornada está evaluada")
        void testCalcularPuntosAlineacion_Exito() {
            // Arrange
            Partido partido = new Partido();
            partido.setJornada(jornada);

            EstadisticaJugadorPartido est1 = new EstadisticaJugadorPartido();
            est1.setPuntosJornada(10);
            est1.setPartido(partido);

            EstadisticaJugadorPartido est2 = new EstadisticaJugadorPartido();
            est2.setPuntosJornada(15);
            est2.setPartido(partido);

            partido.setEstadisticas(Arrays.asList(est1, est2));
            jornada.setPartidos(Arrays.asList(partido));
            alineacion.setJornada(jornada);

            when(alineacionRepository.findById(1L)).thenReturn(Optional.of(alineacion));
            when(estadisticaService.obtenerEstadisticasJugador(1L)).thenReturn(Arrays.asList(est1));
            when(estadisticaService.obtenerEstadisticasJugador(2L)).thenReturn(Arrays.asList(est2));
            when(estadisticaService.obtenerEstadisticasJugador(3L)).thenReturn(new ArrayList<>());
            when(alineacionRepository.save(any(Alineacion.class))).thenReturn(alineacion);

            // Act
            Alineacion resultado = alineacionService.calcularPuntosAlineacion(1L);

            // Assert
            assertNotNull(resultado);
            verify(alineacionRepository).save(any(Alineacion.class));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando ID es nulo")
        void testCalcularPuntosAlineacion_IdNulo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.calcularPuntosAlineacion(null);
            });

            assertEquals("El ID de la alineación no puede ser nulo", exception.getMessage());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando ID es negativo")
        void testCalcularPuntosAlineacion_IdNegativo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.calcularPuntosAlineacion(-1L);
            });

            assertTrue(exception.getMessage().contains("debe ser un valor positivo"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando ID es cero")
        void testCalcularPuntosAlineacion_IdCero() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.calcularPuntosAlineacion(0L);
            });

            assertTrue(exception.getMessage().contains("debe ser un valor positivo"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando alineación no existe")
        void testCalcularPuntosAlineacion_AlineacionNoExiste() {
            // Arrange
            when(alineacionRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.calcularPuntosAlineacion(1L);
            });

            assertTrue(exception.getMessage().contains("No existe ninguna alineación con ID"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando jornada no está evaluada")
        void testCalcularPuntosAlineacion_JornadaNoEvaluada() {
            // Arrange - Jornada sin partidos (no evaluada)
            jornada.setPartidos(new ArrayList<>());
            alineacion.setJornada(jornada);

            when(alineacionRepository.findById(1L)).thenReturn(Optional.of(alineacion));

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.calcularPuntosAlineacion(1L);
            });

            assertTrue(exception.getMessage().contains("aún no ha sido evaluada"));
        }
    }

    // ==================== TESTS DE modificarAlineacion ====================

    @Nested
    @DisplayName("Tests de modificarAlineacion()")
    class ModificarAlineacionTests {

        private List<Jugador> nuevosJugadores;

        @BeforeEach
        void setUp() {
            Jugador j1 = new Jugador();
            j1.setIdJugador(10L);
            j1.setNombreJugador("Nuevo Jugador 1");

            Jugador j2 = new Jugador();
            j2.setIdJugador(11L);
            j2.setNombreJugador("Nuevo Jugador 2");

            nuevosJugadores = Arrays.asList(j1, j2);
        }

        @Test
        @DisplayName("✅ Debe modificar alineación correctamente")
        void testModificarAlineacion_Exito() {
            // Arrange
            when(alineacionRepository.findById(1L)).thenReturn(Optional.of(alineacion));
            when(alineacionRepository.save(any(Alineacion.class))).thenReturn(alineacion);

            // Act
            Alineacion resultado = alineacionService.modificarAlineacion(1L, nuevosJugadores);

            // Assert
            assertNotNull(resultado);
            verify(alineacionRepository).save(any(Alineacion.class));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando ID es nulo")
        void testModificarAlineacion_IdNulo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.modificarAlineacion(null, nuevosJugadores);
            });

            assertEquals("El ID de la alineación no puede ser nulo", exception.getMessage());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando ID es negativo")
        void testModificarAlineacion_IdNegativo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.modificarAlineacion(-1L, nuevosJugadores);
            });

            assertTrue(exception.getMessage().contains("debe ser un valor positivo"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando alineación no existe")
        void testModificarAlineacion_AlineacionNoExiste() {
            // Arrange
            when(alineacionRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.modificarAlineacion(1L, nuevosJugadores);
            });

            assertTrue(exception.getMessage().contains("No existe ninguna alineación con ID"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando nuevos jugadores es nulo")
        void testModificarAlineacion_JugadoresNulos() {
            // Arrange
            when(alineacionRepository.findById(1L)).thenReturn(Optional.of(alineacion));

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.modificarAlineacion(1L, null);
            });

            assertTrue(exception.getMessage().contains("Debe proporcionar al menos un jugador"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando lista de jugadores está vacía")
        void testModificarAlineacion_JugadoresVacios() {
            // Arrange
            when(alineacionRepository.findById(1L)).thenReturn(Optional.of(alineacion));

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.modificarAlineacion(1L, new ArrayList<>());
            });

            assertTrue(exception.getMessage().contains("Debe proporcionar al menos un jugador"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando hay jugadores duplicados")
        void testModificarAlineacion_JugadoresDuplicados() {
            // Arrange
            Jugador j1 = new Jugador();
            j1.setIdJugador(10L);
            j1.setNombreJugador("Jugador Duplicado");

            List<Jugador> duplicados = Arrays.asList(j1, j1);

            when(alineacionRepository.findById(1L)).thenReturn(Optional.of(alineacion));

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.modificarAlineacion(1L, duplicados);
            });

            assertTrue(exception.getMessage().contains("está duplicado"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando jornada está evaluada")
        void testModificarAlineacion_JornadaEvaluada() {
            // Arrange
            Partido partido = new Partido();
            EstadisticaJugadorPartido estadistica = new EstadisticaJugadorPartido();
            estadistica.setPuntosJornada(10);
            partido.setEstadisticas(Arrays.asList(estadistica));
            jornada.setPartidos(Arrays.asList(partido));
            alineacion.setJornada(jornada);

            when(alineacionRepository.findById(1L)).thenReturn(Optional.of(alineacion));

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.modificarAlineacion(1L, nuevosJugadores);
            });

            assertTrue(exception.getMessage().contains("ya ha sido evaluada"));
        }
    }

    // ==================== TESTS DE listarAlineacionesPorUsuario ====================

    @Nested
    @DisplayName("Tests de listarAlineacionesPorUsuario()")
    class ListarAlineacionesPorUsuarioTests {

        @Test
        @DisplayName("✅ Debe listar alineaciones de usuario correctamente")
        void testListarAlineacionesPorUsuario_Exito() {
            // Arrange
            List<Alineacion> alineaciones = Arrays.asList(alineacion);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(alineacionRepository.findByUsuarioIdUsuario(1L)).thenReturn(alineaciones);

            // Act
            List<Alineacion> resultado = alineacionService.listarAlineacionesPorUsuario(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(usuarioRepository).findById(1L);
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando usuarioId es nulo")
        void testListarAlineacionesPorUsuario_IdNulo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.listarAlineacionesPorUsuario(null);
            });

            assertEquals("El ID del usuario no puede ser nulo", exception.getMessage());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando usuarioId es negativo")
        void testListarAlineacionesPorUsuario_IdNegativo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.listarAlineacionesPorUsuario(-1L);
            });

            assertTrue(exception.getMessage().contains("debe ser un valor positivo"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando usuario no existe")
        void testListarAlineacionesPorUsuario_UsuarioNoExiste() {
            // Arrange
            when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.listarAlineacionesPorUsuario(1L);
            });

            assertTrue(exception.getMessage().contains("No existe ningún usuario con ID"));
        }

        @Test
        @DisplayName("✅ Debe retornar lista vacía cuando usuario no tiene alineaciones")
        void testListarAlineacionesPorUsuario_ListaVacia() {
            // Arrange
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(alineacionRepository.findByUsuarioIdUsuario(1L)).thenReturn(new ArrayList<>());

            // Act
            List<Alineacion> resultado = alineacionService.listarAlineacionesPorUsuario(1L);

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    // ==================== TESTS DE listarAlineacionesPorJornada ====================

    @Nested
    @DisplayName("Tests de listarAlineacionesPorJornada()")
    class ListarAlineacionesPorJornadaTests {

        @Test
        @DisplayName("✅ Debe listar alineaciones de jornada correctamente")
        void testListarAlineacionesPorJornada_Exito() {
            // Arrange
            Partido partido = new Partido();
            jornada.setPartidos(Arrays.asList(partido));

            List<Alineacion> alineaciones = Arrays.asList(alineacion);
            when(jornadaRepository.findById(1L)).thenReturn(Optional.of(jornada));
            when(alineacionRepository.findByJornadaIdJornada(1L)).thenReturn(alineaciones);

            // Act
            List<Alineacion> resultado = alineacionService.listarAlineacionesPorJornada(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando jornadaId es nulo")
        void testListarAlineacionesPorJornada_IdNulo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.listarAlineacionesPorJornada(null);
            });

            assertEquals("El ID de la jornada no puede ser nulo", exception.getMessage());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando jornadaId es negativo")
        void testListarAlineacionesPorJornada_IdNegativo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.listarAlineacionesPorJornada(-1L);
            });

            assertTrue(exception.getMessage().contains("debe ser un valor positivo"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando jornada no existe")
        void testListarAlineacionesPorJornada_JornadaNoExiste() {
            // Arrange
            when(jornadaRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.listarAlineacionesPorJornada(1L);
            });

            assertTrue(exception.getMessage().contains("No existe ninguna jornada con ID"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando jornada no tiene partidos")
        void testListarAlineacionesPorJornada_SinPartidos() {
            // Arrange
            jornada.setPartidos(new ArrayList<>());
            when(jornadaRepository.findById(1L)).thenReturn(Optional.of(jornada));

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.listarAlineacionesPorJornada(1L);
            });

            assertTrue(exception.getMessage().contains("no tiene partidos asignados"));
        }
    }

    // ==================== TESTS DE obtenerAlineacion ====================

    @Nested
    @DisplayName("Tests de obtenerAlineacion()")
    class ObtenerAlineacionTests {

        @Test
        @DisplayName("✅ Debe obtener alineación correctamente")
        void testObtenerAlineacion_Exito() {
            // Arrange
            when(alineacionRepository.findById(1L)).thenReturn(Optional.of(alineacion));

            // Act
            Alineacion resultado = alineacionService.obtenerAlineacion(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(alineacion, resultado);
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando ID es nulo")
        void testObtenerAlineacion_IdNulo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.obtenerAlineacion(null);
            });

            assertEquals("El ID de la alineación no puede ser nulo", exception.getMessage());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando ID es negativo")
        void testObtenerAlineacion_IdNegativo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.obtenerAlineacion(-1L);
            });

            assertTrue(exception.getMessage().contains("debe ser un valor positivo"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando ID es cero")
        void testObtenerAlineacion_IdCero() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.obtenerAlineacion(0L);
            });

            assertTrue(exception.getMessage().contains("debe ser un valor positivo"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando alineación no existe")
        void testObtenerAlineacion_NoExiste() {
            // Arrange
            when(alineacionRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.obtenerAlineacion(1L);
            });

            assertTrue(exception.getMessage().contains("No existe ninguna alineación con ID"));
        }
    }

    // ==================== TESTS DE eliminarAlineacion ====================

    @Nested
    @DisplayName("Tests de eliminarAlineacion()")
    class EliminarAlineacionTests {

        @Test
        @DisplayName("✅ Debe eliminar alineación correctamente")
        void testEliminarAlineacion_Exito() {
            // Arrange
            when(alineacionRepository.findById(1L)).thenReturn(Optional.of(alineacion));
            doNothing().when(alineacionRepository).deleteById(1L);

            // Act
            alineacionService.eliminarAlineacion(1L);

            // Assert
            verify(alineacionRepository).deleteById(1L);
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando ID es nulo")
        void testEliminarAlineacion_IdNulo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.eliminarAlineacion(null);
            });

            assertEquals("El ID de la alineación no puede ser nulo", exception.getMessage());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando ID es negativo")
        void testEliminarAlineacion_IdNegativo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.eliminarAlineacion(-1L);
            });

            assertTrue(exception.getMessage().contains("debe ser un valor positivo"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando alineación no existe")
        void testEliminarAlineacion_NoExiste() {
            // Arrange
            when(alineacionRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.eliminarAlineacion(1L);
            });

            assertTrue(exception.getMessage().contains("No existe ninguna alineación con ID"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando jornada está evaluada")
        void testEliminarAlineacion_JornadaEvaluada() {
            // Arrange
            Partido partido = new Partido();
            EstadisticaJugadorPartido estadistica = new EstadisticaJugadorPartido();
            estadistica.setPuntosJornada(10);
            partido.setEstadisticas(Arrays.asList(estadistica));
            jornada.setPartidos(Arrays.asList(partido));
            alineacion.setJornada(jornada);

            when(alineacionRepository.findById(1L)).thenReturn(Optional.of(alineacion));

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.eliminarAlineacion(1L);
            });

            assertTrue(exception.getMessage().contains("ya ha sido evaluada"));
            verify(alineacionRepository, never()).deleteById(any());
        }
    }

    // ==================== TESTS DE eliminarAlineacionesPorUsuario ====================

    @Nested
    @DisplayName("Tests de eliminarAlineacionesPorUsuario()")
    class EliminarAlineacionesPorUsuarioTests {

        @Test
        @DisplayName("✅ Debe eliminar alineaciones no evaluadas correctamente")
        void testEliminarAlineacionesPorUsuario_Exito() {
            // Arrange
            List<Alineacion> alineaciones = Arrays.asList(alineacion);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(alineacionRepository.findByUsuarioIdUsuario(1L)).thenReturn(alineaciones);
            doNothing().when(alineacionRepository).deleteAll(anyList());

            // Act
            alineacionService.eliminarAlineacionesPorUsuario(1L);

            // Assert
            verify(alineacionRepository).deleteAll(anyList());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando usuarioId es nulo")
        void testEliminarAlineacionesPorUsuario_IdNulo() {
            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.eliminarAlineacionesPorUsuario(null);
            });

            assertEquals("El ID del usuario no puede ser nulo", exception.getMessage());
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción cuando usuario no existe")
        void testEliminarAlineacionesPorUsuario_UsuarioNoExiste() {
            // Arrange
            when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.eliminarAlineacionesPorUsuario(1L);
            });

            assertTrue(exception.getMessage().contains("No existe ningún usuario con ID"));
        }

        @Test
        @DisplayName("❌ Debe lanzar excepción informativa cuando hay jornadas evaluadas")
        void testEliminarAlineacionesPorUsuario_ConJornadasEvaluadas() {
            // Arrange
            Partido partido = new Partido();
            EstadisticaJugadorPartido estadistica = new EstadisticaJugadorPartido();
            estadistica.setPuntosJornada(10);
            partido.setEstadisticas(Arrays.asList(estadistica));
            jornada.setPartidos(Arrays.asList(partido));

            Alineacion alineacionEvaluada = new Alineacion();
            alineacionEvaluada.setJornada(jornada);

            List<Alineacion> alineaciones = Arrays.asList(alineacionEvaluada);

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(alineacionRepository.findByUsuarioIdUsuario(1L)).thenReturn(alineaciones);

            // Act & Assert
            AlineacionException exception = assertThrows(AlineacionException.class, () -> {
                alineacionService.eliminarAlineacionesPorUsuario(1L);
            });

            assertTrue(exception.getMessage().contains("Se eliminaron"));
            assertTrue(exception.getMessage().contains("no se pudieron eliminar"));
        }
    }

    // ==================== TESTS DE listarTodasLasAlineaciones ====================

    @Nested
    @DisplayName("Tests de listarTodasLasAlineaciones()")
    class ListarTodasLasAlineacionesTests {

        @Test
        @DisplayName("✅ Debe listar todas las alineaciones correctamente")
        void testListarTodasLasAlineaciones_Exito() {
            // Arrange
            List<Alineacion> alineaciones = Arrays.asList(alineacion);
            when(alineacionRepository.findAll()).thenReturn(alineaciones);

            // Act
            List<Alineacion> resultado = alineacionService.listarTodasLasAlineaciones();

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(alineacionRepository).findAll();
        }

        @Test
        @DisplayName("✅ Debe retornar lista vacía cuando no hay alineaciones")
        void testListarTodasLasAlineaciones_ListaVacia() {
            // Arrange
            when(alineacionRepository.findAll()).thenReturn(new ArrayList<>());

            // Act
            List<Alineacion> resultado = alineacionService.listarTodasLasAlineaciones();

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }
}

