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

        // 3. Crear Equipos Reales de la Liga del Cume con sus Jugadores
        List<Equipo> equipos = crearEquiposReales(liga);

        // 4. Generar Calendario y Simular las primeras 6 jornadas
        generarYSimularJornadas(liga, equipos, usuarios);

        // 5. Calcular Clasificación Final
        mostrarClasificacion(liga);
    }

    // ==================== CREACIÓN DE EQUIPOS Y JUGADORES ====================

    private List<Equipo> crearEquiposReales(LigaCume liga) {
        System.out.println("\n⚽ Creando equipos y jugadores de la Liga del Cume...");
        List<Equipo> listaEquipos = new ArrayList<>();

        // Equipo 1: CUM UNITED
        Equipo cumUnited = crearEquipo("CUM UNITED", "https://laliga-del-cume.web.app/images/Escudos/CUM%20UNITED.png", liga);
        crearJugador(cumUnited, "Miguel Fernández", true, "https://laliga-del-cume.web.app/images/jugadores/cumUnited/miguel.png");
        crearJugador(cumUnited, "Assaad Abbadi", false, "https://laliga-del-cume.web.app/images/jugadores/cumUnited/assaad.png");
        crearJugador(cumUnited, "Eduardo Casquete", false, "https://laliga-del-cume.web.app/images/jugadores/cumUnited/eduardo.png");
        crearJugador(cumUnited, "Néstor Pérez", false, "https://laliga-del-cume.web.app/images/jugadores/cumUnited/nestor.png");
        crearJugador(cumUnited, "Mario Blázquez", false, "https://laliga-del-cume.web.app/images/jugadores/cumUnited/mario.jpg");
        crearJugador(cumUnited, "Luis Ramón Cabezas", false, "https://laliga-del-cume.web.app/images/jugadores/cumUnited/luis3.png");
        crearJugador(cumUnited, "Juan Cabezas", false, "https://laliga-del-cume.web.app/images/jugadores/cumUnited/juan.png");
        crearJugador(cumUnited, "Daniel Durán", false, "https://laliga-del-cume.web.app/images/jugadores/cumUnited/daniel.png");
        crearJugador(cumUnited, "Rodrigo Vicente", false, "https://laliga-del-cume.web.app/images/jugadores/cumUnited/rodri.png");
        crearJugador(cumUnited, "Ernesto Ruiz", false, "https://laliga-del-cume.web.app/images/jugadores/cumUnited/ernesto.png");
        crearJugador(cumUnited, "Juan Carlos Ruiz", false, "https://laliga-del-cume.web.app/images/jugadores/cumUnited/juanki.png");
        listaEquipos.add(cumUnited);

        // Equipo 2: RAYO MARIGUANO
        Equipo rayoMariguano = crearEquipo("RAYO MARIGUANO", "https://laliga-del-cume.web.app/images/Escudos/RAYO%20MARIGUANO.png", liga);
        crearJugador(rayoMariguano, "Carlos Frejido", true, "https://laliga-del-cume.web.app/images/jugadores/rayo/carlos.jpg");
        crearJugador(rayoMariguano, "Alejandro Torrado", false, "https://laliga-del-cume.web.app/images/jugadores/rayo/alejandro.jpg");
        crearJugador(rayoMariguano, "David Álvarez", false, "https://laliga-del-cume.web.app/images/jugadores/rayo/david.jpg");
        crearJugador(rayoMariguano, "Francisco López", false, "https://laliga-del-cume.web.app/images/jugadores/rayo/francisco.jpg");
        crearJugador(rayoMariguano, "Fernando Martínez", false, "https://laliga-del-cume.web.app/images/jugadores/rayo/fernando.jpg");
        crearJugador(rayoMariguano, "Álvaro Luna", false, "https://laliga-del-cume.web.app/images/jugadores/rayo/alvaro.jpg");
        crearJugador(rayoMariguano, "Gonzalo Bermejo", false, "https://laliga-del-cume.web.app/images/jugadores/rayo/gonzalo.jpg");
        crearJugador(rayoMariguano, "José María Ibáñez", false, "https://laliga-del-cume.web.app/images/jugadores/rayo/josemaria.jpg");
        crearJugador(rayoMariguano, "Francisco González", false, "https://laliga-del-cume.web.app/images/jugadores/rayo/francisco2.jpg");
        crearJugador(rayoMariguano, "Juan Ibáñez", false, "https://laliga-del-cume.web.app/images/jugadores/rayo/juan.jpg");
        crearJugador(rayoMariguano, "Lucía Cantonero", false, "https://laliga-del-cume.web.app/images/jugadores/rayo/lucia.jpg");
        listaEquipos.add(rayoMariguano);

        // Equipo 3: ATLÉTICO MORANTE
        Equipo atleticoMorante = crearEquipo("ATLÉTICO MORANTE", "https://laliga-del-cume.web.app/images/Escudos/ATL%C3%89TICO%20MORANTE.png", liga);
        crearJugador(atleticoMorante, "Manuel Mestre", true, "https://laliga-del-cume.web.app/images/jugadores/morante/manuel.jpg");
        crearJugador(atleticoMorante, "Carlos Fuentes", false, "https://laliga-del-cume.web.app/images/jugadores/morante/carlos.jpg");
        crearJugador(atleticoMorante, "Daniel Gallego", false, "https://laliga-del-cume.web.app/images/jugadores/morante/daniel.jpg");
        crearJugador(atleticoMorante, "Javier Fernández", false, "https://laliga-del-cume.web.app/images/jugadores/morante/javier.jpg");
        crearJugador(atleticoMorante, "Álvaro Fuentes", false, "https://laliga-del-cume.web.app/images/jugadores/morante/alvaro.jpg");
        crearJugador(atleticoMorante, "Gonzalo García", false, "https://laliga-del-cume.web.app/images/jugadores/morante/gonzalo.jpg");
        crearJugador(atleticoMorante, "Vicente Collado", false, "https://laliga-del-cume.web.app/images/jugadores/morante/vicente.jpg");
        crearJugador(atleticoMorante, "Luis Miguel Lavado", false, "https://laliga-del-cume.web.app/images/jugadores/morante/luismi.jpg");
        crearJugador(atleticoMorante, "Javier Herrero", false, "https://laliga-del-cume.web.app/images/jugadores/morante/javier2.jpg");
        crearJugador(atleticoMorante, "Pedro José Padilla", false, "https://laliga-del-cume.web.app/images/jugadores/morante/pedro.jpg");
        listaEquipos.add(atleticoMorante);

        // Equipo 4: ASTON BIRRA
        Equipo astonBirra = crearEquipo("ASTON BIRRA", "https://laliga-del-cume.web.app/images/Escudos/ASTON%20BIRRA.png", liga);
        crearJugador(astonBirra, "Víctor Pérez", true, "https://laliga-del-cume.web.app/images/jugadores/aston/victor.jpg");
        crearJugador(astonBirra, "Izan Campanón", false, "https://laliga-del-cume.web.app/images/jugadores/aston/izan.jpg");
        crearJugador(astonBirra, "Iván Rodríguez", false, "https://laliga-del-cume.web.app/images/jugadores/aston/ivan.jpg");
        crearJugador(astonBirra, "Isidro Capitán", false, "https://laliga-del-cume.web.app/images/jugadores/aston/isidro.jpg");
        crearJugador(astonBirra, "David Gómez", false, "https://laliga-del-cume.web.app/images/jugadores/aston/david.jpg");
        crearJugador(astonBirra, "Carlos Borja", false, "https://laliga-del-cume.web.app/images/jugadores/aston/carlos.jpg");
        crearJugador(astonBirra, "Antonio Carrasco", false, "https://laliga-del-cume.web.app/images/jugadores/aston/antonio.jpg");
        crearJugador(astonBirra, "Javier Conejero", false, "https://laliga-del-cume.web.app/images/jugadores/aston/javi.jpg");
        crearJugador(astonBirra, "Antonio González", false, "https://laliga-del-cume.web.app/images/jugadores/aston/antonio2.jpg");
        crearJugador(astonBirra, "Rubén Garrido", false, "https://laliga-del-cume.web.app/images/jugadores/aston/ruben.jpg");
        crearJugador(astonBirra, "Álvaro Clavero", false, "https://laliga-del-cume.web.app/images/jugadores/aston/alvaro.jpg");
        crearJugador(astonBirra, "Pablo Mimbrero", false, "https://laliga-del-cume.web.app/images/jugadores/aston/pablo.jpg");
        listaEquipos.add(astonBirra);

        // Equipo 5: I.E.SALA
        Equipo ieSala = crearEquipo("I.E.SALA", "https://laliga-del-cume.web.app/images/Escudos/I.E.%20SALA.png", liga);
        crearJugador(ieSala, "Fabio Sánchez", true, "https://laliga-del-cume.web.app/images/jugadores/iesala/fabio.png");
        crearJugador(ieSala, "Ángel Carapeto", false, "https://laliga-del-cume.web.app/images/jugadores/iesala/angel.png");
        crearJugador(ieSala, "Marcos Ocaña", false, "https://laliga-del-cume.web.app/images/jugadores/iesala/marcos.jpg");
        crearJugador(ieSala, "Juan Luís Rodríguez", false, "https://laliga-del-cume.web.app/images/jugadores/iesala/juanluis.png");
        crearJugador(ieSala, "David Fernández", false, "https://laliga-del-cume.web.app/images/jugadores/iesala/david.png");
        crearJugador(ieSala, "Víctor Álvarez", false, "https://laliga-del-cume.web.app/images/jugadores/iesala/victor.jpg");
        crearJugador(ieSala, "Aarón Molina", false, "https://laliga-del-cume.web.app/images/jugadores/iesala/aaron.png");
        listaEquipos.add(ieSala);

        // Equipo 6: CUM CITY
        Equipo cumCity = crearEquipo("CUM CITY", "https://laliga-del-cume.web.app/images/Escudos/CUM%20CITY.png", liga);
        crearJugador(cumCity, "Carlos Zambrano", true, "https://laliga-del-cume.web.app/images/jugadores/cumCity/carlos.jpg");
        crearJugador(cumCity, "David Cáceres", false, "https://laliga-del-cume.web.app/images/jugadores/cumCity/david.png");
        crearJugador(cumCity, "Andrés Pajuelo", false, "https://laliga-del-cume.web.app/images/jugadores/cumCity/andres.png");
        crearJugador(cumCity, "Justo Manuel Sánchez", false, "https://laliga-del-cume.web.app/images/jugadores/cumCity/justo.png");
        crearJugador(cumCity, "Alberto Durán", false, "https://laliga-del-cume.web.app/images/jugadores/cumCity/alberto.jpg");
        crearJugador(cumCity, "Juan Prieto Da Silva", false, "https://laliga-del-cume.web.app/images/jugadores/cumCity/pietro.jpg");
        crearJugador(cumCity, "Juan Luis Fernández", false, "https://laliga-del-cume.web.app/images/jugadores/cumCity/juanlu.jpg");
        crearJugador(cumCity, "Luis Miguel Márquez", false, "https://laliga-del-cume.web.app/images/jugadores/cumCity/luismi.jpg");
        crearJugador(cumCity, "Ciro Benjamín Castañeda", false, "https://laliga-del-cume.web.app/images/jugadores/cumCity/ciro.jpg");
        listaEquipos.add(cumCity);

        // Equipo 7: UNIÓN DEPORTIVA PORRETA
        Equipo udPorteta = crearEquipo("UNIÓN DEPORTIVA PORRETA", "https://laliga-del-cume.web.app/images/Escudos/UNI%C3%93N%20DEPORTIVA%20PORRETA.png", liga);
        crearJugador(udPorteta, "José María Cabezas", true, "https://laliga-del-cume.web.app/images/jugadores/udp/josemaria.jpg");
        crearJugador(udPorteta, "Álvaro Gobernado", false, "https://laliga-del-cume.web.app/images/jugadores/udp/alvaro.jpg");
        crearJugador(udPorteta, "Alberto Salazar", false, "https://laliga-del-cume.web.app/images/jugadores/udp/alberto.jpg");
        crearJugador(udPorteta, "Hugo Segovia", false, "https://laliga-del-cume.web.app/images/jugadores/udp/hugo.jpg");
        crearJugador(udPorteta, "Asier Mariño", false, "https://laliga-del-cume.web.app/images/jugadores/udp/asier.jpg");
        crearJugador(udPorteta, "Victor Santamaría", false, "https://laliga-del-cume.web.app/images/jugadores/udp/victor.jpg");
        crearJugador(udPorteta, "Álex Cancho", false, "https://laliga-del-cume.web.app/images/jugadores/udp/alex.jpg");
        crearJugador(udPorteta, "Ángel Moreno", false, "https://laliga-del-cume.web.app/images/jugadores/udp/angel.jpg");
        crearJugador(udPorteta, "Francisco José Castro", false, "https://laliga-del-cume.web.app/images/jugadores/udp/fran.jpg");
        crearJugador(udPorteta, "Francisco Javier Carretero", false, "https://laliga-del-cume.web.app/images/jugadores/udp/fcojavier.jpg");
        listaEquipos.add(udPorteta);

        System.out.println("✓ " + listaEquipos.size() + " equipos creados con sus jugadores");
        return listaEquipos;
    }

    private void crearJugador(Equipo equipo, String nombre, boolean esPortero, String avatarUrl) {
        Jugador j = new Jugador();
        j.setNombreJugador(nombre);
        j.setEsPortero(esPortero);
        j.setEquipo(equipo);
        j.setPrecioMercado(100000); // Todos empiezan con 100.000
        j.setAvatarUrl(avatarUrl);
        jugadorRepository.save(j);
    }

    // ==================== LÓGICA DE JORNADAS Y SIMULACIÓN ====================

    private void generarYSimularJornadas(LigaCume liga, List<Equipo> equipos, List<Usuario> usuarios) {
        // Algoritmo Round Robin para 7 equipos (número impar - uno descansa cada jornada)
        int numEquipos = equipos.size(); // 7
        int numJornadasIda = numEquipos; // 7 (uno descansa cada jornada)

        // Solo simulamos 6 jornadas como se solicitó
        int jornadasASimular = 6;

        // Copia para rotar
        List<Equipo> equiposRotacion = new ArrayList<>(equipos);

        System.out.println("\n📅 Generando " + jornadasASimular + " jornadas...");

        for (int dia = 0; dia < jornadasASimular; dia++) {
            int numeroJornada = dia + 1;
            Jornada jornada = new Jornada();
            jornada.setLiga(liga);
            jornada.setNumeroJornada(numeroJornada);
            jornada = jornadaRepository.save(jornada);

            System.out.println("  ➜ Jornada " + numeroJornada + " (Simulada)");

            // Con 7 equipos, uno descansa cada jornada (el primero de la lista rotativa)
            // Se forman 3 partidos
            for (int i = 1; i < numEquipos; i += 2) {
                if (i + 1 < numEquipos) {
                    Equipo local = equiposRotacion.get(i);
                    Equipo visitante = equiposRotacion.get(i + 1);

                    procesarPartido(jornada, local, visitante, true);
                }
            }

            // Simular alineaciones de usuarios
            simularAlineacionesUsuarios(usuarios, jornada);

            // Rotar equipos para la siguiente jornada
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
        System.out.println("║            🏆 CLASIFICACIÓN TRAS 6 JORNADAS                   ║");
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
        System.out.println("   Partidos: " + partidoRepository.count() + " (jugados en 6 jornadas)");
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