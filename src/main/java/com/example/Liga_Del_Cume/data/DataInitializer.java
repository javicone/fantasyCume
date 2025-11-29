package com.example.Liga_Del_Cume.data;

import com.example.Liga_Del_Cume.data.model.*;
import com.example.Liga_Del_Cume.data.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Script para poblar la base de datos con datos iniciales de prueba
 *
 * Este componente se ejecuta automáticamente al iniciar la aplicación
 * y crea una liga completa con usuarios, equipos, jugadores, jornadas,
 * partidos, estadísticas y alineaciones.
 *
 * NOTA: Solo se ejecuta con el perfil 'dev' activo
 * Para activarlo, agregar en application.properties:
 * spring.profiles.active=dev
 *
 * Para desactivarlo, comentar la línea anterior o cambiar el perfil.
 */
@Component
@Profile("dev") // Solo se ejecuta en modo desarrollo
public class DataInitializer implements CommandLineRunner {

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

    @Override
    public void run(String... args) throws Exception {
        // Verificar si ya hay datos en la base de datos
        if (ligaCumeRepository.count() > 0) {
            System.out.println("\n⚠️  La base de datos ya contiene datos. Saltando inicialización.\n");
            return;
        }

        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║          INICIALIZANDO BASE DE DATOS                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        poblarBaseDatos();

        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║           ✅ BASE DE DATOS INICIALIZADA CORRECTAMENTE         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");
    }

    private void poblarBaseDatos() {
        // ============ PASO 1: CREAR LIGA ============
        System.out.println("📋 PASO 1: Creando Liga...");
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("LigaCume Fantasy 2024-2025");
        liga.setPresupuestoMaximo(100000000L);
        liga = ligaCumeRepository.save(liga);
        System.out.println("✓ Liga creada: " + liga.getNombreLiga());
        System.out.println("  Presupuesto máximo: " + liga.getPresupuestoMaximo() + "€\n");

        // ============ PASO 2: CREAR USUARIOS ============
        System.out.println("👥 PASO 2: Creando 3 usuarios...");
        Usuario usuario1 = crearUsuario("Ibai Llanos", "ibai@fantasy.com", "pass123", liga);
        Usuario usuario2 = crearUsuario("ElRubius", "rubius@fantasy.com", "pass123", liga);
        Usuario usuario3 = crearUsuario("DJMaRiiO", "djmario@fantasy.com", "pass123", liga);
        Usuario usuario4 = crearUsuario("aaa", "aaa@ex.com", "123123", liga);

        System.out.println();

        // ============ PASO 3: CREAR EQUIPOS ============
        System.out.println("⚽ PASO 3: Creando 4 equipos...");
        Equipo equipo1 = crearEquipo("Real Madrid", "https://upload.wikimedia.org/wikipedia/en/5/56/Real_Madrid_CF.svg", liga);
        Equipo equipo2 = crearEquipo("FC Barcelona", "https://upload.wikimedia.org/wikipedia/en/4/47/FC_Barcelona.svg", liga);
        Equipo equipo3 = crearEquipo("Atlético Madrid", "https://upload.wikimedia.org/wikipedia/en/f/f4/Atletico_Madrid.svg", liga);
        Equipo equipo4 = crearEquipo("Sevilla FC", "https://upload.wikimedia.org/wikipedia/en/3/3b/Sevilla_FC_logo.svg", liga);
        System.out.println("✓ Equipos creados correctamente\n");

        // ============ PASO 4: CREAR JUGADORES (5 por equipo = 20 total) ============
        System.out.println("🏃 PASO 4: Creando jugadores (5 por equipo)...");

        // Real Madrid
        Jugador rm1 = crearJugador("Courtois", true, equipo1, 40000000);
        Jugador rm2 = crearJugador("Vinicius Jr", false, equipo1, 80000000);
        Jugador rm3 = crearJugador("Bellingham", false, equipo1, 100000000);
        Jugador rm4 = crearJugador("Rodrygo", false, equipo1, 60000000);
        Jugador rm5 = crearJugador("Valverde", false, equipo1, 70000000);
        System.out.println("✓ Real Madrid: 5 jugadores");

        // FC Barcelona
        Jugador fcb1 = crearJugador("Ter Stegen", true, equipo2, 30000000);
        Jugador fcb2 = crearJugador("Lewandowski", false, equipo2, 45000000);
        Jugador fcb3 = crearJugador("Gavi", false, equipo2, 90000000);
        Jugador fcb4 = crearJugador("Pedri", false, equipo2, 80000000);
        Jugador fcb5 = crearJugador("Raphinha", false, equipo2, 50000000);
        System.out.println("✓ FC Barcelona: 5 jugadores");

        // Atlético Madrid
        Jugador atm1 = crearJugador("Oblak", true, equipo3, 35000000);
        Jugador atm2 = crearJugador("Griezmann", false, equipo3, 40000000);
        Jugador atm3 = crearJugador("Morata", false, equipo3, 25000000);
        Jugador atm4 = crearJugador("Koke", false, equipo3, 15000000);
        Jugador atm5 = crearJugador("De Paul", false, equipo3, 30000000);
        System.out.println("✓ Atlético Madrid: 5 jugadores");

        // Sevilla FC
        Jugador sev1 = crearJugador("Bounou", true, equipo4, 20000000);
        Jugador sev2 = crearJugador("Ocampos", false, equipo4, 25000000);
        Jugador sev3 = crearJugador("En-Nesyri", false, equipo4, 20000000);
        Jugador sev4 = crearJugador("Rakitic", false, equipo4, 10000000);
        Jugador sev5 = crearJugador("Acuña", false, equipo4, 15000000);
        System.out.println("✓ Sevilla FC: 5 jugadores\n");

        // ============ JORNADA 1 ============
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("                         JORNADA 1                             ");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        Jornada jornada1 = new Jornada();
        jornada1.setLiga(liga);
        jornada1.setNumeroJornada(1);
        jornada1 = jornadaRepository.save(jornada1);
        System.out.println("📅 Jornada 1 creada (ID: " + jornada1.getIdJornada() + ")\n");

        // Partido 1: Real Madrid vs Barcelona
        System.out.println("⚽ Partido 1: Real Madrid 2 - 1 Barcelona");
        Partido partido1J1 = crearPartido(equipo1, equipo2, 2, 1, jornada1);

        // Estadísticas Partido 1
        crearEstadistica(rm1, partido1J1, 0, 0, 0, false, true, 1, 3);
        crearEstadistica(rm2, partido1J1, 1, 1, 0, false, true, 0, 12);
        crearEstadistica(rm3, partido1J1, 1, 0, 0, false, true, 0, 7);
        crearEstadistica(fcb1, partido1J1, 0, 0, 0, false, true, 2, 1);
        crearEstadistica(fcb2, partido1J1, 1, 0, 1, false, true, 0, 5);
        crearEstadistica(fcb3, partido1J1, 0, 1, 0, false, true, 0, 5);
        System.out.println("  ✓ Estadísticas registradas\n");

        // Partido 2: Atlético Madrid vs Sevilla
        System.out.println("⚽ Partido 2: Atlético Madrid 3 - 0 Sevilla");
        Partido partido2J1 = crearPartido(equipo3, equipo4, 3, 0, jornada1);

        // Estadísticas Partido 2
        crearEstadistica(atm1, partido2J1, 0, 0, 0, false, true, 0, 8);
        crearEstadistica(atm2, partido2J1, 2, 0, 0, false, true, 0, 14);
        crearEstadistica(atm3, partido2J1, 1, 2, 0, false, true, 0, 12);
        crearEstadistica(sev1, partido2J1, 0, 0, 0, false, true, 3, -2);
        crearEstadistica(sev2, partido2J1, 0, 0, 1, false, true, 0, 1);
        System.out.println("  ✓ Estadísticas registradas\n");

        // Crear alineaciones para Jornada 1
        System.out.println("📝 Creando alineaciones para Jornada 1...");
        crearAlineacion(usuario1, jornada1, rm1, rm2, fcb2, atm2, atm3);
        crearAlineacion(usuario2, jornada1, atm1, rm3, fcb3, atm2, sev2);
        crearAlineacion(usuario3, jornada1, fcb1, rm2, atm3, fcb2, sev2);
        System.out.println("✓ Alineaciones creadas\n");

        // Mostrar resultados Jornada 1
        mostrarResultadosJornada("JORNADA 1", jornada1, usuario1, usuario2, usuario3);

        // ============ JORNADA 2 ============
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("                         JORNADA 2                             ");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        Jornada jornada2 = new Jornada();
        jornada2.setLiga(liga);
        jornada2.setNumeroJornada(2);
        jornada2 = jornadaRepository.save(jornada2);
        System.out.println("📅 Jornada 2 creada (ID: " + jornada2.getIdJornada() + ")\n");

        // Partido 1: Barcelona vs Atlético Madrid
        System.out.println("⚽ Partido 1: Barcelona 1 - 1 Atlético Madrid");
        Partido partido1J2 = crearPartido(equipo2, equipo3, 1, 1, jornada2);

        // Estadísticas Partido 1
        crearEstadistica(fcb1, partido1J2, 0, 0, 0, false, true, 1, 3);
        crearEstadistica(fcb2, partido1J2, 1, 0, 0, false, true, 0, 7);
        crearEstadistica(fcb4, partido1J2, 0, 1, 0, false, true, 0, 5);
        crearEstadistica(atm1, partido1J2, 0, 0, 0, false, true, 1, 3);
        crearEstadistica(atm2, partido1J2, 1, 0, 0, false, true, 0, 7);
        crearEstadistica(atm4, partido1J2, 0, 1, 1, false, true, 0, 3);
        System.out.println("  ✓ Estadísticas registradas\n");

        // Partido 2: Sevilla vs Real Madrid
        System.out.println("⚽ Partido 2: Sevilla 0 - 4 Real Madrid");
        Partido partido2J2 = crearPartido(equipo4, equipo1, 0, 4, jornada2);

        // Estadísticas Partido 2
        crearEstadistica(sev1, partido2J2, 0, 0, 0, false, true, 4, -3);
        crearEstadistica(sev3, partido2J2, 0, 0, 0, false, false, 0, -1);
        crearEstadistica(rm1, partido2J2, 0, 0, 0, false, true, 0, 8);
        crearEstadistica(rm2, partido2J2, 2, 1, 0, false, true, 0, 17);
        crearEstadistica(rm4, partido2J2, 1, 1, 0, false, true, 0, 12);
        crearEstadistica(rm5, partido2J2, 1, 0, 0, false, true, 0, 7);
        System.out.println("  ✓ Estadísticas registradas\n");

        // Crear alineaciones para Jornada 2
        System.out.println("📝 Creando alineaciones para Jornada 2...");
        crearAlineacion(usuario1, jornada2, rm1, rm2, rm4, fcb2, atm2);
        crearAlineacion(usuario2, jornada2, fcb1, rm2, rm5, fcb4, atm4);
        crearAlineacion(usuario3, jornada2, atm1, rm4, fcb2, sev3, atm2);
        System.out.println("✓ Alineaciones creadas\n");

        // Mostrar resultados Jornada 2
        mostrarResultadosJornada("JORNADA 2", jornada2, usuario1, usuario2, usuario3);

        // ============ RESUMEN FINAL ============
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    CLASIFICACIÓN FINAL                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        // Actualizar puntos acumulados de usuarios
        actualizarPuntosUsuario(usuario1, jornada1, jornada2);
        actualizarPuntosUsuario(usuario2, jornada1, jornada2);
        actualizarPuntosUsuario(usuario3, jornada1, jornada2);

        // Obtener clasificación
        List<Usuario> clasificacion = usuarioRepository.findByLigaIdLigaCumeOrderByPuntosAcumuladosDesc(liga.getIdLigaCume());

        int posicion = 1;
        for (Usuario u : clasificacion) {
            String medalla = posicion == 1 ? "🥇" : posicion == 2 ? "🥈" : "🥉";
            System.out.printf("%s %d. %-20s %4d puntos%n", medalla, posicion, u.getNombreUsuario(), u.getPuntosAcumulados());
            posicion++;
        }

        // Resumen de datos creados
        System.out.println("\n📊 RESUMEN DE DATOS CREADOS:");
        System.out.println("  • Ligas: " + ligaCumeRepository.count());
        System.out.println("  • Usuarios: " + usuarioRepository.count());
        System.out.println("  • Equipos: " + equipoRepository.count());
        System.out.println("  • Jugadores: " + jugadorRepository.count());
        System.out.println("  • Jornadas: " + jornadaRepository.count());
        System.out.println("  • Partidos: " + partidoRepository.count());
        System.out.println("  • Alineaciones: " + alineacionRepository.count());
        System.out.println("  • Estadísticas: " + estadisticaRepository.count());
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private Usuario crearUsuario(String nombre, String email, String password, LigaCume liga) {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombre);
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setPuntosAcumulados(0);
        usuario.setLiga(liga);
        usuario = usuarioRepository.save(usuario);
        System.out.println("✓ Usuario: " + nombre);
        return usuario;
    }

    private Usuario crearUsuarioSinLiga(String nombre, String email, String password) {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombre);
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario = usuarioRepository.save(usuario);
        System.out.println("✓ Usuario: " + nombre);
        return usuario;
    }

    private Equipo crearEquipo(String nombre, String escudo, LigaCume liga) {
        Equipo equipo = new Equipo();
        equipo.setNombreEquipo(nombre);
        equipo.setEscudoURL(escudo);
        equipo.setLiga(liga);
        equipo = equipoRepository.save(equipo);
        System.out.println("  ✓ " + nombre);
        return equipo;
    }

    private Jugador crearJugador(String nombre, boolean esPortero, Equipo equipo, int precio) {
        Jugador jugador = new Jugador();
        jugador.setNombreJugador(nombre);
        jugador.setEsPortero(esPortero);
        jugador.setEquipo(equipo);
        jugador.setPrecioMercado(precio);
        jugador.setAvatarUrl("https://via.placeholder.com/150");
        return jugadorRepository.save(jugador);
    }

    private Partido crearPartido(Equipo local, Equipo visitante, int golesLocal, int golesVisitante, Jornada jornada) {
        Partido partido = new Partido();
        partido.setEquipoLocal(local);
        partido.setEquipoVisitante(visitante);
        partido.setGolesLocal(golesLocal);
        partido.setGolesVisitante(golesVisitante);
        partido.setJornada(jornada);
        return partidoRepository.save(partido);
    }

    private void crearEstadistica(Jugador jugador, Partido partido, int goles, int asistencias,
                                  int amarillas, boolean roja, boolean jugador60, int golesRecibidos, int puntos) {
        EstadisticaJugadorPartido est = new EstadisticaJugadorPartido();
        est.setJugador(jugador);
        est.setPartido(partido);
        est.setGolesAnotados(goles);
        est.setAsistencias(asistencias);
        est.setTarjetaAmarillas(amarillas);
        est.setTarjetaRojas(roja);
        est.setMinMinutosJugados(jugador60);
        est.setGolesRecibidos(golesRecibidos);
        est.setPuntosJornada(puntos);
        estadisticaRepository.save(est);
    }

    private void crearAlineacion(Usuario usuario, Jornada jornada, Jugador j1, Jugador j2,
                                 Jugador j3, Jugador j4, Jugador j5) {
        Alineacion alineacion = new Alineacion();
        alineacion.setUsuario(usuario);
        alineacion.setJornada(jornada);
        alineacion.getJugadores().add(j1);
        alineacion.getJugadores().add(j2);
        alineacion.getJugadores().add(j3);
        alineacion.getJugadores().add(j4);
        alineacion.getJugadores().add(j5);

        // Calcular puntos
        int puntos = calcularPuntosAlineacion(alineacion);
        alineacion.setPuntosTotalesJornada(puntos);

        alineacionRepository.save(alineacion);
    }

    private int calcularPuntosAlineacion(Alineacion alineacion) {
        int total = 0;
        for (Jugador jugador : alineacion.getJugadores()) {
            List<EstadisticaJugadorPartido> stats = estadisticaRepository.findByJugadorIdJugador(jugador.getIdJugador());
            for (EstadisticaJugadorPartido stat : stats) {
                if (stat.getPartido().getJornada().getIdJornada().equals(alineacion.getJornada().getIdJornada())) {
                    total += stat.getPuntosJornada();
                }
            }
        }
        return total;
    }

    private void mostrarResultadosJornada(String nombreJornada, Jornada jornada, Usuario... usuarios) {
        System.out.println("📊 Puntuaciones " + nombreJornada + ":");
        for (Usuario usuario : usuarios) {
            alineacionRepository.findByUsuarioIdUsuarioAndJornadaIdJornada(
                    usuario.getIdUsuario(), jornada.getIdJornada()
            ).ifPresent(alin ->
                System.out.printf("  • %-20s: %3d puntos%n", usuario.getNombreUsuario(), alin.getPuntosTotalesJornada())
            );
        }
        System.out.println();
    }

    private void actualizarPuntosUsuario(Usuario usuario, Jornada... jornadas) {
        int puntosTotal = 0;
        for (Jornada jornada : jornadas) {
            var alineacionOpt = alineacionRepository.findByUsuarioIdUsuarioAndJornadaIdJornada(
                    usuario.getIdUsuario(), jornada.getIdJornada()
            );
            if (alineacionOpt.isPresent()) {
                puntosTotal += alineacionOpt.get().getPuntosTotalesJornada();
            }
        }
        usuario.setPuntosAcumulados(puntosTotal);
        usuarioRepository.save(usuario);
    }
}

