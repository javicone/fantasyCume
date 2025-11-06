package com.example.Liga_Del_Cume;

import com.example.Liga_Del_Cume.data.model.*;
import com.example.Liga_Del_Cume.data.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class GenericUseCaseTest {

    @Autowired
    private LigaCumeRepository ligaCumeRepository;

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
    private AlineacionRepository alineacionRepository;

    @Autowired
    private EstadisticaJugadorPartidoRepository estadisticaRepository;

    @Test
    public void testGenericFantasyLeagueFlow() {
        // =================================================================================================
        // PASO 1: CONFIGURACIÓN INICIAL (LIGA, USUARIOS, EQUIPOS, JUGADORES)
        // =================================================================================================
        System.out.println("\n╔═════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║             🏆 INICIANDO SIMULACIÓN DE LA LIGA DEL CUME 🏆                  ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════════════╝");

        // --- Crear la Liga ---
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("La Liga del Cume");
        liga.setPresupuestoMaximo(200000000L);
        liga = ligaCumeRepository.save(liga);
        System.out.println("\n-> Liga '" + liga.getNombreLiga() + "' creada con un presupuesto de " + liga.getPresupuestoMaximo() + "€.");

        // --- Crear Usuarios ---
        Usuario usuario1 = crearUsuario("Ibai Llanos", "pass1", liga);
        Usuario usuario2 = crearUsuario("Gerard Piqué", "pass2", liga);
        Usuario usuario3 = crearUsuario("DJMaRiiO", "pass3", liga);
        System.out.println("-> 3 usuarios se han unido a la liga: " + usuario1.getNombreUsuario() + ", " + usuario2.getNombreUsuario() + ", " + usuario3.getNombreUsuario() + ".");

        // --- Crear Equipos y Jugadores ---
        Equipo equipoA = crearEquipo("Real Madrid", "escudo_rm.png", liga);
        Jugador porteroA = crearJugador("Thibaut Courtois", true, 50000000, equipoA);
        Jugador jugadorA1 = crearJugador("Vinícius Júnior", false, 80000000, equipoA);
        Jugador jugadorA2 = crearJugador("Jude Bellingham", false, 90000000, equipoA);

        Equipo equipoB = crearEquipo("FC Barcelona", "escudo_fcb.png", liga);
        Jugador porteroB = crearJugador("Marc-André ter Stegen", true, 45000000, equipoB);
        Jugador jugadorB1 = crearJugador("Lamine Yamal", false, 75000000, equipoB);
        Jugador jugadorB2 = crearJugador("Robert Lewandowski", false, 60000000, equipoB);
        System.out.println("-> 2 equipos y 6 jugadores estrella han sido creados.\n");

        // =================================================================================================
        // PASO 2: SIMULACIÓN DE LA JORNADA 1
        // =================================================================================================
        System.out.println("╔═════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                           ⚽ JORNADA 1: El Clásico ⚽                       ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════════════╝");

        // --- Crear Jornada y Partido ---
        Jornada jornada1 = new Jornada();
        jornada1.setLiga(liga);
        jornada1 = jornadaRepository.save(jornada1);

        Partido partido1 = new Partido();
        partido1.setEquipoLocal(equipoA);
        partido1.setEquipoVisitante(equipoB);
        partido1.setGolesLocal(3);
        partido1.setGolesVisitante(2);
        partido1.setJornada(jornada1);
        partido1 = partidoRepository.save(partido1);
        System.out.println("\nResultado del partido: " + equipoA.getNombreEquipo() + " " + partido1.getGolesLocal() + " - " + partido1.getGolesVisitante() + " " + equipoB.getNombreEquipo() + "\n");

        // --- Registrar Estadísticas de Jugadores ---
        System.out.println("📊 Registrando estadísticas de jugadores para la Jornada 1:");
        crearEstadistica(jugadorA1, partido1, 1, 0, 1, 0, false, true, 11); // Gol + Asistencia
        crearEstadistica(jugadorA2, partido1, 2, 0, 0, 0, false, true, 16);  // Doblete
        crearEstadistica(porteroA, partido1, 0, 2, 0, 0, false, true, 2);   // 2 goles recibidos
        crearEstadistica(jugadorB1, partido1, 1, 0, 0, 1, false, true, 6);  // Gol + Amarilla
        crearEstadistica(jugadorB2, partido1, 1, 0, 1, 0, false, true, 11); // Gol + Asistencia
        crearEstadistica(porteroB, partido1, 0, 3, 0, 0, false, true, 1);   // 3 Goles recibidos

        // --- Crear Alineaciones y Calcular Puntos ---
        System.out.println("\n📋 Creando alineaciones y calculando puntos para la Jornada 1:");
        Alineacion alin1_1 = crearAlineacion(usuario1, jornada1, List.of(porteroA, jugadorA1, jugadorB1), 19); // 2 + 11 + 6
        Alineacion alin1_2 = crearAlineacion(usuario2, jornada1, List.of(porteroB, jugadorA2, jugadorB2), 28); // 1 + 16 + 11
        Alineacion alin1_3 = crearAlineacion(usuario3, jornada1, List.of(porteroA, jugadorA2, jugadorB1), 24); // 2 + 16 + 6

        // --- Actualizar Puntos Acumulados de Usuarios ---
        actualizarPuntosUsuario(usuario1, alin1_1.getPuntosTotalesJornada());
        actualizarPuntosUsuario(usuario2, alin1_2.getPuntosTotalesJornada());
        actualizarPuntosUsuario(usuario3, alin1_3.getPuntosTotalesJornada());

        // --- Asserts para Jornada 1 ---
        assertEquals(19, alin1_1.getPuntosTotalesJornada());
        assertEquals(28, usuario2.getPuntosAcumulados());
        assertEquals(24, usuario3.getPuntosAcumulados());
        System.out.println("\n-> Asserts de Jornada 1 superados ✓");

        // --- Mostrar Rankings ---
        mostrarRanking(liga, jornada1);

        // =================================================================================================
        // PASO 3: SIMULACIÓN DE LA JORNADA 2
        // =================================================================================================
        System.out.println("\n╔═════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                       ⚽ JORNADA 2: La Revancha ⚽                        ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════════════╝");

        // --- Crear Jornada y Partido ---
        Jornada jornada2 = new Jornada();
        jornada2.setLiga(liga);
        jornada2 = jornadaRepository.save(jornada2);

        Partido partido2 = new Partido();
        partido2.setEquipoLocal(equipoB);
        partido2.setEquipoVisitante(equipoA);
        partido2.setGolesLocal(1);
        partido2.setGolesVisitante(0);
        partido2.setJornada(jornada2);
        partido2 = partidoRepository.save(partido2);
        System.out.println("\nResultado del partido: " + equipoB.getNombreEquipo() + " " + partido2.getGolesLocal() + " - " + partido2.getGolesVisitante() + " " + equipoA.getNombreEquipo() + "\n");

        // --- Registrar Estadísticas ---
        System.out.println("📊 Registrando estadísticas de jugadores para la Jornada 2:");
        crearEstadistica(porteroA, partido2, 0, 1, 0, 0, false, true, 4);  // 1 gol recibido
        crearEstadistica(porteroB, partido2, 0, 0, 0, 0, false, true, 10); // Portería a cero
        crearEstadistica(jugadorA1, partido2, 0, 0, 0, 0, false, true, 3);  // Partido sin eventos
        crearEstadistica(jugadorB2, partido2, 1, 0, 0, 0, false, true, 8);  // Gol de la victoria

        // --- Crear Alineaciones y Actualizar Puntos ---
        System.out.println("\n📋 Creando alineaciones y calculando puntos para la Jornada 2:");
        Alineacion alin2_1 = crearAlineacion(usuario1, jornada2, List.of(porteroA, jugadorA1, jugadorB2), 15); // 4 + 3 + 8
        Alineacion alin2_2 = crearAlineacion(usuario2, jornada2, List.of(porteroB, jugadorA1, jugadorB2), 21); // 10 + 3 + 8
        Alineacion alin2_3 = crearAlineacion(usuario3, jornada2, List.of(porteroA, jugadorB2), 12);             // 4 + 8

        actualizarPuntosUsuario(usuario1, alin2_1.getPuntosTotalesJornada());
        actualizarPuntosUsuario(usuario2, alin2_2.getPuntosTotalesJornada());
        actualizarPuntosUsuario(usuario3, alin2_3.getPuntosTotalesJornada());

        // --- Asserts para Puntos Acumulados ---
        assertEquals(19 + 15, usuario1.getPuntosAcumulados());
        assertEquals(28 + 21, usuario2.getPuntosAcumulados());
        assertEquals(24 + 12, usuario3.getPuntosAcumulados());
        System.out.println("\n-> Asserts de Jornada 2 superados ✓");

        // --- Mostrar Rankings ---
        mostrarRanking(liga, jornada2);

        // =================================================================================================
        // PASO 4: ELIMINAR UN USUARIO Y VERIFICAR ESTADO
        // =================================================================================================
        System.out.println("\n╔═════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║              🗑️  ELIMINANDO USUARIO Y VERIFICANDO CONSISTENCIA 🗑️            ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════════════╝");
        Long idUsuarioEliminado = usuario3.getIdUsuario();
        System.out.println("\n-> Eliminando a " + usuario3.getNombreUsuario() + " (ID: " + idUsuarioEliminado + ") de la liga.");

        // --- Borrado en cascada (si está configurado) o manual ---
        // Para este test, borramos explícitamente las alineaciones para asegurar la limpieza.
        alineacionRepository.deleteAll(alineacionRepository.findByUsuarioIdUsuario(idUsuarioEliminado));
        usuarioRepository.deleteById(idUsuarioEliminado);

        // --- Asserts de Eliminación ---
        assertFalse(usuarioRepository.findById(idUsuarioEliminado).isPresent(), "DJMaRiiO no debería existir.");
        assertTrue(alineacionRepository.findByUsuarioIdUsuario(idUsuarioEliminado).isEmpty(), "Las alineaciones de DJMaRiiO no deberían existir.");
        assertEquals(2, usuarioRepository.count(), "Deberían quedar 2 usuarios en la liga.");
        assertEquals(4, alineacionRepository.count(), "Deberían quedar 4 alineaciones en total.");
        System.out.println("\n-> Asserts de eliminación superados ✓. El usuario ha sido purgado correctamente.");

        // =================================================================================================
        // PASO 5: SIMULACIÓN DE LA JORNADA 3 (CON USUARIOS RESTANTES)
        // =================================================================================================
        System.out.println("\n╔═════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                     ⚽ JORNADA 3: La Gran Final ⚽                        ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════════════╝");

        // --- Crear Jornada y Partido ---
        Jornada jornada3 = new Jornada();
        jornada3.setLiga(liga);
        jornada3 = jornadaRepository.save(jornada3);

        Partido partido3 = new Partido();
        partido3.setEquipoLocal(equipoA);
        partido3.setEquipoVisitante(equipoB);
        partido3.setGolesLocal(1);
        partido3.setGolesVisitante(1);
        partido3.setJornada(jornada3);
        partido3 = partidoRepository.save(partido3);
        System.out.println("\nResultado del partido: " + equipoA.getNombreEquipo() + " " + partido3.getGolesLocal() + " - " + partido3.getGolesVisitante() + " " + equipoB.getNombreEquipo() + "\n");

        // --- Registrar Estadísticas ---
        System.out.println("📊 Registrando estadísticas de jugadores para la Jornada 3:");
        crearEstadistica(jugadorA2, partido3, 1, 0, 0, 0, false, true, 8);  // Gol de Bellingham
        crearEstadistica(jugadorB1, partido3, 1, 0, 0, 0, false, true, 8);  // Gol de Lamine Yamal
        crearEstadistica(porteroA, partido3, 0, 1, 0, 0, false, true, 4);   // 1 gol recibido
        crearEstadistica(porteroB, partido3, 0, 1, 0, 0, false, true, 4);   // 1 gol recibido

        // --- Crear Alineaciones y Actualizar Puntos (solo usuarios restantes) ---
        System.out.println("\n📋 Creando alineaciones y calculando puntos para la Jornada 3:");
        Alineacion alin3_1 = crearAlineacion(usuario1, jornada3, List.of(porteroA, jugadorA2), 12); // 4 + 8
        Alineacion alin3_2 = crearAlineacion(usuario2, jornada3, List.of(porteroB, jugadorB1), 12); // 4 + 8

        actualizarPuntosUsuario(usuario1, alin3_1.getPuntosTotalesJornada());
        actualizarPuntosUsuario(usuario2, alin3_2.getPuntosTotalesJornada());

        // --- Asserts para Jornada 3 ---
        assertEquals(12, alin3_1.getPuntosTotalesJornada());
        assertEquals(12, alin3_2.getPuntosTotalesJornada());
        System.out.println("\n-> Asserts de Jornada 3 superados ✓");

        // --- Mostrar Rankings ---
        mostrarRanking(liga, jornada3);


        // =================================================================================================
        // PASO 6: MOSTRAR RANKING FINAL
        // =================================================================================================
        System.out.println("\n╔═════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                       🏆 RANKING FINAL DE LA LIGA 🏆                        ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════════════╝");
        List<Usuario> rankingFinal = usuarioRepository.findAll();
        rankingFinal.sort(Comparator.comparing(Usuario::getPuntosAcumulados).reversed());

        // --- Asserts del Ranking Final ---
        assertEquals(usuario2.getIdUsuario(), rankingFinal.get(0).getIdUsuario());
        assertEquals(49 + 12, rankingFinal.get(0).getPuntosAcumulados());
        assertEquals(usuario1.getIdUsuario(), rankingFinal.get(1).getIdUsuario());
        assertEquals(34 + 12, rankingFinal.get(1).getPuntosAcumulados());
        System.out.println("\n-> Asserts de ranking final superados ✓");

        // --- Imprimir Ranking Final ---
        imprimirRankingGeneral(usuarioRepository.findAll());

        System.out.println("\n\n╔═════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                  ✅ SIMULACIÓN COMPLETADA CON ÉXITO ✅                      ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════════════╝\n");
    }

    // =================================================================================================
    // MÉTODOS AUXILIARES PARA CREACIÓN DE ENTIDADES
    // =================================================================================================

    private Usuario crearUsuario(String nombre, String password, LigaCume liga) {
        Usuario u = new Usuario();
        u.setNombreUsuario(nombre);
        u.setPassword(password);
        u.setLiga(liga);
        u.setPuntosAcumulados(0);
        return usuarioRepository.save(u);
    }

    private Equipo crearEquipo(String nombre, String escudoUrl, LigaCume liga) {
        Equipo e = new Equipo();
        e.setNombreEquipo(nombre);
        e.setEscudoURL(escudoUrl);
        e.setLiga(liga);
        return equipoRepository.save(e);
    }

    private Jugador crearJugador(String nombre, boolean esPortero, float precio, Equipo equipo) {
        Jugador j = new Jugador();
        j.setNombreJugador(nombre);
        j.setEsPortero(esPortero);
        j.setPrecioMercado(precio);
        j.setEquipo(equipo);
        return jugadorRepository.save(j);
    }

    private void crearEstadistica(Jugador jugador, Partido partido, int goles, int golesRecibidos, int asistencias, int amarillas, boolean roja, boolean minJugados, int puntos) {
        EstadisticaJugadorPartido est = new EstadisticaJugadorPartido();
        est.setJugador(jugador);
        est.setPartido(partido);
        est.setGolesAnotados(goles);
        est.setGolesRecibidos(golesRecibidos);
        est.setAsistencias(asistencias);
        est.setTarjetaAmarillas(amarillas);
        est.setTarjetaRojas(roja);
        est.setMinMinutosJugados(minJugados);
        est.setPuntosJornada(puntos);
        estadisticaRepository.save(est);
        System.out.println("  - " + String.format("%-20s", jugador.getNombreJugador()) + " -> " + puntos + " puntos.");
    }

    private Alineacion crearAlineacion(Usuario usuario, Jornada jornada, List<Jugador> jugadores, int puntos) {
        Alineacion a = new Alineacion();
        a.setUsuario(usuario);
        a.setJornada(jornada);
        a.setJugadores(new ArrayList<>(jugadores));
        a.setPuntosTotalesJornada(puntos);
        System.out.println("  - Alineación de " + String.format("%-15s", usuario.getNombreUsuario()) + " -> " + puntos + " puntos.");
        return alineacionRepository.save(a);
    }

    private void actualizarPuntosUsuario(Usuario usuario, int puntosJornada) {
        usuario.setPuntosAcumulados(usuario.getPuntosAcumulados() + puntosJornada);
        usuarioRepository.save(usuario);
    }

    // =================================================================================================
    // MÉTODOS AUXILIARES PARA MOSTRAR INFORMACIÓN
    // =================================================================================================

    private void mostrarRanking(LigaCume liga, Jornada jornada) {
        System.out.println("\n--- 🏆 Clasificación Jornada " + jornada.getIdJornada() + " 🏆 ---");
        List<Alineacion> alineaciones = alineacionRepository.findByJornadaIdJornadaOrderByPuntosTotalesJornadaDesc(jornada.getIdJornada());
        int pos = 1;
        for (Alineacion a : alineaciones) {
            String medalla = pos == 1 ? "🥇" : pos == 2 ? "🥈" : "🥉";
            System.out.printf("  %s %d. %-20s %d pts\n", medalla, pos++, a.getUsuario().getNombreUsuario(), a.getPuntosTotalesJornada());
        }

        System.out.println("\n\n--- 📋 Detalles de Alineaciones Jornada " + jornada.getIdJornada() + " 📋 ---");
        for (Alineacion a : alineaciones) {
            System.out.println("\n┌──────────────────────────────────────────┐");
            System.out.printf("│ 👤 %-35s │\n", a.getUsuario().getNombreUsuario());
            System.out.printf("│ Puntos: %-30d │\n", a.getPuntosTotalesJornada());
            System.out.println("├──────────────────────────────────────────┤");
            for (Jugador j : a.getJugadores()) {
                EstadisticaJugadorPartido stats = estadisticaRepository.findByJugadorIdJugadorAndPartidoJornadaIdJornada(j.getIdJugador(), jornada.getIdJornada());
                int puntosJugador = (stats != null) ? stats.getPuntosJornada() : 0;
                System.out.printf("│   %-25s -> %2d pts │\n", j.getNombreJugador(), puntosJugador);
            }
            System.out.println("└──────────────────────────────────────────┘");
        }


        System.out.println("\n--- 📈 Clasificación General 📈 ---");
        imprimirRankingGeneral(usuarioRepository.findAll());
    }

    private void imprimirRankingGeneral(List<Usuario> usuarios) {
        usuarios.sort(Comparator.comparing(Usuario::getPuntosAcumulados).reversed());
        int pos = 1;
        for (Usuario u : usuarios) {
            String medalla = pos == 1 ? "🥇" : pos == 2 ? "🥈" : "🥉";
            System.out.printf("  %s %d. %-20s %d pts (total)\n", medalla, pos++, u.getNombreUsuario(), u.getPuntosAcumulados());
        }
    }
}
