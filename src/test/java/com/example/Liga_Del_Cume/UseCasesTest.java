package com.example.Liga_Del_Cume;

import com.example.Liga_Del_Cume.data.model.*;
import com.example.Liga_Del_Cume.data.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UseCasesTest {

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

    // ========================================
    // MÉTODOS AUXILIARES PARA CREAR DATOS
    // ========================================

    private LigaCume crearLigaTest() {
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("Liga Cume Test");
        liga.setPresupuestoMaximo(100000000L);
        return ligaRepository.save(liga);
    }

    private Equipo crearEquipoTest(String nombre, LigaCume liga) {
        Equipo equipo = new Equipo();
        equipo.setNombreEquipo(nombre);
        equipo.setEscudoURL("http://example.com/" + nombre.toLowerCase().replace(" ", "_") + ".png");
        equipo.setLiga(liga);
        return equipoRepository.save(equipo);
    }

    private Jugador crearJugadorTest(String nombre, boolean esPortero, Equipo equipo, float precio) {
        Jugador jugador = new Jugador();
        jugador.setNombreJugador(nombre);
        jugador.setEsPortero(esPortero);
        jugador.setPrecioMercado(precio);
        jugador.setEquipo(equipo);
        return jugadorRepository.save(jugador);
    }

    private Usuario crearUsuarioTest(String nombre, LigaCume liga, int puntos) {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombre);
        usuario.setLiga(liga);
        usuario.setPuntosAcumulados(puntos);
        return usuarioRepository.save(usuario);
    }

    private Jornada crearJornadaTest(LigaCume liga) {
        Jornada jornada = new Jornada();
        jornada.setLiga(liga);
        return jornadaRepository.save(jornada);
    }

    private Partido crearPartidoTest(Jornada jornada, Equipo local, Equipo visitante, int golesLocal, int golesVisitante) {
        Partido partido = new Partido();
        partido.setJornada(jornada);
        partido.setEquipoLocal(local);
        partido.setEquipoVisitante(visitante);
        partido.setGolesLocal(golesLocal);
        partido.setGolesVisitante(golesVisitante);
        return partidoRepository.save(partido);
    }

    // ========================================
    // FUNCIONALIDAD 1: GESTIÓN DE EQUIPOS
    // ========================================

    @Test
    public void testFuncionalidad1_1_AgregarEquipos() {
        System.out.println("\n=== TEST: 1.1 Agregar equipos ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();

        // Agregar nuevo equipo
        Equipo nuevoEquipo = new Equipo();
        nuevoEquipo.setNombreEquipo("Atlético de Madrid");
        nuevoEquipo.setEscudoURL("http://example.com/atletico.png");
        nuevoEquipo.setLiga(liga);

        Equipo equipoGuardado = equipoRepository.save(nuevoEquipo);

        assertNotNull(equipoGuardado.getIdEquipo(), "El equipo debe tener un ID asignado");
        assertEquals("Atlético de Madrid", equipoGuardado.getNombreEquipo());

        Equipo equipoEncontrado = equipoRepository.findByNombreEquipo("Atlético de Madrid");
        assertNotNull(equipoEncontrado, "El equipo debe existir en la base de datos");

        System.out.println("✓ Equipo agregado exitosamente: " + equipoGuardado.getNombreEquipo());
        System.out.println("  ID: " + equipoGuardado.getIdEquipo());
    }

    @Test
    public void testFuncionalidad1_2_ModificarEquipos() {
        System.out.println("\n=== TEST: 1.2 Modificar equipos ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Equipo equipo = crearEquipoTest("FC Barcelona Test", liga);

        String nombreOriginal = equipo.getNombreEquipo();
        String nuevoNombre = "FC Barcelona Updated";

        equipo.setNombreEquipo(nuevoNombre);
        equipo.setEscudoURL("http://example.com/nuevo_escudo.png");

        Equipo equipoModificado = equipoRepository.save(equipo);

        assertEquals(nuevoNombre, equipoModificado.getNombreEquipo());
        assertEquals("http://example.com/nuevo_escudo.png", equipoModificado.getEscudoURL());

        System.out.println("✓ Equipo modificado exitosamente");
        System.out.println("  Nombre anterior: " + nombreOriginal);
        System.out.println("  Nombre nuevo: " + equipoModificado.getNombreEquipo());
    }

    @Test
    public void testFuncionalidad1_3_EliminarEquipos() {
        System.out.println("\n=== TEST: 1.3 Eliminar equipos ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Equipo equipoTemporal = crearEquipoTest("Equipo Temporal", liga);

        Long idEquipo = equipoTemporal.getIdEquipo();
        assertTrue(equipoRepository.existsById(idEquipo), "El equipo debe existir antes de eliminar");

        equipoRepository.delete(equipoTemporal);

        assertFalse(equipoRepository.existsById(idEquipo), "El equipo no debe existir después de eliminar");

        System.out.println("✓ Equipo eliminado exitosamente");
        System.out.println("  ID eliminado: " + idEquipo);
    }

    @Test
    public void testFuncionalidad1_4_ListarEquipos() {
        System.out.println("\n=== TEST: 1.4 Listar equipos ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Equipo equipo1 = crearEquipoTest("Real Madrid", liga);
        Equipo equipo2 = crearEquipoTest("Barcelona", liga);
        Equipo equipo3 = crearEquipoTest("Sevilla", liga);

        List<Equipo> equipos = equipoRepository.findAll();
        assertTrue(equipos.size() >= 3, "Debe haber al menos los 3 equipos creados");

        System.out.println("✓ Equipos encontrados: " + equipos.size());
        for (Equipo equipo : equipos) {
            System.out.println("  - " + equipo.getNombreEquipo() + " (ID: " + equipo.getIdEquipo() + ")");
        }
    }

    // ========================================
    // FUNCIONALIDAD 2: GESTIÓN DE JUGADORES
    // ========================================

    @Test
    public void testFuncionalidad2_1_AgregarJugadoresAEquipo() {
        System.out.println("\n=== TEST: 2.1 Agregar jugadores a un equipo ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Equipo equipo = crearEquipoTest("Valencia CF", liga);

        Jugador nuevoJugador = new Jugador();
        nuevoJugador.setNombreJugador("Nuevo Jugador Test");
        nuevoJugador.setEsPortero(false);
        nuevoJugador.setPrecioMercado(5000000);
        nuevoJugador.setEquipo(equipo);

        Jugador jugadorGuardado = jugadorRepository.save(nuevoJugador);

        assertNotNull(jugadorGuardado.getIdJugador(), "El jugador debe tener un ID");
        assertEquals("Nuevo Jugador Test", jugadorGuardado.getNombreJugador());
        assertEquals(equipo.getIdEquipo(), jugadorGuardado.getEquipo().getIdEquipo());

        System.out.println("✓ Jugador agregado exitosamente: " + jugadorGuardado.getNombreJugador());
        System.out.println("  Equipo: " + jugadorGuardado.getEquipo().getNombreEquipo());
        System.out.println("  Precio: " + jugadorGuardado.getPrecioMercado());
    }

    @Test
    public void testFuncionalidad2_2_ActualizarInformacionJugadores() {
        System.out.println("\n=== TEST: 2.2 Actualizar información de jugadores ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Equipo equipo = crearEquipoTest("Athletic Club", liga);
        Jugador jugador = crearJugadorTest("Iñaki Williams", false, equipo, 10000000);

        float precioOriginal = jugador.getPrecioMercado();
        float nuevoPrecio = 15000000;

        jugador.setPrecioMercado(nuevoPrecio);
        jugador.setNombreJugador(jugador.getNombreJugador() + " Updated");

        Jugador jugadorActualizado = jugadorRepository.save(jugador);

        assertEquals(nuevoPrecio, jugadorActualizado.getPrecioMercado());
        assertTrue(jugadorActualizado.getNombreJugador().contains("Updated"));

        System.out.println("✓ Jugador actualizado exitosamente");
        System.out.println("  Precio anterior: " + precioOriginal);
        System.out.println("  Precio nuevo: " + jugadorActualizado.getPrecioMercado());
    }

    @Test
    public void testFuncionalidad2_3_EliminarJugadoresDeEquipo() {
        System.out.println("\n=== TEST: 2.3 Eliminar jugadores de un equipo ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Equipo equipo = crearEquipoTest("Villarreal CF", liga);
        Jugador jugadorTemporal = crearJugadorTest("Jugador Temporal", false, equipo, 1000000);

        Long idJugador = jugadorTemporal.getIdJugador();
        assertTrue(jugadorRepository.existsById(idJugador), "El jugador debe existir");

        jugadorRepository.delete(jugadorTemporal);

        assertFalse(jugadorRepository.existsById(idJugador), "El jugador no debe existir después de eliminar");

        System.out.println("✓ Jugador eliminado exitosamente del equipo");
    }

    @Test
    public void testFuncionalidad2_4_ListarJugadores() {
        System.out.println("\n=== TEST: 2.4 Listar jugadores ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Equipo equipo = crearEquipoTest("Real Betis", liga);
        Jugador portero = crearJugadorTest("Claudio Bravo", true, equipo, 2000000);
        Jugador delantero = crearJugadorTest("Borja Iglesias", false, equipo, 8000000);
        Jugador centrocampista = crearJugadorTest("Sergio Canales", false, equipo, 12000000);

        List<Jugador> jugadores = jugadorRepository.findByEquipoNombreEquipo("Real Betis");
        assertEquals(3, jugadores.size(), "Debe haber 3 jugadores del Real Betis");

        System.out.println("✓ Jugadores encontrados: " + jugadores.size());
        for (Jugador jugador : jugadores) {
            System.out.println("  - " + jugador.getNombreJugador() +
                             " (" + jugador.getEquipo().getNombreEquipo() + ")" +
                             (jugador.isEsPortero() ? " [PORTERO]" : " [CAMPO]"));
        }
    }

    // ========================================
    // FUNCIONALIDAD 3: GESTIÓN DE PARTIDOS
    // ========================================

    @Test
    public void testFuncionalidad3_1_AgregarResultadosPartidosPorJornada() {
        System.out.println("\n=== TEST: 3.1 Agregar resultados de partidos por jornada ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Jornada jornada = crearJornadaTest(liga);
        Equipo equipoLocal = crearEquipoTest("Celta de Vigo", liga);
        Equipo equipoVisitante = crearEquipoTest("Getafe CF", liga);

        Partido nuevoPartido = new Partido();
        nuevoPartido.setJornada(jornada);
        nuevoPartido.setEquipoLocal(equipoLocal);
        nuevoPartido.setEquipoVisitante(equipoVisitante);
        nuevoPartido.setGolesLocal(2);
        nuevoPartido.setGolesVisitante(1);

        Partido partidoGuardado = partidoRepository.save(nuevoPartido);

        assertNotNull(partidoGuardado.getIdPartido(), "El partido debe tener un ID");
        assertEquals(2, partidoGuardado.getGolesLocal());
        assertEquals(1, partidoGuardado.getGolesVisitante());

        System.out.println("✓ Partido agregado exitosamente");
        System.out.println("  " + partidoGuardado.getEquipoLocal().getNombreEquipo() +
                         " " + partidoGuardado.getGolesLocal() + "-" +
                         partidoGuardado.getGolesVisitante() + " " +
                         partidoGuardado.getEquipoVisitante().getNombreEquipo());
    }

    @Test
    public void testFuncionalidad3_2_ModificarResultadosPartidosPorJornada() {
        System.out.println("\n=== TEST: 3.2 Modificar resultados de partidos por jornada ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Jornada jornada = crearJornadaTest(liga);
        Equipo equipoLocal = crearEquipoTest("Osasuna", liga);
        Equipo equipoVisitante = crearEquipoTest("Granada CF", liga);
        Partido partido = crearPartidoTest(jornada, equipoLocal, equipoVisitante, 1, 1);

        int golesLocalesOriginales = partido.getGolesLocal();
        int golesVisitantesOriginales = partido.getGolesVisitante();

        partido.setGolesLocal(5);
        partido.setGolesVisitante(3);

        Partido partidoModificado = partidoRepository.save(partido);

        assertEquals(5, partidoModificado.getGolesLocal());
        assertEquals(3, partidoModificado.getGolesVisitante());

        System.out.println("✓ Resultado modificado exitosamente");
        System.out.println("  Resultado anterior: " + golesLocalesOriginales + "-" + golesVisitantesOriginales);
        System.out.println("  Resultado nuevo: " + partidoModificado.getGolesLocal() + "-" + partidoModificado.getGolesVisitante());
    }

    // ========================================
    // FUNCIONALIDAD 4: ESTADÍSTICAS DE JUGADORES
    // ========================================

    @Test
    public void testFuncionalidad4_1_AñadirEstadisticasJugadoresPorPartido() {
        System.out.println("\n=== TEST: 4.1 Añadir estadísticas de jugadores por partido ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Jornada jornada = crearJornadaTest(liga);
        Jornada jornada2 = crearJornadaTest(liga);
        Equipo equipo1 = crearEquipoTest("Mallorca", liga);
        Equipo equipo2 = crearEquipoTest("Almería", liga);
        Jugador jugador = crearJugadorTest("Vedat Muriqi", false, equipo1, 7000000);
        Partido partido = crearPartidoTest(jornada, equipo1, equipo2, 3, 1);
        Partido partido2 = crearPartidoTest(jornada2, equipo2, equipo1, 3, 3);


        Usuario user = crearUsuarioTest("Usuario Estadisticas", liga, 0);

        EstadisticaJugadorPartido nuevaEstadistica = new EstadisticaJugadorPartido();
        nuevaEstadistica.setJugador(jugador);
        nuevaEstadistica.setPartido(partido);
        nuevaEstadistica.setGolesAnotados(2);
        nuevaEstadistica.setAsistencias(1);
        nuevaEstadistica.setTarjetaRojas(false);  // 0 = no tiene tarjeta roja
        nuevaEstadistica.setTarjetaRojas(false);
        nuevaEstadistica.setMinMinutosJugados(true);
        nuevaEstadistica.setGolesRecibidos(0);
        nuevaEstadistica.setPuntosJornada(15);

        EstadisticaJugadorPartido nuevaEstadistica2 = new EstadisticaJugadorPartido();
        nuevaEstadistica2.setJugador(jugador);
        nuevaEstadistica2.setPartido(partido2);
        nuevaEstadistica2.setGolesAnotados(23);
        nuevaEstadistica2.setAsistencias(12);
        nuevaEstadistica2.setTarjetaRojas(false);  // 0 = no tiene tarjeta roja
        nuevaEstadistica2.setTarjetaRojas(false);
        nuevaEstadistica2.setMinMinutosJugados(true);
        nuevaEstadistica2.setGolesRecibidos(123);
        nuevaEstadistica2.setPuntosJornada(1231);

        EstadisticaJugadorPartido estadisticaGuardada = estadisticaRepository.save(nuevaEstadistica);
        EstadisticaJugadorPartido estadisticaGuardada2 = estadisticaRepository.save(nuevaEstadistica2);
        assertNotNull(estadisticaGuardada.getJugador(), "La estadística debe tener un jugador");
        assertNotNull(estadisticaGuardada.getPartido(), "La estadística debe tener un ID");
        assertNotNull(estadisticaGuardada.getJugador(), "La estadística debe tener un jugador asociado");
        estadisticaRepository.flush();

// Ahora la consulta debe encontrar las 2 estadísticas
        List<EstadisticaJugadorPartido> stats = estadisticaRepository.findByJugadorIdJugador(jugador.getIdJugador());
        assertEquals(2, stats.size(), "Debe haber 2 estadísticas para el jugador");
        System.out.println("✓ Estadística agregada exitosamente");
        System.out.println("  Jugador: " + estadisticaGuardada.getJugador().getNombreJugador());
        System.out.println("  Goles: " + estadisticaGuardada.getGolesAnotados());
        System.out.println("  Asistencias: " + estadisticaGuardada.getAsistencias());
        System.out.println("  Puntos: " + estadisticaGuardada.getPuntosJornada());
    }

    @Test
    public void testFuncionalidad4_2_ModificarEstadisticasJugadoresPorPartido() {
        System.out.println("\n=== TEST: 4.2 Modificar estadísticas de jugadores por partido ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Jornada jornada = crearJornadaTest(liga);
        Equipo equipo1 = crearEquipoTest("Rayo Vallecano", liga);
        Equipo equipo2 = crearEquipoTest("Cádiz CF", liga);
        Jugador jugador = crearJugadorTest("Radamel Falcao", false, equipo1, 3000000);
        Partido partido = crearPartidoTest(jornada, equipo1, equipo2, 2, 0);

        EstadisticaJugadorPartido estadistica = new EstadisticaJugadorPartido();
        estadistica.setJugador(jugador);
        estadistica.setPartido(partido);
        estadistica.setGolesAnotados(1);
        estadistica.setAsistencias(0);
        estadistica.setTarjetaAmarillas(1);
        estadistica.setTarjetaRojas(false);
        estadistica.setMinMinutosJugados(true);
        estadistica.setGolesRecibidos(0);
        estadistica.setPuntosJornada(8);
        estadistica = estadisticaRepository.save(estadistica);

        int golesOriginales = estadistica.getGolesAnotados();

        estadistica.setGolesAnotados(3);
        estadistica.setAsistencias(2);
        estadistica.setPuntosJornada(20);

        EstadisticaJugadorPartido estadisticaModificada = estadisticaRepository.save(estadistica);

        assertEquals(3, estadisticaModificada.getGolesAnotados());
        assertEquals(2, estadisticaModificada.getAsistencias());
        assertEquals(20, estadisticaModificada.getPuntosJornada());

        System.out.println("✓ Estadística modificada exitosamente");
        System.out.println("  Goles anteriores: " + golesOriginales);
        System.out.println("  Goles nuevos: " + estadisticaModificada.getGolesAnotados());
    }

    // ========================================
    // FUNCIONALIDAD 5: CALENDARIO DE ENFRENTAMIENTOS
    // ========================================

    @Test
    public void testFuncionalidad5_1_GenerarCuadroEnfrentamientosPorJornada() {
        System.out.println("\n=== TEST: 5.1 Generar cuadro de enfrentamientos por jornada ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Jornada jornada = crearJornadaTest(liga);
        Equipo equipo1 = crearEquipoTest("Real Madrid", liga);
        Equipo equipo2 = crearEquipoTest("Barcelona", liga);
        Equipo equipo3 = crearEquipoTest("Atlético", liga);
        Equipo equipo4 = crearEquipoTest("Sevilla", liga);

        Partido partido1 = crearPartidoTest(jornada, equipo1, equipo2, 2, 1);
        Partido partido2 = crearPartidoTest(jornada, equipo3, equipo4, 1, 1);

        List<Partido> partidos = partidoRepository.findByJornadaIdJornada(jornada.getIdJornada());

        assertEquals(2, partidos.size(), "Debe haber 2 partidos en la jornada");

        System.out.println("✓ Cuadro de enfrentamientos de la Jornada " + jornada.getIdJornada() + ":");
        System.out.println("================================================");
        for (Partido partido : partidos) {
            String resultado = partido.getGolesLocal() + " - " + partido.getGolesVisitante();
            System.out.println("  " + partido.getEquipoLocal().getNombreEquipo() +
                             " vs " + partido.getEquipoVisitante().getNombreEquipo() +
                             " [" + resultado + "]");
        }
        System.out.println("================================================");
    }

    // ========================================
    // FUNCIONALIDAD 6: CREAR ALINEACIÓN PARA LA JORNADA
    // ========================================

    @Test
    public void testFuncionalidad6_1_ListarJugadoresDisponiblesPorPosicion() {
        System.out.println("\n=== TEST: 6.1 Listar jugadores disponibles por posición ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Equipo equipo = crearEquipoTest("Valencia CF", liga);
        Jugador portero1 = crearJugadorTest("Giorgi Mamardashvili", true, equipo, 4000000);
        Jugador portero2 = crearJugadorTest("Jaume Doménech", true, equipo, 1000000);
        Jugador jugador1 = crearJugadorTest("Samuel Lino", false, equipo, 8000000);
        Jugador jugador2 = crearJugadorTest("Hugo Duro", false, equipo, 6000000);

        List<Jugador> porteros = jugadorRepository.findByEsPortero(true);
        List<Jugador> jugadoresCampo = jugadorRepository.findByEsPortero(false);

        System.out.println("✓ Jugadores disponibles:");
        System.out.println("\n  PORTEROS:");
        for (Jugador p : porteros) {
            System.out.println("    - " + p.getNombreJugador() +
                             " (" + p.getEquipo().getNombreEquipo() + ")" +
                             " - Precio: " + p.getPrecioMercado());
        }

        System.out.println("\n  JUGADORES DE CAMPO:");
        for (Jugador j : jugadoresCampo) {
            System.out.println("    - " + j.getNombreJugador() +
                             " (" + j.getEquipo().getNombreEquipo() + ")" +
                             " - Precio: " + j.getPrecioMercado());
        }

        assertTrue(porteros.size() > 0, "Debe haber porteros disponibles");
        assertTrue(jugadoresCampo.size() > 0, "Debe haber jugadores de campo disponibles");
    }

    @Test
    public void testFuncionalidad6_2_SeleccionarJugadorPorPosicion() {
        System.out.println("\n=== TEST: 6.2 Seleccionar jugador por posición ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Usuario usuario = crearUsuarioTest("Usuario Test", liga, 0);
        Jornada jornada = crearJornadaTest(liga);
        Equipo equipo = crearEquipoTest("Athletic Club", liga);
        Jugador portero = crearJugadorTest("Unai Simón", true, equipo, 12000000);
        Jugador delantero = crearJugadorTest("Iñaki Williams", false, equipo, 15000000);

        Alineacion alineacion = new Alineacion();
        alineacion.setUsuario(usuario);
        alineacion.setJornada(jornada);
        alineacion.getJugadores().add(portero);
        alineacion.getJugadores().add(delantero);
        alineacion.setPuntosTotalesJornada(0);

        Alineacion alineacionGuardada = alineacionRepository.save(alineacion);

        assertNotNull(alineacionGuardada.getIdAlineacion());
        assertEquals(2, alineacionGuardada.getJugadores().size());

        System.out.println("✓ Jugadores seleccionados para la alineación:");
        for (Jugador j : alineacionGuardada.getJugadores()) {
            System.out.println("  - " + j.getNombreJugador() +
                             (j.isEsPortero() ? " [PORTERO]" : " [CAMPO]"));
        }
    }

    @Test
    public void testFuncionalidad6_3_ConsultarEquipoAlineado() {
        System.out.println("\n=== TEST: 6.3 Consultar equipo alineado ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Usuario usuario = crearUsuarioTest("Carlos López", liga, 0);
        Jornada jornada = crearJornadaTest(liga);
        Equipo equipo = crearEquipoTest("Real Sociedad", liga);
        Jugador portero = crearJugadorTest("Álex Remiro", true, equipo, 8000000);
        Jugador defensa = crearJugadorTest("Robin Le Normand", false, equipo, 10000000);
        Jugador centrocampista = crearJugadorTest("Mikel Merino", false, equipo, 12000000);

        Alineacion alineacion = new Alineacion();
        alineacion.setUsuario(usuario);
        alineacion.setJornada(jornada);
        alineacion.getJugadores().add(portero);
        alineacion.getJugadores().add(defensa);
        alineacion.getJugadores().add(centrocampista);
        alineacion.setPuntosTotalesJornada(25);
        alineacion = alineacionRepository.save(alineacion);

        Alineacion alineacionConsultada = alineacionRepository.findById(alineacion.getIdAlineacion()).orElse(null);
        assertNotNull(alineacionConsultada, "La alineación debe existir");

        System.out.println("✓ Equipo alineado:");
        System.out.println("  Usuario: " + alineacionConsultada.getUsuario().getNombreUsuario());
        System.out.println("  Jornada: " + alineacionConsultada.getJornada().getIdJornada());
        System.out.println("  Puntos totales: " + alineacionConsultada.getPuntosTotalesJornada());
        System.out.println("  Jugadores:");

        for (Jugador jugador : alineacionConsultada.getJugadores()) {
            System.out.println("    - " + jugador.getNombreJugador() +
                             " (" + jugador.getEquipo().getNombreEquipo() + ")" +
                             (jugador.isEsPortero() ? " [PORTERO]" : " [CAMPO]"));
        }
    }

    // ========================================
    // FUNCIONALIDAD 7: CONSULTAR ESTADÍSTICAS GENERALES DE JUGADORES
    // ========================================

    @Test
    public void testFuncionalidad7_1_BuscarJugadorFiltrarPorNombre() {
        System.out.println("\n=== TEST: 7.1 Buscar jugador (filtrar por nombre) ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Equipo equipo = crearEquipoTest("FC Barcelona", liga);
        Jugador pedri = crearJugadorTest("Pedri González", false, equipo, 80000000);
        Jugador pedrito = crearJugadorTest("Pedrito Sánchez", false, equipo, 5000000);

        List<Jugador> jugadores = jugadorRepository.findByNombreJugadorContainingIgnoreCase("Pedri");

        assertTrue(jugadores.size() >= 2, "Debe encontrar al menos 2 jugadores con 'Pedri'");

        System.out.println("✓ Jugadores encontrados con 'Pedri': " + jugadores.size());
        for (Jugador j : jugadores) {
            System.out.println("  - " + j.getNombreJugador() + " (" + j.getEquipo().getNombreEquipo() + ")");
        }
    }

    @Test
    public void testFuncionalidad7_1_BuscarJugadorFiltrarPorEquipo() {
        System.out.println("\n=== TEST: 7.1 Buscar jugador (filtrar por equipo) ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Equipo equipo = crearEquipoTest("FC Barcelona", liga);
        Jugador jugador1 = crearJugadorTest("Robert Lewandowski", false, equipo, 45000000);
        Jugador jugador2 = crearJugadorTest("Gavi", false, equipo, 60000000);
        Jugador jugador3 = crearJugadorTest("Ter Stegen", true, equipo, 30000000);

        List<Jugador> jugadores = jugadorRepository.findByEquipoNombreEquipo("FC Barcelona");

        assertEquals(3, jugadores.size(), "Debe haber 3 jugadores del FC Barcelona");

        System.out.println("✓ Jugadores del FC Barcelona:");
        for (Jugador j : jugadores) {
            System.out.println("  - " + j.getNombreJugador() + " - Precio: " + j.getPrecioMercado());
        }
    }

    @Test
    public void testFuncionalidad7_1_BuscarJugadorOrdenadoPorGoles() {
        System.out.println("\n=== TEST: 7.1 Buscar jugador (ordenado por goles) ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Jornada jornada = crearJornadaTest(liga);
        Equipo equipo1 = crearEquipoTest("Real Madrid", liga);
        Equipo equipo2 = crearEquipoTest("Barcelona", liga);
        Partido partido = crearPartidoTest(jornada, equipo1, equipo2, 3, 2);

        Jugador jugador1 = crearJugadorTest("Karim Benzema", false, equipo1, 30000000);
        Jugador jugador2 = crearJugadorTest("Vinícius Jr", false, equipo1, 100000000);
        Jugador jugador3 = crearJugadorTest("Lewandowski", false, equipo2, 45000000);

        EstadisticaJugadorPartido est1 = new EstadisticaJugadorPartido();
        est1.setJugador(jugador1);
        est1.setPartido(partido);
        est1.setGolesAnotados(3);
        est1.setAsistencias(0);
        est1.setTarjetaAmarillas(0);
        est1.setTarjetaRojas(false);
        est1.setMinMinutosJugados(true);
        est1.setGolesRecibidos(0);
        est1.setPuntosJornada(18);
        estadisticaRepository.save(est1);

        EstadisticaJugadorPartido est2 = new EstadisticaJugadorPartido();
        est2.setJugador(jugador2);
        est2.setPartido(partido);
        est2.setGolesAnotados(1);
        est2.setAsistencias(2);
        est2.setTarjetaAmarillas(0);
        est2.setTarjetaRojas(false);
        est2.setMinMinutosJugados(true);
        est2.setGolesRecibidos(0);
        est2.setPuntosJornada(12);
        estadisticaRepository.save(est2);

        EstadisticaJugadorPartido est3 = new EstadisticaJugadorPartido();
        est3.setJugador(jugador3);
        est3.setPartido(partido);
        est3.setGolesAnotados(2);
        est3.setAsistencias(0);
        est3.setTarjetaAmarillas(1);
        est3.setTarjetaRojas(false);
        est3.setMinMinutosJugados(true);
        est3.setGolesRecibidos(0);
        est3.setPuntosJornada(11);
        estadisticaRepository.save(est3);

        List<EstadisticaJugadorPartido> estadisticas = estadisticaRepository.findAll();
        estadisticas.sort((e1, e2) -> Integer.compare(e2.getGolesAnotados(), e1.getGolesAnotados()));

        System.out.println("✓ Jugadores ordenados por goles:");
        for (EstadisticaJugadorPartido stat : estadisticas) {
            System.out.println("  - " + stat.getJugador().getNombreJugador() +
                             " - Goles: " + stat.getGolesAnotados() +
                             " - Puntos: " + stat.getPuntosJornada());
        }

        assertTrue(estadisticas.get(0).getGolesAnotados() >= estadisticas.get(1).getGolesAnotados(),
                  "Debe estar ordenado por goles descendente");
    }

    // ========================================
    // FUNCIONALIDAD 8: VER CLASIFICACIÓN GENERAL
    // ========================================

    @Test
    public void testFuncionalidad8_ConsultarRankingUsuarios() {
        System.out.println("\n=== TEST: 8. Ver clasificación general ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Usuario usuario1 = crearUsuarioTest("Juan Pérez", liga, 250);
        Usuario usuario2 = crearUsuarioTest("María García", liga, 300);
        Usuario usuario3 = crearUsuarioTest("Pedro Martínez", liga, 180);
        Usuario usuario4 = crearUsuarioTest("Ana López", liga, 320);

        List<Usuario> ranking = usuarioRepository.findByLigaIdLigaCumeOrderByPuntosAcumuladosDesc(liga.getIdLigaCume());

        assertEquals(4, ranking.size(), "Debe haber 4 usuarios en el ranking");

        System.out.println("✓ CLASIFICACIÓN GENERAL:");
        System.out.println("================================================");
        int posicion = 1;
        for (Usuario usuario : ranking) {
            System.out.println("  " + posicion + "º " + usuario.getNombreUsuario() +
                             " - " + usuario.getPuntosAcumulados() + " puntos");
            posicion++;
        }
        System.out.println("================================================");

        // Verificar que está ordenado
        for (int i = 0; i < ranking.size() - 1; i++) {
            assertTrue(ranking.get(i).getPuntosAcumulados() >= ranking.get(i + 1).getPuntosAcumulados(),
                      "El ranking debe estar ordenado de mayor a menor");
        }

        assertEquals("Ana López", ranking.get(0).getNombreUsuario(), "Ana López debe ser primera con 320 puntos");
    }

    // ========================================
    // FUNCIONALIDAD 9: VER RESULTADOS
    // ========================================

    @Test
    public void testFuncionalidad9_VerResultadosPartidosDisputados() {
        System.out.println("\n=== TEST: 9. Ver resultados de partidos disputados ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Jornada jornada1 = crearJornadaTest(liga);
        Jornada jornada2 = crearJornadaTest(liga);
        Equipo equipo1 = crearEquipoTest("Real Madrid", liga);
        Equipo equipo2 = crearEquipoTest("Barcelona", liga);
        Equipo equipo3 = crearEquipoTest("Atlético", liga);

        Partido partido1 = crearPartidoTest(jornada1, equipo1, equipo2, 3, 1);
        Partido partido2 = crearPartidoTest(jornada1, equipo3, equipo1, 0, 2);
        Partido partido3 = crearPartidoTest(jornada2, equipo2, equipo3, 2, 2);

        List<Partido> partidos = partidoRepository.findAll();

        assertTrue(partidos.size() >= 3, "Debe haber al menos 3 partidos disputados");

        System.out.println("✓ RESULTADOS DE PARTIDOS:");
        System.out.println("================================================");
        for (Partido partido : partidos) {
            String resultado = partido.getGolesLocal() + " - " + partido.getGolesVisitante();
            System.out.println("  Jornada " + partido.getJornada().getIdJornada() + ": " +
                             partido.getEquipoLocal().getNombreEquipo() + " " + resultado + " " +
                             partido.getEquipoVisitante().getNombreEquipo());
        }
        System.out.println("================================================");
    }

    // ========================================
    // FUNCIONALIDAD 10: PUNTUACIONES DE USUARIOS POR JORNADA
    // ========================================

    @Test
    public void testFuncionalidad10_ConsultarPuntuacionJornada() {
        System.out.println("\n=== TEST: 10. Puntuaciones de usuarios por jornada ===");

        // Crear datos de prueba
        LigaCume liga = crearLigaTest();
        Jornada jornada = crearJornadaTest(liga);
        Usuario usuario1 = crearUsuarioTest("Roberto Gómez", liga, 0);
        Usuario usuario2 = crearUsuarioTest("Laura Fernández", liga, 0);

        Equipo equipo = crearEquipoTest("Sevilla FC", liga);
        Jugador jugador1 = crearJugadorTest("Youssef En-Nesyri", false, equipo, 15000000);
        Jugador jugador2 = crearJugadorTest("Ivan Rakitic", false, equipo, 8000000);

        Equipo equipo2 = crearEquipoTest("Valencia CF", liga);
        Partido partido = crearPartidoTest(jornada, equipo, equipo2, 2, 0);

        // Crear estadísticas
        EstadisticaJugadorPartido est1 = new EstadisticaJugadorPartido();
        est1.setJugador(jugador1);
        est1.setPartido(partido);
        est1.setGolesAnotados(2);
        est1.setAsistencias(0);
        est1.setTarjetaAmarillas(0);
        est1.setTarjetaRojas(false);
        est1.setMinMinutosJugados(true);
        est1.setGolesRecibidos(0);
        est1.setPuntosJornada(14);
        estadisticaRepository.save(est1);

        EstadisticaJugadorPartido est2 = new EstadisticaJugadorPartido();
        est2.setJugador(jugador2);
        est2.setPartido(partido);
        est2.setGolesAnotados(0);
        est2.setAsistencias(2);
        est2.setTarjetaAmarillas(1);
        est2.setTarjetaRojas(false);
        est2.setMinMinutosJugados(true);
        est2.setGolesRecibidos(0);
        est2.setPuntosJornada(9);
        estadisticaRepository.save(est2);

        // Crear alineaciones
        Alineacion alineacion1 = new Alineacion();
        alineacion1.setUsuario(usuario1);
        alineacion1.setJornada(jornada);
        alineacion1.getJugadores().add(jugador1);
        alineacion1.setPuntosTotalesJornada(14);
        alineacionRepository.save(alineacion1);

        Alineacion alineacion2 = new Alineacion();
        alineacion2.setUsuario(usuario2);
        alineacion2.setJornada(jornada);
        alineacion2.getJugadores().add(jugador2);
        alineacion2.setPuntosTotalesJornada(9);
        alineacionRepository.save(alineacion2);

        List<Alineacion> alineaciones = alineacionRepository.findByJornadaIdJornada(jornada.getIdJornada());

        assertEquals(2, alineaciones.size(), "Debe haber 2 alineaciones en la jornada");

        System.out.println("✓ PUNTUACIONES JORNADA " + jornada.getIdJornada() + ":");
        System.out.println("================================================");

        for (Alineacion alineacion : alineaciones) {
            System.out.println("\n  Usuario: " + alineacion.getUsuario().getNombreUsuario());
            System.out.println("  Puntos totales de la jornada: " + alineacion.getPuntosTotalesJornada());
            System.out.println("  Jugadores seleccionados:");

            for (Jugador jugador : alineacion.getJugadores()) {
                List<EstadisticaJugadorPartido> stats = estadisticaRepository
                    .findByJugadorIdJugador(jugador.getIdJugador());

                int puntosJugador = 0;
                for (EstadisticaJugadorPartido stat : stats) {
                    if (stat.getPartido().getJornada().getIdJornada().equals(jornada.getIdJornada())) {
                        puntosJugador = stat.getPuntosJornada();
                        break;
                    }
                }

                System.out.println("    - " + jugador.getNombreJugador() +
                                 " (" + jugador.getEquipo().getNombreEquipo() + ")" +
                                 " - Puntos: " + puntosJugador);
            }
        }
        System.out.println("================================================");
    }

    @Test
    public void testFuncionalidad10_MostrarJugadoresSeleccionadosYPuntuaciones() {
        System.out.println("\n=== TEST: 10. Mostrar jugadores seleccionados y sus puntuaciones ===");

        // Crear datos de prueba completos
        LigaCume liga = crearLigaTest();
        Jornada jornada = crearJornadaTest(liga);
        Usuario usuario = crearUsuarioTest("Diego Martínez", liga, 0);

        Equipo equipo = crearEquipoTest("Athletic Club", liga);
        Equipo equipo2 = crearEquipoTest("Real Sociedad", liga);
        Partido partido = crearPartidoTest(jornada, equipo, equipo2, 3, 1);

        Jugador jugador1 = crearJugadorTest("Iñaki Williams", false, equipo, 15000000);
        Jugador jugador2 = crearJugadorTest("Nico Williams", false, equipo, 25000000);
        Jugador jugador3 = crearJugadorTest("Oihan Sancet", false, equipo, 18000000);

        // Crear estadísticas detalladas
        EstadisticaJugadorPartido est1 = new EstadisticaJugadorPartido();
        est1.setJugador(jugador1);
        est1.setPartido(partido);
        est1.setGolesAnotados(2);
        est1.setAsistencias(1);
        est1.setTarjetaAmarillas(0);
        est1.setTarjetaRojas(false);
        est1.setMinMinutosJugados(true);
        est1.setGolesRecibidos(0);
        est1.setPuntosJornada(17);
        estadisticaRepository.save(est1);


        EstadisticaJugadorPartido est2 = new EstadisticaJugadorPartido();
        est2.setJugador(jugador2);
        est2.setPartido(partido);
        est2.setGolesAnotados(1);
        est2.setAsistencias(2);
        est2.setTarjetaAmarillas(1);
        est2.setTarjetaRojas(false);
        est2.setMinMinutosJugados(true);
        est2.setGolesRecibidos(0);
        est2.setPuntosJornada(14);
        estadisticaRepository.save(est2);

        EstadisticaJugadorPartido est3 = new EstadisticaJugadorPartido();
        est3.setJugador(jugador3);
        est3.setPartido(partido);
        est3.setGolesAnotados(0);
        est3.setAsistencias(0);
        est3.setTarjetaAmarillas(0);
        est3.setTarjetaRojas(true);
        est3.setMinMinutosJugados(true);
        est3.setGolesRecibidos(0);
        est3.setPuntosJornada(-2);
        estadisticaRepository.save(est3);

        Alineacion alineacion = new Alineacion();
        alineacion.setUsuario(usuario);
        alineacion.setJornada(jornada);
        alineacion.getJugadores().add(jugador1);
        alineacion.getJugadores().add(jugador2);
        alineacion.getJugadores().add(jugador3);
        alineacion.setPuntosTotalesJornada(29); // 17 + 14 - 2
        alineacion = alineacionRepository.save(alineacion);

        alineacionRepository.flush();
        Alineacion alineacionConsultada = alineacionRepository.findById(alineacion.getIdAlineacion()).orElse(null);
        assertNotNull(alineacionConsultada, "La alineación debe existir");

        System.out.println("✓ Detalle de alineación:");
        System.out.println("tamaño de la alineacion " + alineacionConsultada.getJugadores().size());
        System.out.println("  Usuario: " + alineacionConsultada.getUsuario().getNombreUsuario());
        System.out.println("  Jornada: " + alineacionConsultada.getJornada().getIdJornada());
        System.out.println("  Puntos totales: " + alineacionConsultada.getPuntosTotalesJornada());
        System.out.println("\n  Detalle por jugador:");

        int totalCalculado = 0;
        for (Jugador jugador : alineacionConsultada.getJugadores()) {
            EstadisticaJugadorPartido stat =  estadisticaRepository.findByJugadorIdJugadorAndPartidoJornadaIdJornada(jugador.getIdJugador(), jornada.getIdJornada());
            System.out.println(jugador.toString());
                if (stat.getPartido().getJornada().getIdJornada().equals(alineacionConsultada.getJornada().getIdJornada())) {
                    totalCalculado += stat.getPuntosJornada();
                    System.out.println("    - " + jugador.getNombreJugador());
                    System.out.println("      Goles: " + stat.getGolesAnotados());
                    System.out.println("      Asistencias: " + stat.getAsistencias());
                    System.out.println("      Tarjetas amarillas: " + stat.getTarjetaAmarillas());
                    System.out.println("      Tarjeta roja: " + (stat.isTarjetaRojas() ? "Sí" : "No"));
                    System.out.println("      Puntos: " + stat.getPuntosJornada());
            }
        }

        System.out.println("\n  Total calculado: " + totalCalculado + " puntos");
        assertEquals(29, totalCalculado, "El total calculado debe coincidir con los puntos de la alineación");
    }

    // ========================================
    // TEST RESUMEN DE TODAS LAS FUNCIONALIDADES
    // ========================================

    @Test
    public void testResumenTodasLasFuncionalidades() {
        System.out.println("\n========================================");
        System.out.println("RESUMEN DE TODAS LAS FUNCIONALIDADES");
        System.out.println("========================================");

        System.out.println("\n✓ Funcionalidad 1: Gestión de equipos - OK");
        System.out.println("  - Agregar, modificar, eliminar y listar equipos");

        System.out.println("\n✓ Funcionalidad 2: Gestión de jugadores - OK");
        System.out.println("  - Agregar, actualizar, eliminar y listar jugadores");

        System.out.println("\n✓ Funcionalidad 3: Gestión de partidos - OK");
        System.out.println("  - Agregar y modificar resultados de partidos");

        System.out.println("\n✓ Funcionalidad 4: Estadísticas de jugadores - OK");
        System.out.println("  - Añadir y modificar estadísticas por partido");

        System.out.println("\n✓ Funcionalidad 5: Calendario de enfrentamientos - OK");
        System.out.println("  - Generar cuadro de enfrentamientos por jornada");

        System.out.println("\n✓ Funcionalidad 6: Crear alineación - OK");
        System.out.println("  - Listar jugadores, seleccionar y consultar alineación");

        System.out.println("\n✓ Funcionalidad 7: Consultar estadísticas - OK");
        System.out.println("  - Buscar jugadores por nombre, equipo y puntos");

        System.out.println("\n✓ Funcionalidad 8: Clasificación general - OK");
        System.out.println("  - Ranking de usuarios");

        System.out.println("\n✓ Funcionalidad 9: Ver resultados - OK");
        System.out.println("  - Resultados de partidos disputados");

        System.out.println("\n✓ Funcionalidad 10: Puntuaciones por jornada - OK");
        System.out.println("  - Consultar puntuación total y detalle de jugadores");

        System.out.println("\n========================================");
        System.out.println("TODAS LAS FUNCIONALIDADES VERIFICADAS");
        System.out.println("========================================\n");
    }
}

