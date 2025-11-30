package com.example.Liga_Del_Cume.data;

import com.example.Liga_Del_Cume.data.model.*;
import com.example.Liga_Del_Cume.data.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Script extendido para poblar la base de datos.
 * Genera una liga de 6 equipos, 5 usuarios y simula media temporada (5 jornadas).
 */
@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    @Autowired private LigaCumeRepository ligaCumeRepository;
    @Autowired private EquipoRepository equipoRepository;
    @Autowired private JugadorRepository jugadorRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private JornadaRepository jornadaRepository;
    @Autowired private PartidoRepository partidoRepository;
    @Autowired private AlineacionRepository alineacionRepository;
    @Autowired private EstadisticaJugadorPartidoRepository estadisticaRepository;

    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        if (ligaCumeRepository.count() > 0) {
            System.out.println("\n⚠️  La base de datos ya contiene datos. Saltando inicialización.\n");
            return;
        }

        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║          INICIALIZANDO LIGA FANTASY EXTENDIDA                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        poblarBaseDatosCompleta();

        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║           ✅ BASE DE DATOS INICIALIZADA CORRECTAMENTE         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");
    }

    private void poblarBaseDatosCompleta() {
        // 1. Crear Liga
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("LigaCume Fantasy 2024-2025");
        liga.setPresupuestoMaximo(100000000L);
        liga = ligaCumeRepository.save(liga);
        System.out.println("✓ Liga creada: " + liga.getNombreLiga());

        // 2. Crear 5 Usuarios
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(crearUsuario("Ibai Llanos", "ibai@fantasy.com", liga));
        usuarios.add(crearUsuario("ElRubius", "rubius@fantasy.com", liga));
        usuarios.add(crearUsuario("DJMaRiiO", "djmario@fantasy.com", liga));
        usuarios.add(crearUsuario("TheGrefg", "grefg@fantasy.com", liga));
        usuarios.add(crearUsuario("IlloJuan", "illojuan@fantasy.com", liga));
        System.out.println("✓ 5 Usuarios creados");

        // 3. Crear 6 Equipos y sus Jugadores
        List<Equipo> equipos = crearEquiposYJugadores(liga);

        // 4. Generar Calendario (10 Jornadas) y Simular las primeras 5
        generarYSimularJornadas(liga, equipos, usuarios);

        // 5. Calcular Clasificación Final
        mostrarClasificacion(liga);
    }

    // ==================== CREACIÓN DE EQUIPOS Y JUGADORES ====================

    private List<Equipo> crearEquiposYJugadores(LigaCume liga) {
        System.out.println("\n⚽ Creando equipos y jugadores...");
        List<Equipo> listaEquipos = new ArrayList<>();

        // Equipo 1: Real Madrid
        Equipo rm = crearEquipo("Real Madrid", "https://logodownload.org/wp-content/uploads/2016/03/real-madrid-logo-1.png", liga);
        crearJugadoresEquipo(rm, "Courtois", "Vinicius Jr", "Bellingham", "Rodrygo", "Valverde");
        listaEquipos.add(rm);

        // Equipo 2: FC Barcelona
        Equipo fcb = crearEquipo("FC Barcelona", "https://logodownload.org/wp-content/uploads/2015/05/barcelona-logo-escudo-1.png", liga);
        crearJugadoresEquipo(fcb, "Ter Stegen", "Lewandowski", "Lamine Yamal", "Pedri", "Gavi");
        listaEquipos.add(fcb);

        // Equipo 3: Atlético Madrid
        Equipo atm = crearEquipo("Atlético Madrid", "https://logodownload.org/wp-content/uploads/2016/10/atletico-madrid-logo-1.png", liga);
        crearJugadoresEquipo(atm, "Oblak", "Griezmann", "Julián Álvarez", "Koke", "De Paul");
        listaEquipos.add(atm);

        // Equipo 4: Sevilla FC
        Equipo sev = crearEquipo("Sevilla FC", "https://logodownload.org/wp-content/uploads/2018/05/sevilla-fc-logo-1.png", liga);
        crearJugadoresEquipo(sev, "Nyland", "Ocampos", "Isaac Romero", "Jesús Navas", "Sow");
        listaEquipos.add(sev);

        // Equipo 5: Valencia CF
        Equipo vcf = crearEquipo("Valencia CF", "https://logodownload.org/wp-content/uploads/2018/05/valencia-cf-logo-1.png", liga);
        crearJugadoresEquipo(vcf, "Mamardashvili", "Hugo Duro", "Pepelu", "Gayà", "Javi Guerra");
        listaEquipos.add(vcf);

        // Equipo 6: Real Betis
        Equipo bet = crearEquipo("Real Betis", "https://logodownload.org/wp-content/uploads/2017/02/real-betis-logo-1.png", liga);
        crearJugadoresEquipo(bet, "Rui Silva", "Isco", "Lo Celso", "Fekir", "Bartra");
        listaEquipos.add(bet);

        return listaEquipos;
    }

    private void crearJugadoresEquipo(Equipo equipo, String... nombres) {
        boolean primerEsPortero = true;
        for (String nombre : nombres) {
            Jugador j = new Jugador();
            j.setNombreJugador(nombre);
            j.setEsPortero(primerEsPortero);
            j.setEquipo(equipo);
            // Precio aleatorio entre 5M y 100M
            j.setPrecioMercado(5000000 + random.nextInt(95000000));
            j.setAvatarUrl("https://ui-avatars.com/api/?name=" + nombre.replace(" ", "+") + "&background=random");
            jugadorRepository.save(j);
            primerEsPortero = false; // Solo el primero es portero
        }
    }

    // ==================== LÓGICA DE JORNADAS Y SIMULACIÓN ====================

    private void generarYSimularJornadas(LigaCume liga, List<Equipo> equipos, List<Usuario> usuarios) {
        // Algoritmo Round Robin simple para 6 equipos
        // Indices: 0 vs 5, 1 vs 4, 2 vs 3 en la primera rotación
        int numEquipos = equipos.size(); // 6
        int numJornadasIda = numEquipos - 1; // 5
        int totalJornadas = numJornadasIda * 2; // 10

        // Copia para rotar
        List<Equipo> equiposRotacion = new ArrayList<>(equipos);
        Equipo equipoFijo = equiposRotacion.remove(0); // Mantenemos uno fijo para el algoritmo

        System.out.println("\n📅 Generando " + totalJornadas + " jornadas...");

        for (int dia = 0; dia < totalJornadas; dia++) {
            int numeroJornada = dia + 1;
            Jornada jornada = new Jornada();
            jornada.setLiga(liga);
            jornada.setNumeroJornada(numeroJornada);
            jornada = jornadaRepository.save(jornada);

            boolean esSimulada = numeroJornada <= 5; // Solo simulamos las primeras 5
            System.out.println("  ➜ Jornada " + numeroJornada + (esSimulada ? " (Simulada)" : " (Pendiente)"));

            // Generar los 3 partidos de la jornada
            int equipoIdx1 = 0; // El fijo siempre juega con el que toca en el ciclo
            int equipoIdx2 = equiposRotacion.size() - 1;

            // Partido del equipo fijo
            Equipo localFijo, visitanteFijo;
            // Alternar local/visitante cada jornada
            if (dia % 2 == 0) {
                localFijo = equipoFijo;
                visitanteFijo = equiposRotacion.get(equipoIdx2);
            } else {
                localFijo = equiposRotacion.get(equipoIdx2);
                visitanteFijo = equipoFijo;
            }
            procesarPartido(jornada, localFijo, visitanteFijo, esSimulada);
            equipoIdx2--;

            // Resto de partidos
            for (int i = 0; i < (equiposRotacion.size() / 2); i++) {
                Equipo e1 = equiposRotacion.get(i);
                Equipo e2 = equiposRotacion.get(equipoIdx2);

                Equipo local, visitante;
                if (dia % 2 == 0) {
                    local = e1; visitante = e2;
                } else {
                    local = e2; visitante = e1;
                }

                procesarPartido(jornada, local, visitante, esSimulada);
                equipoIdx2--;
            }

            // Simular alineaciones de usuarios si la jornada está simulada
            if (esSimulada) {
                simularAlineacionesUsuarios(usuarios, jornada);
            }

            // Rotar equipos (excepto el fijo)
            Collections.rotate(equiposRotacion, 1);
        }
    }

    private void procesarPartido(Jornada jornada, Equipo local, Equipo visitante, boolean simular) {
        Partido partido = new Partido();
        partido.setJornada(jornada);
        partido.setEquipoLocal(local);
        partido.setEquipoVisitante(visitante);

        if (simular) {
            // Generar resultado aleatorio
            int golesLocal = random.nextInt(4); // 0-3
            int golesVisitante = random.nextInt(3); // 0-2
            partido.setGolesLocal(golesLocal);
            partido.setGolesVisitante(golesVisitante);
            partido = partidoRepository.save(partido);

            // Generar estadísticas para los jugadores
            generarEstadisticasEquipo(local, partido, golesLocal, golesVisitante, true);
            generarEstadisticasEquipo(visitante, partido, golesVisitante, golesLocal, false);
        } else {
            // Partido futuro
            partido.setGolesLocal(0);
            partido.setGolesVisitante(0);
            partidoRepository.save(partido);
        }
    }

    private void generarEstadisticasEquipo(Equipo equipo, Partido partido, int golesFavor, int golesContra, boolean esLocal) {
        List<Jugador> jugadores = jugadorRepository.findByEquipoIdEquipo(equipo.getIdEquipo());

        // Repartir los goles aleatoriamente entre los jugadores (excepto portero si se quiere realismo, pero lo simplificamos)
        int golesPorAsignar = golesFavor;

        for (Jugador jugador : jugadores) {
            EstadisticaJugadorPartido est = new EstadisticaJugadorPartido();
            est.setJugador(jugador);
            est.setPartido(partido);
            est.setMinMinutosJugados(true); // Asumimos que todos jugaron
            est.setGolesRecibidos(jugador.isEsPortero() ? golesContra : 0);

            // Asignar Goles
            int golesJugador = 0;
            if (golesPorAsignar > 0 && !jugador.isEsPortero()) {
                if (random.nextBoolean()) { // 50% chance de meter gol si quedan goles
                    golesJugador = 1 + random.nextInt(golesPorAsignar); // al menos 1
                    if(golesJugador > golesPorAsignar) golesJugador = golesPorAsignar;
                    golesPorAsignar -= golesJugador;
                }
            }
            est.setGolesAnotados(golesJugador);

            // Asistencias y Tarjetas (Aleatorio simple)
            est.setAsistencias(golesFavor > 0 && random.nextInt(10) > 7 ? 1 : 0);
            est.setTarjetaAmarillas(random.nextInt(10) > 8 ? 1 : 0);
            est.setTarjetaRojas(false);

            // Calcular Puntos Fantasy (Lógica simplificada)
            // Base: 2 pts. Gol: +4. Asistencia: +2. Portería a 0 (si ganó y no recibió goles): +3
            int puntos = 2; // Por jugar
            puntos += (est.getGolesAnotados() * 4);
            puntos += (est.getAsistencias() * 2);
            puntos -= (est.getTarjetaAmarillas());
            if (jugador.isEsPortero() && golesContra == 0) puntos += 3;
            if (golesFavor > golesContra) puntos += 1; // Bonus victoria

            // Variación aleatoria de rendimiento
            puntos += (random.nextInt(5) - 2);

            est.setPuntosJornada(Math.max(0, puntos)); // No negativos para este ejemplo
            estadisticaRepository.save(est);
        }
    }

    private void simularAlineacionesUsuarios(List<Usuario> usuarios, Jornada jornada) {
        // Obtenemos todos los jugadores disponibles en la BBDD para elegir al azar
        List<Jugador> todosJugadores = jugadorRepository.findAll();

        for (Usuario usuario : usuarios) {
            Alineacion alineacion = new Alineacion();
            alineacion.setUsuario(usuario);
            alineacion.setJornada(jornada);

            // Seleccionar 5 jugadores aleatorios diferentes
            Collections.shuffle(todosJugadores);
            List<Jugador> seleccionados = todosJugadores.stream().limit(5).collect(Collectors.toList());

            alineacion.getJugadores().addAll(seleccionados);

            // Calcular puntos basados en las estadísticas ya generadas
            int totalPuntos = 0;
            for (Jugador j : seleccionados) {
                // Buscar la estadística de este jugador en esta jornada (a través del partido)
                // Nota: Esto asume que el jugador jugó en esa jornada. Como simulamos liga completa, sí jugó.
                // En SQL real sería más directo, aquí filtramos en memoria por simplicidad del script
                List<EstadisticaJugadorPartido> stats = estadisticaRepository.findByJugadorIdJugador(j.getIdJugador());
                for (EstadisticaJugadorPartido stat : stats) {
                    if (stat.getPartido().getJornada().getIdJornada().equals(jornada.getIdJornada())) {
                        totalPuntos += stat.getPuntosJornada();
                    }
                }
            }

            alineacion.setPuntosTotalesJornada(totalPuntos);
            alineacionRepository.save(alineacion);
        }
    }

    // ==================== RESULTADOS FINALES ====================

    private void mostrarClasificacion(LigaCume liga) {
        // Actualizar puntos acumulados de usuarios sumando alineaciones
        List<Usuario> usuarios = usuarioRepository.findByLigaIdLigaCumeOrderByPuntosAcumuladosDesc(liga.getIdLigaCume());

        for (Usuario u : usuarios) {
            int suma = 0;
            List<Alineacion> alineaciones = alineacionRepository.findByUsuarioIdUsuario(u.getIdUsuario());
            for (Alineacion al : alineaciones) {
                suma += al.getPuntosTotalesJornada();
            }
            u.setPuntosAcumulados(suma);
            usuarioRepository.save(u);
        }

        // Volver a cargar ordenados
        usuarios = usuarioRepository.findByLigaIdLigaCumeOrderByPuntosAcumuladosDesc(liga.getIdLigaCume());

        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║            🏆 CLASIFICACIÓN TRAS 5 JORNADAS                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");

        int pos = 1;
        for (Usuario u : usuarios) {
            String icono = pos == 1 ? "🥇" : pos == 2 ? "🥈" : pos == 3 ? "🥉" : "  ";
            System.out.printf("%s %d. %-15s | Puntos: %4d%n", icono, pos, u.getNombreUsuario(), u.getPuntosAcumulados());
            pos++;
        }
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println("📊 Datos Generados:");
        System.out.println("   Equipos: " + equipoRepository.count());
        System.out.println("   Jugadores: " + jugadorRepository.count());
        System.out.println("   Jornadas: " + jornadaRepository.count());
        System.out.println("   Partidos: " + partidoRepository.count() + " (15 jugados, 15 pendientes)");
        System.out.println("   Estadísticas: " + estadisticaRepository.count());
    }

    // ==================== HELPERS SIMPLES ====================

    private Usuario crearUsuario(String nombre, String email, LigaCume liga) {
        Usuario u = new Usuario();
        u.setNombreUsuario(nombre);
        u.setEmail(email);
        u.setPassword("pass123");
        u.setLiga(liga);
        u.setPuntosAcumulados(0);
        return usuarioRepository.save(u);
    }

    private Equipo crearEquipo(String nombre, String escudo, LigaCume liga) {
        Equipo e = new Equipo();
        e.setNombreEquipo(nombre);
        e.setEscudoURL(escudo);
        e.setLiga(liga);
        return equipoRepository.save(e);
    }
}