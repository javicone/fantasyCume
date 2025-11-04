package com.example.Liga_Del_Cume;

import com.example.Liga_Del_Cume.data.model.*;
import com.example.Liga_Del_Cume.data.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class DatabaseTest {

    @Autowired
    private LigaCumeRepository ligaRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JornadaRepository jornadaRepository;

    @Autowired
    private PartidoRepository partidoRepository;

    @Autowired
    private EstadisticaJugadorPartidoRepository estadisticaRepository;

    @Autowired
    private AlineacionRepository alineacionRepository;

    @Test
    public void testLigaCreada() {
        System.out.println("\n=== TEST: Verificar que la liga fue creada ===");

        List<LigaCume> ligas = ligaRepository.findAll();
        assertFalse(ligas.isEmpty(), "Debe haber al menos una liga");

        LigaCume liga = ligas.get(0);
        assertNotNull(liga.getIdLigaCume(), "La liga debe tener un ID");
        assertEquals(100000000L, liga.getPresupuestoMaximo(), "El presupuesto debe ser 100000000");

        System.out.println("✓ Liga encontrada con ID: " + liga.getIdLigaCume());
        System.out.println("✓ Presupuesto máximo: " + liga.getPresupuestoMaximo());
    }

    @Test
    public void testEquiposCreados() {
        System.out.println("\n=== TEST: Verificar equipos creados ===");

        List<Equipo> equipos = equipoRepository.findAll();
        assertEquals(2, equipos.size(), "Deben existir 2 equipos");

        Equipo barca = equipoRepository.findByNombreEquipo("FC Barcelona");
        assertNotNull(barca, "El FC Barcelona debe existir");
        assertEquals("FC Barcelona", barca.getNombreEquipo());

        Equipo madrid = equipoRepository.findByNombreEquipo("Real Madrid");
        assertNotNull(madrid, "El Real Madrid debe existir");
        assertEquals("Real Madrid", madrid.getNombreEquipo());

        System.out.println("✓ Equipos encontrados: " + equipos.size());
        System.out.println("  - " + barca.getNombreEquipo());
        System.out.println("  - " + madrid.getNombreEquipo());
    }

    @Test
    public void testJugadoresPorEquipo() {
        System.out.println("\n=== TEST: Verificar jugadores por equipo ===");

        Equipo barca = equipoRepository.findByNombreEquipo("FC Barcelona");
        List<Jugador> jugadoresBarca = jugadorRepository.findByEquipoIdEquipo(barca.getIdEquipo());

        assertEquals(2, jugadoresBarca.size(), "El Barça debe tener 2 jugadores");

        System.out.println("✓ Jugadores del " + barca.getNombreEquipo() + ":");
        for (Jugador j : jugadoresBarca) {
            System.out.println("  - " + j.getNombreJugador() +
                             (j.isEsPortero() ? " (Portero)" : "") +
                             " - Precio: " + j.getPrecioMercado());
        }
    }

    @Test
    public void testJugadoresPorteros() {
        System.out.println("\n=== TEST: Verificar jugadores porteros ===");

        List<Jugador> porteros = jugadorRepository.findByEsPortero(true);
        assertEquals(2, porteros.size(), "Deben existir 2 porteros");

        System.out.println("✓ Porteros encontrados:");
        for (Jugador p : porteros) {
            assertTrue(p.isEsPortero(), "El jugador debe ser portero");
            System.out.println("  - " + p.getNombreJugador() + " (" + p.getEquipo().getNombreEquipo() + ")");
        }
    }

    @Test
    public void testUsuariosCreados() {
        System.out.println("\n=== TEST: Verificar usuarios creados ===");

        List<Usuario> usuarios = usuarioRepository.findAll();
        assertEquals(2, usuarios.size(), "Deben existir 2 usuarios");

        Optional<Usuario> juan = usuarioRepository.findByNombreUsuario("Juan Pérez");
        assertTrue(juan.isPresent(), "El usuario Juan Pérez debe existir");

        System.out.println("✓ Usuarios encontrados:");
        for (Usuario u : usuarios) {
            System.out.println("  - " + u.getNombreUsuario() + " - Puntos: " + u.getPuntosAcumulados());
        }
    }

    @Test
    public void testJornadaYPartidos() {
        System.out.println("\n=== TEST: Verificar jornadas y partidos ===");

        List<Jornada> jornadas = jornadaRepository.findAll();
        assertFalse(jornadas.isEmpty(), "Debe existir al menos una jornada");

        Jornada jornada = jornadas.get(0);
        assertNotNull(jornada.getIdJornada(), "La jornada debe tener un ID");

        List<Partido> partidos = partidoRepository.findByJornadaIdJornada(jornada.getIdJornada());
        assertEquals(1, partidos.size(), "Debe existir 1 partido en la jornada");

        Partido partido = partidos.get(0);
        System.out.println("✓ Jornada con ID " + jornada.getIdJornada() + " encontrada");
        System.out.println("✓ Partido: " + partido.getEquipoLocal().getNombreEquipo() +
                          " " + partido.getGolesLocal() + "-" + partido.getGolesVisitante() +
                          " " + partido.getEquipoVisitante().getNombreEquipo());
    }

    @Test
    public void testEstadisticasJugadores() {
        System.out.println("\n=== TEST: Verificar estadísticas de jugadores ===");

        List<EstadisticaJugadorPartido> estadisticas = estadisticaRepository.findAll();
        assertEquals(3, estadisticas.size(), "Deben existir 3 estadísticas");

        System.out.println("✓ Estadísticas encontradas:");
        for (EstadisticaJugadorPartido stat : estadisticas) {
            System.out.println("  - " + stat.getJugador().getNombreJugador() +
                             " | Goles: " + stat.getGolesAnotados() +
                             " | Asistencias: " + stat.getAsistencias() +
                             " | Puntos: " + stat.getPuntosJornada());
        }
    }

    @Test
    public void testEstadisticasPorJugador() {
        System.out.println("\n=== TEST: Verificar estadísticas por jugador específico ===");

        Jugador pedri = jugadorRepository.findAll().stream()
            .filter(j -> j.getNombreJugador().contains("Pedri"))
            .findFirst()
            .orElse(null);

        assertNotNull(pedri, "Pedri debe existir");

        List<EstadisticaJugadorPartido> statsPedri =
            estadisticaRepository.findByJugadorIdJugador(pedri.getIdJugador());

        assertEquals(1, statsPedri.size(), "Pedri debe tener 1 estadística");
        EstadisticaJugadorPartido stat = statsPedri.get(0);

        assertEquals(1, stat.getGolesAnotados(), "Pedri debe tener 1 gol");
        assertEquals(1, stat.getAsistencias(), "Pedri debe tener 1 asistencia");

        System.out.println("✓ Estadísticas de " + pedri.getNombreJugador() + ":");
        System.out.println("  - Goles: " + stat.getGolesAnotados());
        System.out.println("  - Asistencias: " + stat.getAsistencias());
        System.out.println("  - Puntos: " + stat.getPuntosJornada());
    }

    @Test
    public void testAlineaciones() {
        System.out.println("\n=== TEST: Verificar alineaciones ===");

        List<Alineacion> alineaciones = alineacionRepository.findAll();
        assertFalse(alineaciones.isEmpty(), "Debe existir al menos una alineación");

        Alineacion alineacion = alineaciones.get(0);
        assertNotNull(alineacion.getUsuario(), "La alineación debe tener un usuario");
        assertNotNull(alineacion.getJornada(), "La alineación debe tener una jornada");
        assertEquals(2, alineacion.getJugadores().size(), "La alineación debe tener 2 jugadores");

        System.out.println("✓ Alineación del usuario: " + alineacion.getUsuario().getNombreUsuario());
        System.out.println("  - Jornada ID: " + alineacion.getJornada().getIdJornada());
        System.out.println("  - Puntos totales: " + alineacion.getPuntosTotalesJornada());
        System.out.println("  - Jugadores:");
        for (Jugador j : alineacion.getJugadores()) {
            System.out.println("    * " + j.getNombreJugador());
        }
    }

    @Test
    public void testRelacionesEquipoLiga() {
        System.out.println("\n=== TEST: Verificar relaciones Equipo-Liga ===");

        LigaCume liga = ligaRepository.findAll().get(0);
        List<Equipo> equipos = equipoRepository.findByLigaIdLigaCume(liga.getIdLigaCume());

        assertEquals(2, equipos.size(), "La liga debe tener 2 equipos");

        for (Equipo equipo : equipos) {
            assertEquals(liga.getIdLigaCume(), equipo.getLiga().getIdLigaCume(),
                        "El equipo debe pertenecer a la liga correcta");
        }

        System.out.println("✓ Relación Liga-Equipos verificada correctamente");
    }

    @Test
    public void testRankingUsuarios() {
        System.out.println("\n=== TEST: Verificar ranking de usuarios ===");

        LigaCume liga = ligaRepository.findAll().get(0);
        List<Usuario> ranking = usuarioRepository.findByLigaIdLigaCumeOrderByPuntosAcumuladosDesc(liga.getIdLigaCume());

        assertEquals(2, ranking.size(), "Debe haber 2 usuarios en el ranking");

        // Verificar que están ordenados
        for (int i = 0; i < ranking.size() - 1; i++) {
            assertTrue(ranking.get(i).getPuntosAcumulados() >= ranking.get(i + 1).getPuntosAcumulados(),
                      "Los usuarios deben estar ordenados por puntos descendente");
        }

        System.out.println("✓ Ranking de usuarios:");
        int posicion = 1;
        for (Usuario u : ranking) {
            System.out.println("  " + posicion + ". " + u.getNombreUsuario() +
                             " - " + u.getPuntosAcumulados() + " puntos");
            posicion++;
        }
    }

    @Test
    public void testResumenCompleto() {
        System.out.println("\n========================================");
        System.out.println("RESUMEN COMPLETO DE LA BASE DE DATOS");
        System.out.println("========================================");

        System.out.println("\n📊 ESTADÍSTICAS GENERALES:");
        System.out.println("  - Ligas: " + ligaRepository.count());
        System.out.println("  - Equipos: " + equipoRepository.count());
        System.out.println("  - Jugadores: " + jugadorRepository.count());
        System.out.println("  - Usuarios: " + usuarioRepository.count());
        System.out.println("  - Jornadas: " + jornadaRepository.count());
        System.out.println("  - Partidos: " + partidoRepository.count());
        System.out.println("  - Estadísticas: " + estadisticaRepository.count());
        System.out.println("  - Alineaciones: " + alineacionRepository.count());

        System.out.println("\n========================================\n");
    }
}

