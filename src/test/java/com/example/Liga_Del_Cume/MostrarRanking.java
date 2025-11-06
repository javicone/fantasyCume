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
public class MostrarRanking {

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
    public void testRankingCompletoDosJornadas() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║         SIMULACIÓN COMPLETA DE LIGA - 2 JORNADAS              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        // ==================== CONFIGURACIÓN INICIAL ====================
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  PASO 1: Creación de la Liga y Usuarios");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        // Crear Liga
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("Liga Fantasy Euskadi 2024-2025");
        liga.setPresupuestoMaximo(100000000L);
        liga = ligaCumeRepository.save(liga);
        System.out.println("✓ Liga creada: " + liga.getNombreLiga());
        System.out.println("  Presupuesto máximo: " + liga.getPresupuestoMaximo() + "€");

        // Crear 3 Usuarios
        Usuario usuario1 = new Usuario();
        usuario1.setNombreUsuario("Carlos Martínez");
        usuario1.setPassword("pass123");
        usuario1.setPuntosAcumulados(0);
        usuario1.setLiga(liga);
        usuario1 = usuarioRepository.save(usuario1);

        Usuario usuario2 = new Usuario();
        usuario2.setNombreUsuario("Ana García");
        usuario2.setPassword("pass456");
        usuario2.setPuntosAcumulados(0);
        usuario2.setLiga(liga);
        usuario2 = usuarioRepository.save(usuario2);

        Usuario usuario3 = new Usuario();
        usuario3.setNombreUsuario("Pedro López");
        usuario3.setPassword("pass789");
        usuario3.setPuntosAcumulados(0);
        usuario3.setLiga(liga);
        usuario3 = usuarioRepository.save(usuario3);

        System.out.println("\n✓ Usuarios registrados:");
        System.out.println("  1. " + usuario1.getNombreUsuario());
        System.out.println("  2. " + usuario2.getNombreUsuario());
        System.out.println("  3. " + usuario3.getNombreUsuario());

        // ==================== CREAR EQUIPOS Y JUGADORES ====================
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  PASO 2: Creación de Equipos y Jugadores");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        // Equipo 1: Athletic Club
        Equipo athletic = new Equipo();
        athletic.setNombreEquipo("Athletic Club");
        athletic.setEscudoURL("http://escudos.com/athletic.png");
        athletic.setLiga(liga);
        athletic = equipoRepository.save(athletic);
        System.out.println("✓ Equipo creado: " + athletic.getNombreEquipo());

        // Jugadores Athletic Club
        Jugador athleticPortero = crearJugador("Unai Simón", true, 15000000, athletic);
        Jugador athleticJug1 = crearJugador("Iñaki Williams", false, 25000000, athletic);
        Jugador athleticJug2 = crearJugador("Nico Williams", false, 30000000, athletic);
        Jugador athleticJug3 = crearJugador("Oihan Sancet", false, 20000000, athletic);
        Jugador athleticJug4 = crearJugador("Álex Berenguer", false, 18000000, athletic);

        System.out.println("  Jugadores Athletic:");
        System.out.println("    - " + athleticPortero.getNombreJugador() + " (Portero) - " + athleticPortero.getPrecioMercado() + "€");
        System.out.println("    - " + athleticJug1.getNombreJugador() + " - " + athleticJug1.getPrecioMercado() + "€");
        System.out.println("    - " + athleticJug2.getNombreJugador() + " - " + athleticJug2.getPrecioMercado() + "€");
        System.out.println("    - " + athleticJug3.getNombreJugador() + " - " + athleticJug3.getPrecioMercado() + "€");
        System.out.println("    - " + athleticJug4.getNombreJugador() + " - " + athleticJug4.getPrecioMercado() + "€");

        // Equipo 2: Real Sociedad
        Equipo realSociedad = new Equipo();
        realSociedad.setNombreEquipo("Real Sociedad");
        realSociedad.setEscudoURL("http://escudos.com/realsociedad.png");
        realSociedad.setLiga(liga);
        realSociedad = equipoRepository.save(realSociedad);
        System.out.println("\n✓ Equipo creado: " + realSociedad.getNombreEquipo());

        // Jugadores Real Sociedad
        Jugador realPortero = crearJugador("Álex Remiro", true, 12000000, realSociedad);
        Jugador realJug1 = crearJugador("Mikel Oyarzabal", false, 28000000, realSociedad);
        Jugador realJug2 = crearJugador("Takefusa Kubo", false, 35000000, realSociedad);
        Jugador realJug3 = crearJugador("Mikel Merino", false, 22000000, realSociedad);
        Jugador realJug4 = crearJugador("Brais Méndez", false, 19000000, realSociedad);

        System.out.println("  Jugadores Real Sociedad:");
        System.out.println("    - " + realPortero.getNombreJugador() + " (Portero) - " + realPortero.getPrecioMercado() + "€");
        System.out.println("    - " + realJug1.getNombreJugador() + " - " + realJug1.getPrecioMercado() + "€");
        System.out.println("    - " + realJug2.getNombreJugador() + " - " + realJug2.getPrecioMercado() + "€");
        System.out.println("    - " + realJug3.getNombreJugador() + " - " + realJug3.getPrecioMercado() + "€");
        System.out.println("    - " + realJug4.getNombreJugador() + " - " + realJug4.getPrecioMercado() + "€");

        // ==================== JORNADA 1 ====================
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                         JORNADA 1                              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        Jornada jornada1 = new Jornada();
        jornada1.setLiga(liga);
        jornada1 = jornadaRepository.save(jornada1);
        System.out.println("✓ Jornada 1 creada (ID: " + jornada1.getIdJornada() + ")");

        // Crear partido Jornada 1
        Partido partido1 = new Partido();
        partido1.setEquipoLocal(athletic);
        partido1.setEquipoVisitante(realSociedad);
        partido1.setGolesLocal(3);
        partido1.setGolesVisitante(2);
        partido1.setJornada(jornada1);
        partido1 = partidoRepository.save(partido1);
        System.out.println("\n📊 Partido Jornada 1:");
        System.out.println("   " + athletic.getNombreEquipo() + " " + partido1.getGolesLocal() +
                         " - " + partido1.getGolesVisitante() + " " + realSociedad.getNombreEquipo());

        // Crear estadísticas Jornada 1
        System.out.println("\n📈 Estadísticas de jugadores:");

        EstadisticaJugadorPartido est1_1 = crearEstadistica(athleticPortero, partido1, 0, 2, 0, 0, false, true, 4);
        EstadisticaJugadorPartido est1_2 = crearEstadistica(athleticJug1, partido1, 2, 0, 1, 0, false, true, 16);
        EstadisticaJugadorPartido est1_3 = crearEstadistica(athleticJug2, partido1, 1, 0, 1, 1, false, true, 9);
        EstadisticaJugadorPartido est1_4 = crearEstadistica(athleticJug3, partido1, 0, 0, 1, 0, false, true, 6);

        EstadisticaJugadorPartido est1_5 = crearEstadistica(realPortero, partido1, 0, 3, 0, 0, false, true, 1);
        EstadisticaJugadorPartido est1_6 = crearEstadistica(realJug1, partido1, 1, 0, 1, 0, false, true, 11);
        EstadisticaJugadorPartido est1_7 = crearEstadistica(realJug2, partido1, 1, 0, 0, 0, false, true, 8);
        EstadisticaJugadorPartido est1_8 = crearEstadistica(realJug3, partido1, 0, 0, 0, 1, false, true, 2);

        // Crear alineaciones Jornada 1
        System.out.println("\n👥 Alineaciones de usuarios:");

        Alineacion alin1_1 = crearAlineacion(usuario1, jornada1,
                List.of(athleticPortero, athleticJug1, athleticJug2, realJug1, realJug2), 48);

        Alineacion alin1_2 = crearAlineacion(usuario2, jornada1,
                List.of(realPortero, athleticJug1, realJug2, realJug3, athleticJug3), 42);

        Alineacion alin1_3 = crearAlineacion(usuario3, jornada1,
                List.of(athleticPortero, realJug1, athleticJug2, realJug2, athleticJug3), 48);

        // Actualizar puntos acumulados de usuarios
        usuario1.setPuntosAcumulados(usuario1.getPuntosAcumulados() + alin1_1.getPuntosTotalesJornada());
        usuarioRepository.save(usuario1);
        usuario2.setPuntosAcumulados(usuario2.getPuntosAcumulados() + alin1_2.getPuntosTotalesJornada());
        usuarioRepository.save(usuario2);
        usuario3.setPuntosAcumulados(usuario3.getPuntosAcumulados() + alin1_3.getPuntosTotalesJornada());
        usuarioRepository.save(usuario3);

        // ASSERTS JORNADA 1: Verificar puntos de alineaciones
        assertEquals(48, alin1_1.getPuntosTotalesJornada(), "Usuario 1 debe tener 48 puntos en jornada 1");
        assertEquals(42, alin1_2.getPuntosTotalesJornada(), "Usuario 2 debe tener 42 puntos en jornada 1");
        assertEquals(48, alin1_3.getPuntosTotalesJornada(), "Usuario 3 debe tener 48 puntos en jornada 1");

        // ASSERTS JORNADA 1: Verificar puntos acumulados
        assertEquals(48, usuario1.getPuntosAcumulados(), "Usuario 1 debe tener 48 puntos acumulados");
        assertEquals(42, usuario2.getPuntosAcumulados(), "Usuario 2 debe tener 42 puntos acumulados");
        assertEquals(48, usuario3.getPuntosAcumulados(), "Usuario 3 debe tener 48 puntos acumulados");

        // ASSERTS: Verificar estadísticas individuales de jugadores
        assertEquals(4, est1_1.getPuntosJornada(), "Unai Simón debe tener 4 puntos");
        assertEquals(16, est1_2.getPuntosJornada(), "Iñaki Williams debe tener 16 puntos");
        assertEquals(9, est1_3.getPuntosJornada(), "Nico Williams debe tener 9 puntos");
        assertEquals(11, est1_6.getPuntosJornada(), "Mikel Oyarzabal debe tener 11 puntos");
        assertEquals(8, est1_7.getPuntosJornada(), "Takefusa Kubo debe tener 8 puntos");

        // ASSERTS: Verificar ranking de jornada 1
        List<Alineacion> rankingJ1 = alineacionRepository.findByJornadaIdJornada(jornada1.getIdJornada());
        rankingJ1.sort(Comparator.comparing(Alineacion::getPuntosTotalesJornada).reversed());

        // Usuario 1 y Usuario 3 empatan en primer lugar con 48 puntos, Usuario 2 en tercer lugar con 42
        assertTrue(rankingJ1.get(0).getPuntosTotalesJornada() == 48, "El primer lugar debe tener 48 puntos");
        assertTrue(rankingJ1.get(1).getPuntosTotalesJornada() == 48, "El segundo lugar debe tener 48 puntos");
        assertEquals(42, rankingJ1.get(2).getPuntosTotalesJornada(), "El tercer lugar debe tener 42 puntos");
        assertEquals(usuario2.getIdUsuario(), rankingJ1.get(2).getUsuario().getIdUsuario(),
                    "Usuario 2 debe estar en tercera posición");

        mostrarRanking(liga, jornada1);

        // ==================== JORNADA 2 ====================
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                         JORNADA 2                              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        Jornada jornada2 = new Jornada();
        jornada2.setLiga(liga);
        jornada2 = jornadaRepository.save(jornada2);
        System.out.println("✓ Jornada 2 creada (ID: " + jornada2.getIdJornada() + ")");

        // Crear partido Jornada 2
        Partido partido2 = new Partido();
        partido2.setEquipoLocal(realSociedad);
        partido2.setEquipoVisitante(athletic);
        partido2.setGolesLocal(1);
        partido2.setGolesVisitante(1);
        partido2.setJornada(jornada2);
        partido2 = partidoRepository.save(partido2);
        System.out.println("\n📊 Partido Jornada 2:");
        System.out.println("   " + realSociedad.getNombreEquipo() + " " + partido2.getGolesLocal() +
                         " - " + partido2.getGolesVisitante() + " " + athletic.getNombreEquipo());

        // Crear estadísticas Jornada 2
        System.out.println("\n📈 Estadísticas de jugadores:");

        EstadisticaJugadorPartido est2_1 = crearEstadistica(realPortero, partido2, 0, 1, 0, 0, false, true, 4);
        EstadisticaJugadorPartido est2_2 = crearEstadistica(realJug1, partido2, 1, 0, 0, 0, false, true, 8);
        EstadisticaJugadorPartido est2_3 = crearEstadistica(realJug2, partido2, 0, 0, 1, 0, false, true, 6);
        EstadisticaJugadorPartido est2_4 = crearEstadistica(realJug3, partido2, 0, 0, 0, 0, false, true, 3);

        EstadisticaJugadorPartido est2_5 = crearEstadistica(athleticPortero, partido2, 0, 1, 0, 0, false, true, 4);
        EstadisticaJugadorPartido est2_6 = crearEstadistica(athleticJug1, partido2, 1, 0, 0, 1, false, true, 6);
        EstadisticaJugadorPartido est2_7 = crearEstadistica(athleticJug2, partido2, 0, 0, 1, 0, false, true, 6);
        EstadisticaJugadorPartido est2_8 = crearEstadistica(athleticJug4, partido2, 0, 0, 0, 0, false, true, 3);


        // Crear alineaciones Jornada 2
        System.out.println("\n👥 Alineaciones de usuarios:");

        Alineacion alin2_1 = crearAlineacion(usuario1, jornada2,
                List.of(realPortero, realJug1, realJug2, athleticJug1, athleticJug2), 28);
        usuario1.setPuntosAcumulados(usuario1.getPuntosAcumulados() + 28);
        usuarioRepository.save(usuario1);

        Alineacion alin2_2 = crearAlineacion(usuario2, jornada2,
                List.of(athleticPortero, realJug1, athleticJug1, realJug3, athleticJug4), 24);
        usuario2.setPuntosAcumulados(usuario2.getPuntosAcumulados() + 24);
        usuarioRepository.save(usuario2);

        Alineacion alin2_3 = crearAlineacion(usuario3, jornada2,
                List.of(realPortero, athleticJug1, athleticJug2, realJug2, realJug3), 27);
        usuario3.setPuntosAcumulados(usuario3.getPuntosAcumulados() + 27);
        usuarioRepository.save(usuario3);

        // ASSERTS JORNADA 2: Verificar puntos de alineaciones
        assertEquals(28, alin2_1.getPuntosTotalesJornada(), "Usuario 1 debe tener 28 puntos en jornada 2");
        assertEquals(24, alin2_2.getPuntosTotalesJornada(), "Usuario 2 debe tener 24 puntos en jornada 2");
        assertEquals(27, alin2_3.getPuntosTotalesJornada(), "Usuario 3 debe tener 27 puntos en jornada 2");

        // ASSERTS: Verificar estadísticas individuales de jugadores jornada 2
        assertEquals(4, est2_1.getPuntosJornada(), "Álex Remiro debe tener 4 puntos");
        assertEquals(8, est2_2.getPuntosJornada(), "Mikel Oyarzabal debe tener 8 puntos");
        assertEquals(6, est2_3.getPuntosJornada(), "Takefusa Kubo debe tener 6 puntos");
        assertEquals(4, est2_5.getPuntosJornada(), "Unai Simón debe tener 4 puntos");
        assertEquals(6, est2_6.getPuntosJornada(), "Iñaki Williams debe tener 6 puntos");

        // ASSERTS: Verificar puntos acumulados totales después de 2 jornadas
        Usuario u1Actualizado = usuarioRepository.findById(usuario1.getIdUsuario()).orElse(null);
        Usuario u2Actualizado = usuarioRepository.findById(usuario2.getIdUsuario()).orElse(null);
        Usuario u3Actualizado = usuarioRepository.findById(usuario3.getIdUsuario()).orElse(null);

        assertNotNull(u1Actualizado);
        assertNotNull(u2Actualizado);
        assertNotNull(u3Actualizado);

        assertEquals(76, u1Actualizado.getPuntosAcumulados(), "Usuario 1 debe tener 76 puntos acumulados (48+28)");
        assertEquals(66, u2Actualizado.getPuntosAcumulados(), "Usuario 2 debe tener 66 puntos acumulados (42+24)");
        assertEquals(75, u3Actualizado.getPuntosAcumulados(), "Usuario 3 debe tener 75 puntos acumulados (48+27)");

        // ASSERTS: Verificar ranking de jornada 2
        List<Alineacion> rankingJ2 = alineacionRepository.findByJornadaIdJornada(jornada2.getIdJornada());
        rankingJ2.sort(Comparator.comparing(Alineacion::getPuntosTotalesJornada).reversed());

        assertEquals(28, rankingJ2.get(0).getPuntosTotalesJornada(), "El primer lugar de J2 debe tener 28 puntos");
        assertEquals(usuario1.getIdUsuario(), rankingJ2.get(0).getUsuario().getIdUsuario(),
                    "Usuario 1 debe estar en primera posición en J2");
        assertEquals(27, rankingJ2.get(1).getPuntosTotalesJornada(), "El segundo lugar de J2 debe tener 27 puntos");
        assertEquals(usuario3.getIdUsuario(), rankingJ2.get(1).getUsuario().getIdUsuario(),
                    "Usuario 3 debe estar en segunda posición en J2");
        assertEquals(24, rankingJ2.get(2).getPuntosTotalesJornada(), "El tercer lugar de J2 debe tener 24 puntos");
        assertEquals(usuario2.getIdUsuario(), rankingJ2.get(2).getUsuario().getIdUsuario(),
                    "Usuario 2 debe estar en tercera posición en J2");

        // ASSERTS: Verificar ranking general acumulado
        List<Usuario> rankingGeneral = usuarioRepository.findAll();
        rankingGeneral.sort(Comparator.comparing(Usuario::getPuntosAcumulados).reversed());

        assertEquals(76, rankingGeneral.get(0).getPuntosAcumulados(), "El primer lugar general debe tener 76 puntos");
        assertEquals(usuario1.getIdUsuario(), rankingGeneral.get(0).getIdUsuario(),
                    "Usuario 1 debe estar primero en clasificación general");
        assertEquals(75, rankingGeneral.get(1).getPuntosAcumulados(), "El segundo lugar general debe tener 75 puntos");
        assertEquals(usuario3.getIdUsuario(), rankingGeneral.get(1).getIdUsuario(),
                    "Usuario 3 debe estar segundo en clasificación general");
        assertEquals(66, rankingGeneral.get(2).getPuntosAcumulados(), "El tercer lugar general debe tener 66 puntos");
        assertEquals(usuario2.getIdUsuario(), rankingGeneral.get(2).getIdUsuario(),
                    "Usuario 2 debe estar tercero en clasificación general");

        mostrarRanking(liga, jornada2);

        // ==================== ELIMINAR USUARIO ====================
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║              ELIMINACIÓN DE USUARIO Y RANKING FINAL            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        System.out.println("⚠️  Eliminando usuario: " + usuario2.getNombreUsuario());
        Long idUsuarioEliminado = usuario2.getIdUsuario();

        // Eliminar alineaciones del usuario
        alineacionRepository.delete(alin1_2);
        alineacionRepository.delete(alin2_2);

        // Eliminar usuario
        usuarioRepository.delete(usuario2);
        System.out.println("✓ Usuario eliminado correctamente\n");

        // ASSERTS: Verificar que el usuario fue eliminado
        assertFalse(usuarioRepository.findById(idUsuarioEliminado).isPresent(),
                   "El usuario debe estar eliminado de la base de datos");

        // ASSERTS: Verificar que las alineaciones del usuario eliminado ya no existen
        List<Alineacion> alineacionesUsuario2 = alineacionRepository.findByUsuarioIdUsuario(idUsuarioEliminado);
        assertTrue(alineacionesUsuario2.isEmpty(),
                  "No deben existir alineaciones del usuario eliminado");

        // ASSERTS: Verificar que las alineaciones de J1 del usuario eliminado no existen
        List<Alineacion> alineacionesJ1DespuesEliminacion =
            alineacionRepository.findByJornadaIdJornada(jornada1.getIdJornada());
        assertEquals(2, alineacionesJ1DespuesEliminacion.size(),
                    "Jornada 1 debe tener solo 2 alineaciones tras eliminar usuario");
        for (Alineacion alin : alineacionesJ1DespuesEliminacion) {
            assertNotEquals(idUsuarioEliminado, alin.getUsuario().getIdUsuario(),
                          "Ninguna alineación debe pertenecer al usuario eliminado");
        }

        // ASSERTS: Verificar que las alineaciones de J2 del usuario eliminado no existen
        List<Alineacion> alineacionesJ2DespuesEliminacion =
            alineacionRepository.findByJornadaIdJornada(jornada2.getIdJornada());
        assertEquals(2, alineacionesJ2DespuesEliminacion.size(),
                    "Jornada 2 debe tener solo 2 alineaciones tras eliminar usuario");
        for (Alineacion alin : alineacionesJ2DespuesEliminacion) {
            assertNotEquals(idUsuarioEliminado, alin.getUsuario().getIdUsuario(),
                          "Ninguna alineación debe pertenecer al usuario eliminado");
        }

        // Mostrar ranking final
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  RANKING FINAL DE LA LIGA (Después de eliminar usuario)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        List<Usuario> usuariosFinales = usuarioRepository.findAll();
        usuariosFinales.sort(Comparator.comparing(Usuario::getPuntosAcumulados).reversed());

        int posicion = 1;
        for (Usuario usuario : usuariosFinales) {
            String medalla = posicion == 1 ? "🥇" : posicion == 2 ? "🥈" : "🥉";
            System.out.printf("%s %d. %-25s %4d puntos\n",
                            medalla, posicion, usuario.getNombreUsuario(),
                            usuario.getPuntosAcumulados());
            posicion++;
        }

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  ALINEACIONES RESTANTES EN LA LIGA");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        List<Alineacion> alineacionesRestantes = alineacionRepository.findAll();
        System.out.println("Total de alineaciones en el sistema: " + alineacionesRestantes.size());

        for (Alineacion alineacion : alineacionesRestantes) {
            System.out.println("\n┌─────────────────────────────────────────────────────────────┐");
            System.out.println("│ Usuario: " + String.format("%-48s", alineacion.getUsuario().getNombreUsuario()) + "│");
            System.out.println("│ Jornada: " + String.format("%-48s", alineacion.getJornada().getIdJornada()) + "│");
            System.out.println("│ Puntos:  " + String.format("%-48d", alineacion.getPuntosTotalesJornada()) + "│");
            System.out.println("├─────────────────────────────────────────────────────────────┤");
            System.out.println("│ Jugadores alineados:                                        │");
            for (Jugador jug : alineacion.getJugadores()) {
                String tipo = jug.isEsPortero() ? "(Portero)" : "";
                System.out.println("│   - " + String.format("%-52s", jug.getNombreJugador() + " " + tipo) + "│");
            }
            System.out.println("└─────────────────────────────────────────────────────────────┘");
        }

        // Verificaciones finales
        assertEquals(2, usuariosFinales.size(), "Deben quedar 2 usuarios");
        assertEquals(4, alineacionesRestantes.size(), "Deben quedar 4 alineaciones (2 por cada usuario restante)");

        // ASSERTS: Verificar que el usuario eliminado NO está en el ranking final
        for (Usuario usuario : usuariosFinales) {
            assertNotEquals(idUsuarioEliminado, usuario.getIdUsuario(),
                          "El usuario eliminado no debe aparecer en el ranking final");
        }

        // ASSERTS: Verificar posiciones correctas en el ranking final
        assertEquals(usuario1.getIdUsuario(), usuariosFinales.get(0).getIdUsuario(),
                    "Usuario 1 debe estar en primera posición del ranking final");
        assertEquals(76, usuariosFinales.get(0).getPuntosAcumulados(),
                    "Usuario 1 debe tener 76 puntos en el ranking final");

        assertEquals(usuario3.getIdUsuario(), usuariosFinales.get(1).getIdUsuario(),
                    "Usuario 3 debe estar en segunda posición del ranking final");
        assertEquals(75, usuariosFinales.get(1).getPuntosAcumulados(),
                    "Usuario 3 debe tener 75 puntos en el ranking final");

        // ASSERTS: Verificar que todas las alineaciones restantes pertenecen solo a los 2 usuarios activos
        for (Alineacion alin : alineacionesRestantes) {
            assertTrue(alin.getUsuario().getIdUsuario().equals(usuario1.getIdUsuario()) ||
                      alin.getUsuario().getIdUsuario().equals(usuario3.getIdUsuario()),
                      "Todas las alineaciones deben pertenecer a Usuario 1 o Usuario 3");
            assertNotEquals(idUsuarioEliminado, alin.getUsuario().getIdUsuario(),
                          "Ninguna alineación debe pertenecer al usuario eliminado");
        }

        // ASSERTS: Verificar que cada usuario restante tiene exactamente 2 alineaciones
        final Long idUsuario1 = usuario1.getIdUsuario();
        final Long idUsuario3 = usuario3.getIdUsuario();

        long alineacionesUsuario1 = alineacionesRestantes.stream()
            .filter(a -> a.getUsuario().getIdUsuario().equals(idUsuario1))
            .count();
        long alineacionesUsuario3 = alineacionesRestantes.stream()
            .filter(a -> a.getUsuario().getIdUsuario().equals(idUsuario3))
            .count();

        assertEquals(2, alineacionesUsuario1, "Usuario 1 debe tener exactamente 2 alineaciones");
        assertEquals(2, alineacionesUsuario3, "Usuario 3 debe tener exactamente 2 alineaciones");


        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    TEST COMPLETADO ✓                           ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private Jugador crearJugador(String nombre, boolean esPortero, float precio, Equipo equipo) {
        Jugador jugador = new Jugador();
        jugador.setNombreJugador(nombre);
        jugador.setEsPortero(esPortero);
        jugador.setPrecioMercado(precio);
        jugador.setEquipo(equipo);
        return jugadorRepository.save(jugador);
    }

    private EstadisticaJugadorPartido crearEstadistica(Jugador jugador, Partido partido,
                                                       int goles, int golesRecibidos, int asistencias,
                                                       int tarjetasAmarillas, boolean tarjetaRoja,
                                                       boolean masde25min, int puntos) {
        EstadisticaJugadorPartido est = new EstadisticaJugadorPartido();
        est.setJugador(jugador);
        est.setPartido(partido);
        est.setGolesAnotados(goles);
        est.setGolesRecibidos(golesRecibidos);
        est.setAsistencias(asistencias);
        est.setTarjetaAmarillas(tarjetasAmarillas);
        est.setTarjetaRojas(tarjetaRoja);
        est.setMinMinutosJugados(masde25min);
        est.setPuntosJornada(puntos);
        est = estadisticaRepository.save(est);

        System.out.println("   " + jugador.getNombreJugador() + " → " + puntos + " pts " +
                         "(G:" + goles + " A:" + asistencias + " TA:" + tarjetasAmarillas +
                         (tarjetaRoja ? " TR:1" : "") + ")");
        return est;
    }

    private Alineacion crearAlineacion(Usuario usuario, Jornada jornada,
                                       List<Jugador> jugadores, int puntosTotales) {
        Alineacion alineacion = new Alineacion();
        alineacion.setUsuario(usuario);
        alineacion.setJornada(jornada);
        alineacion.setPuntosTotalesJornada(puntosTotales);
        alineacion.setJugadores(new ArrayList<>(jugadores));
        alineacion = alineacionRepository.save(alineacion);

        System.out.println("   " + usuario.getNombreUsuario() + " → " + puntosTotales + " pts");
        for (Jugador jug : jugadores) {
            System.out.println("      - " + jug.getNombreJugador() +
                             (jug.isEsPortero() ? " (Portero)" : ""));
        }

        return alineacion;
    }

    private void mostrarRanking(LigaCume liga, Jornada jornada) {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  CLASIFICACIÓN JORNADA " + jornada.getIdJornada());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        List<Alineacion> alineaciones = alineacionRepository.findByJornadaIdJornada(jornada.getIdJornada());
        alineaciones.sort(Comparator.comparing(Alineacion::getPuntosTotalesJornada).reversed());

        int posicion = 1;
        for (Alineacion alin : alineaciones) {
            String medalla = posicion == 1 ? "🥇" : posicion == 2 ? "🥈" : posicion == 3 ? "🥉" : "  ";
            System.out.printf("%s %d. %-25s %4d puntos\n",
                            medalla, posicion,
                            alin.getUsuario().getNombreUsuario(),
                            alin.getPuntosTotalesJornada());
            posicion++;
        }

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  PUNTOS ACUMULADOS TOTALES");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        List<Usuario> usuarios = usuarioRepository.findAll();
        usuarios.sort(Comparator.comparing(Usuario::getPuntosAcumulados).reversed());

        posicion = 1;
        for (Usuario usuario : usuarios) {
            String medalla = posicion == 1 ? "🥇" : posicion == 2 ? "🥈" : "🥉";
            System.out.printf("%s %d. %-25s %4d puntos\n",
                            medalla, posicion, usuario.getNombreUsuario(),
                            usuario.getPuntosAcumulados());
            posicion++;
        }

        System.out.println();
    }
}
