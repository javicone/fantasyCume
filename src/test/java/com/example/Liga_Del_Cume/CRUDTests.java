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
public class CRUDTests {

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

    // ==================== TESTS CRUD LIGA ====================
    @Test
    public void testCRUDLigaCume() {
        System.out.println("\n=== TEST CRUD: LigaCume ===");

        // CREATE
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("Liga Fantasy 2024");
        liga.setPresupuestoMaximo(100000000L);
        liga = ligaCumeRepository.save(liga);
        assertNotNull(liga.getIdLigaCume(), "El ID de la liga debe generarse automáticamente");
        System.out.println("✓ CREATE: Liga creada con ID: " + liga.getIdLigaCume());

        // READ
        Optional<LigaCume> ligaLeida = ligaCumeRepository.findById(liga.getIdLigaCume());
        assertTrue(ligaLeida.isPresent(), "La liga debe encontrarse en la base de datos");
        assertEquals("Liga Fantasy 2024", ligaLeida.get().getNombreLiga(), "El nombre debe coincidir");
        assertEquals(100000000L, ligaLeida.get().getPresupuestoMaximo(), "El presupuesto debe coincidir");
        System.out.println("✓ READ: Liga encontrada: " + ligaLeida.get().getNombreLiga());

        // UPDATE
        liga.setNombreLiga("Liga Fantasy 2024-2025");
        liga.setPresupuestoMaximo(120000000L);
        ligaCumeRepository.save(liga);
        LigaCume ligaActualizada = ligaCumeRepository.findById(liga.getIdLigaCume()).orElse(null);
        assertNotNull(ligaActualizada);
        assertEquals("Liga Fantasy 2024-2025", ligaActualizada.getNombreLiga());
        assertEquals(120000000L, ligaActualizada.getPresupuestoMaximo());
        System.out.println("✓ UPDATE: Liga actualizada: " + ligaActualizada.getNombreLiga());

        // DELETE
        Long idLiga = liga.getIdLigaCume();
        ligaCumeRepository.delete(liga);
        Optional<LigaCume> ligaEliminada = ligaCumeRepository.findById(idLiga);
        assertFalse(ligaEliminada.isPresent(), "La liga debe estar eliminada");
        System.out.println("✓ DELETE: Liga eliminada correctamente");
    }

    // ==================== TESTS CRUD EQUIPO ====================
    @Test
    public void testCRUDEquipo() {
        System.out.println("\n=== TEST CRUD: Equipo ===");

        // Preparar datos necesarios
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("Liga Test");
        liga.setPresupuestoMaximo(100000000L);
        liga = ligaCumeRepository.save(liga);

        // CREATE
        Equipo equipo = new Equipo();
        equipo.setNombreEquipo("Athletic Club");
        equipo.setEscudoURL("http://ejemplo.com/escudo.png");
        equipo.setLiga(liga);
        equipo = equipoRepository.save(equipo);
        assertNotNull(equipo.getIdEquipo(), "El ID del equipo debe generarse automáticamente");
        System.out.println("✓ CREATE: Equipo creado con ID: " + equipo.getIdEquipo());

        // READ
        Optional<Equipo> equipoLeido = equipoRepository.findById(equipo.getIdEquipo());
        assertTrue(equipoLeido.isPresent(), "El equipo debe encontrarse en la base de datos");
        assertEquals("Athletic Club", equipoLeido.get().getNombreEquipo());
        System.out.println("✓ READ: Equipo encontrado: " + equipoLeido.get().getNombreEquipo());

        // UPDATE
        equipo.setNombreEquipo("Athletic Club Bilbao");
        equipo.setEscudoURL("http://ejemplo.com/nuevo-escudo.png");
        equipoRepository.save(equipo);
        Equipo equipoActualizado = equipoRepository.findById(equipo.getIdEquipo()).orElse(null);
        assertNotNull(equipoActualizado);
        assertEquals("Athletic Club Bilbao", equipoActualizado.getNombreEquipo());
        assertEquals("http://ejemplo.com/nuevo-escudo.png", equipoActualizado.getEscudoURL());
        System.out.println("✓ UPDATE: Equipo actualizado: " + equipoActualizado.getNombreEquipo());

        // DELETE
        Long idEquipo = equipo.getIdEquipo();
        equipoRepository.delete(equipo);
        Optional<Equipo> equipoEliminado = equipoRepository.findById(idEquipo);
        assertFalse(equipoEliminado.isPresent(), "El equipo debe estar eliminado");
        System.out.println("✓ DELETE: Equipo eliminado correctamente");
    }

    // ==================== TESTS CRUD JUGADOR ====================
    @Test
    public void testCRUDJugador() {
        System.out.println("\n=== TEST CRUD: Jugador ===");

        // Preparar datos necesarios
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("Liga Test");
        liga.setPresupuestoMaximo(100000000L);
        liga = ligaCumeRepository.save(liga);

        Equipo equipo = new Equipo();
        equipo.setNombreEquipo("Real Sociedad");
        equipo.setLiga(liga);
        equipo = equipoRepository.save(equipo);

        // CREATE
        Jugador jugador = new Jugador();
        jugador.setNombreJugador("Mikel Oyarzabal");
        jugador.setEsPortero(false);
        jugador.setPrecioMercado(25000000);
        jugador.setEquipo(equipo);
        jugador = jugadorRepository.save(jugador);
        assertNotNull(jugador.getIdJugador(), "El ID del jugador debe generarse automáticamente");
        System.out.println("✓ CREATE: Jugador creado con ID: " + jugador.getIdJugador());

        // READ
        Optional<Jugador> jugadorLeido = jugadorRepository.findById(jugador.getIdJugador());
        assertTrue(jugadorLeido.isPresent(), "El jugador debe encontrarse en la base de datos");
        assertEquals("Mikel Oyarzabal", jugadorLeido.get().getNombreJugador());
        assertFalse(jugadorLeido.get().isEsPortero());
        System.out.println("✓ READ: Jugador encontrado: " + jugadorLeido.get().getNombreJugador());

        // UPDATE
        jugador.setNombreJugador("Mikel Oyarzabal Ugarte");
        jugador.setPrecioMercado(30000000);
        jugadorRepository.save(jugador);
        Jugador jugadorActualizado = jugadorRepository.findById(jugador.getIdJugador()).orElse(null);
        assertNotNull(jugadorActualizado);
        assertEquals("Mikel Oyarzabal Ugarte", jugadorActualizado.getNombreJugador());
        assertEquals(30000000, jugadorActualizado.getPrecioMercado());
        System.out.println("✓ UPDATE: Jugador actualizado: " + jugadorActualizado.getNombreJugador());

        // DELETE
        Long idJugador = jugador.getIdJugador();
        jugadorRepository.delete(jugador);
        Optional<Jugador> jugadorEliminado = jugadorRepository.findById(idJugador);
        assertFalse(jugadorEliminado.isPresent(), "El jugador debe estar eliminado");
        System.out.println("✓ DELETE: Jugador eliminado correctamente");
    }

    // ==================== TESTS CRUD USUARIO ====================
    @Test
    public void testCRUDUsuario() {
        System.out.println("\n=== TEST CRUD: Usuario ===");

        // Preparar datos necesarios
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("Liga Test");
        liga.setPresupuestoMaximo(100000000L);
        liga = ligaCumeRepository.save(liga);

        // CREATE
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("Carlos Pérez");
        usuario.setPassword("password123");
        usuario.setPuntosAcumulados(0);
        usuario.setLiga(liga);
        usuario = usuarioRepository.save(usuario);
        assertNotNull(usuario.getIdUsuario(), "El ID del usuario debe generarse automáticamente");
        System.out.println("✓ CREATE: Usuario creado con ID: " + usuario.getIdUsuario());

        // READ
        Optional<Usuario> usuarioLeido = usuarioRepository.findById(usuario.getIdUsuario());
        assertTrue(usuarioLeido.isPresent(), "El usuario debe encontrarse en la base de datos");
        assertEquals("Carlos Pérez", usuarioLeido.get().getNombreUsuario());
        assertEquals(0, usuarioLeido.get().getPuntosAcumulados());
        System.out.println("✓ READ: Usuario encontrado: " + usuarioLeido.get().getNombreUsuario());

        // UPDATE
        usuario.setNombreUsuario("Carlos Pérez García");
        usuario.setPuntosAcumulados(150);
        usuario.setPassword("newpassword456");
        usuarioRepository.save(usuario);
        Usuario usuarioActualizado = usuarioRepository.findById(usuario.getIdUsuario()).orElse(null);
        assertNotNull(usuarioActualizado);
        assertEquals("Carlos Pérez García", usuarioActualizado.getNombreUsuario());
        assertEquals(150, usuarioActualizado.getPuntosAcumulados());
        assertEquals("newpassword456", usuarioActualizado.getPassword());
        System.out.println("✓ UPDATE: Usuario actualizado: " + usuarioActualizado.getNombreUsuario() +
                         " con " + usuarioActualizado.getPuntosAcumulados() + " puntos");

        // DELETE
        Long idUsuario = usuario.getIdUsuario();
        usuarioRepository.delete(usuario);
        Optional<Usuario> usuarioEliminado = usuarioRepository.findById(idUsuario);
        assertFalse(usuarioEliminado.isPresent(), "El usuario debe estar eliminado");
        System.out.println("✓ DELETE: Usuario eliminado correctamente");
    }

    // ==================== TESTS CRUD JORNADA ====================
    @Test
    public void testCRUDJornada() {
        System.out.println("\n=== TEST CRUD: Jornada ===");

        // Preparar datos necesarios
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("Liga Test");
        liga.setPresupuestoMaximo(100000000L);
        liga = ligaCumeRepository.save(liga);

        // CREATE
        Jornada jornada = new Jornada();
        jornada.setLiga(liga);
        jornada = jornadaRepository.save(jornada);
        assertNotNull(jornada.getIdJornada(), "El ID de la jornada debe generarse automáticamente");
        System.out.println("✓ CREATE: Jornada creada con ID: " + jornada.getIdJornada());

        // READ
        Optional<Jornada> jornadaLeida = jornadaRepository.findById(jornada.getIdJornada());
        assertTrue(jornadaLeida.isPresent(), "La jornada debe encontrarse en la base de datos");
        assertEquals(liga.getIdLigaCume(), jornadaLeida.get().getLiga().getIdLigaCume());
        System.out.println("✓ READ: Jornada encontrada con ID: " + jornadaLeida.get().getIdJornada());

        // UPDATE (las jornadas no tienen muchos campos editables, pero podemos cambiar la liga)
        LigaCume nuevaLiga = new LigaCume();
        nuevaLiga.setNombreLiga("Nueva Liga");
        nuevaLiga.setPresupuestoMaximo(80000000L);
        nuevaLiga = ligaCumeRepository.save(nuevaLiga);

        jornada.setLiga(nuevaLiga);
        jornadaRepository.save(jornada);
        Jornada jornadaActualizada = jornadaRepository.findById(jornada.getIdJornada()).orElse(null);
        assertNotNull(jornadaActualizada);
        assertEquals(nuevaLiga.getIdLigaCume(), jornadaActualizada.getLiga().getIdLigaCume());
        System.out.println("✓ UPDATE: Jornada actualizada con nueva liga: " +
                         jornadaActualizada.getLiga().getNombreLiga());

        // DELETE
        Long idJornada = jornada.getIdJornada();
        jornadaRepository.delete(jornada);
        Optional<Jornada> jornadaEliminada = jornadaRepository.findById(idJornada);
        assertFalse(jornadaEliminada.isPresent(), "La jornada debe estar eliminada");
        System.out.println("✓ DELETE: Jornada eliminada correctamente");
    }

    // ==================== TESTS CRUD PARTIDO ====================
    @Test
    public void testCRUDPartido() {
        System.out.println("\n=== TEST CRUD: Partido ===");

        // Preparar datos necesarios
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("Liga Test");
        liga.setPresupuestoMaximo(100000000L);
        liga = ligaCumeRepository.save(liga);

        Equipo equipoLocal = new Equipo();
        equipoLocal.setNombreEquipo("Barcelona");
        equipoLocal.setLiga(liga);
        equipoLocal = equipoRepository.save(equipoLocal);

        Equipo equipoVisitante = new Equipo();
        equipoVisitante.setNombreEquipo("Real Madrid");
        equipoVisitante.setLiga(liga);
        equipoVisitante = equipoRepository.save(equipoVisitante);

        Jornada jornada = new Jornada();
        jornada.setLiga(liga);
        jornada = jornadaRepository.save(jornada);

        // CREATE
        Partido partido = new Partido();
        partido.setEquipoLocal(equipoLocal);
        partido.setEquipoVisitante(equipoVisitante);
        partido.setGolesLocal(2);
        partido.setGolesVisitante(1);
        partido.setJornada(jornada);
        partido = partidoRepository.save(partido);
        assertNotNull(partido.getIdPartido(), "El ID del partido debe generarse automáticamente");
        System.out.println("✓ CREATE: Partido creado con ID: " + partido.getIdPartido() +
                         " (" + equipoLocal.getNombreEquipo() + " " + partido.getGolesLocal() +
                         " - " + partido.getGolesVisitante() + " " + equipoVisitante.getNombreEquipo() + ")");

        // READ
        Optional<Partido> partidoLeido = partidoRepository.findById(partido.getIdPartido());
        assertTrue(partidoLeido.isPresent(), "El partido debe encontrarse en la base de datos");
        assertEquals(2, partidoLeido.get().getGolesLocal());
        assertEquals(1, partidoLeido.get().getGolesVisitante());
        System.out.println("✓ READ: Partido encontrado: " +
                         partidoLeido.get().getEquipoLocal().getNombreEquipo() + " vs " +
                         partidoLeido.get().getEquipoVisitante().getNombreEquipo());

        // UPDATE
        partido.setGolesLocal(3);
        partido.setGolesVisitante(3);
        partidoRepository.save(partido);
        Partido partidoActualizado = partidoRepository.findById(partido.getIdPartido()).orElse(null);
        assertNotNull(partidoActualizado);
        assertEquals(3, partidoActualizado.getGolesLocal());
        assertEquals(3, partidoActualizado.getGolesVisitante());
        System.out.println("✓ UPDATE: Resultado actualizado a " + partidoActualizado.getGolesLocal() +
                         " - " + partidoActualizado.getGolesVisitante());

        // DELETE
        Long idPartido = partido.getIdPartido();
        partidoRepository.delete(partido);
        Optional<Partido> partidoEliminado = partidoRepository.findById(idPartido);
        assertFalse(partidoEliminado.isPresent(), "El partido debe estar eliminado");
        System.out.println("✓ DELETE: Partido eliminado correctamente");
    }

    // ==================== TESTS CRUD ALINEACION ====================
    @Test
    public void testCRUDAlineacion() {
        System.out.println("\n=== TEST CRUD: Alineacion ===");

        // Preparar datos necesarios
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("Liga Test");
        liga.setPresupuestoMaximo(100000000L);
        liga = ligaCumeRepository.save(liga);

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("Ana López");
        usuario.setPuntosAcumulados(0);
        usuario.setLiga(liga);
        usuario = usuarioRepository.save(usuario);

        Jornada jornada = new Jornada();
        jornada.setLiga(liga);
        jornada = jornadaRepository.save(jornada);

        Equipo equipo = new Equipo();
        equipo.setNombreEquipo("Atlético Madrid");
        equipo.setLiga(liga);
        equipo = equipoRepository.save(equipo);

        Jugador jugador1 = new Jugador();
        jugador1.setNombreJugador("Antoine Griezmann");
        jugador1.setEsPortero(false);
        jugador1.setPrecioMercado(20000000);
        jugador1.setEquipo(equipo);
        jugador1 = jugadorRepository.save(jugador1);

        Jugador jugador2 = new Jugador();
        jugador2.setNombreJugador("Jan Oblak");
        jugador2.setEsPortero(true);
        jugador2.setPrecioMercado(35000000);
        jugador2.setEquipo(equipo);
        jugador2 = jugadorRepository.save(jugador2);

        // CREATE
        Alineacion alineacion = new Alineacion();
        alineacion.setUsuario(usuario);
        alineacion.setJornada(jornada);
        alineacion.setPuntosTotalesJornada(0);
        alineacion.getJugadores().add(jugador1);
        alineacion.getJugadores().add(jugador2);
        alineacion = alineacionRepository.save(alineacion);
        assertNotNull(alineacion.getIdAlineacion(), "El ID de la alineación debe generarse automáticamente");
        System.out.println("✓ CREATE: Alineación creada con ID: " + alineacion.getIdAlineacion() +
                         " con " + alineacion.getJugadores().size() + " jugadores");

        // READ
        Optional<Alineacion> alineacionLeida = alineacionRepository.findById(alineacion.getIdAlineacion());
        assertTrue(alineacionLeida.isPresent(), "La alineación debe encontrarse en la base de datos");
        assertEquals(2, alineacionLeida.get().getJugadores().size(), "Debe tener 2 jugadores");
        System.out.println("✓ READ: Alineación encontrada del usuario: " +
                         alineacionLeida.get().getUsuario().getNombreUsuario());

        // UPDATE
        Jugador jugador3 = new Jugador();
        jugador3.setNombreJugador("Koke");
        jugador3.setEsPortero(false);
        jugador3.setPrecioMercado(15000000);
        jugador3.setEquipo(equipo);
        jugador3 = jugadorRepository.save(jugador3);

        alineacion.getJugadores().add(jugador3);
        alineacion.setPuntosTotalesJornada(45);
        alineacionRepository.save(alineacion);
        Alineacion alineacionActualizada = alineacionRepository.findById(alineacion.getIdAlineacion()).orElse(null);
        assertNotNull(alineacionActualizada);
        assertEquals(3, alineacionActualizada.getJugadores().size(), "Debe tener 3 jugadores");
        assertEquals(45, alineacionActualizada.getPuntosTotalesJornada());
        System.out.println("✓ UPDATE: Alineación actualizada con " +
                         alineacionActualizada.getJugadores().size() + " jugadores y " +
                         alineacionActualizada.getPuntosTotalesJornada() + " puntos");

        // DELETE
        Long idAlineacion = alineacion.getIdAlineacion();
        alineacionRepository.delete(alineacion);
        Optional<Alineacion> alineacionEliminada = alineacionRepository.findById(idAlineacion);
        assertFalse(alineacionEliminada.isPresent(), "La alineación debe estar eliminada");
        System.out.println("✓ DELETE: Alineación eliminada correctamente");
    }

    // ==================== TESTS CRUD ESTADISTICA JUGADOR PARTIDO ====================
    @Test
    public void testCRUDEstadisticaJugadorPartido() {
        System.out.println("\n=== TEST CRUD: EstadisticaJugadorPartido ===");

        // Preparar datos necesarios
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("Liga Test");
        liga.setPresupuestoMaximo(100000000L);
        liga = ligaCumeRepository.save(liga);

        Equipo equipo1 = new Equipo();
        equipo1.setNombreEquipo("Sevilla");
        equipo1.setLiga(liga);
        equipo1 = equipoRepository.save(equipo1);

        Equipo equipo2 = new Equipo();
        equipo2.setNombreEquipo("Valencia");
        equipo2.setLiga(liga);
        equipo2 = equipoRepository.save(equipo2);

        Jugador jugador = new Jugador();
        jugador.setNombreJugador("Jesús Navas");
        jugador.setEsPortero(false);
        jugador.setPrecioMercado(5000000);
        jugador.setEquipo(equipo1);
        jugador = jugadorRepository.save(jugador);

        Jornada jornada = new Jornada();
        jornada.setLiga(liga);
        jornada = jornadaRepository.save(jornada);

        Partido partido = new Partido();
        partido.setEquipoLocal(equipo1);
        partido.setEquipoVisitante(equipo2);
        partido.setGolesLocal(2);
        partido.setGolesVisitante(0);
        partido.setJornada(jornada);
        partido = partidoRepository.save(partido);

        // CREATE
        EstadisticaJugadorPartido estadistica = new EstadisticaJugadorPartido();
        estadistica.setJugador(jugador);
        estadistica.setPartido(partido);
        estadistica.setGolesAnotados(1);
        estadistica.setAsistencias(1);
        estadistica.setTarjetaAmarillas(0);
        estadistica.setTarjetaRojas(false);
        estadistica.setMinMinutosJugados(true);
        estadistica.setGolesRecibidos(0);
        estadistica.setPuntosJornada(12);
        estadistica = estadisticaRepository.save(estadistica);
        System.out.println("✓ CREATE: Estadística creada para jugador: " + jugador.getNombreJugador() +
                         " con " + estadistica.getPuntosJornada() + " puntos");

        // READ
        List<EstadisticaJugadorPartido> estadisticasLeidas =
            estadisticaRepository.findByJugadorIdJugador(jugador.getIdJugador());
        assertFalse(estadisticasLeidas.isEmpty(), "Debe encontrarse al menos una estadística");
        EstadisticaJugadorPartido estadisticaLeida = estadisticasLeidas.get(0);
        assertEquals(1, estadisticaLeida.getGolesAnotados());
        assertEquals(1, estadisticaLeida.getAsistencias());
        assertEquals(12, estadisticaLeida.getPuntosJornada());
        System.out.println("✓ READ: Estadística encontrada - Goles: " + estadisticaLeida.getGolesAnotados() +
                         ", Asistencias: " + estadisticaLeida.getAsistencias());

        // UPDATE
        estadistica.setGolesAnotados(2);
        estadistica.setAsistencias(2);
        estadistica.setTarjetaAmarillas(1);
        estadistica.setPuntosJornada(18);
        estadisticaRepository.save(estadistica);

        List<EstadisticaJugadorPartido> estadisticasActualizadas =
            estadisticaRepository.findByJugadorIdJugador(jugador.getIdJugador());
        EstadisticaJugadorPartido estadisticaActualizada = estadisticasActualizadas.get(0);
        assertEquals(2, estadisticaActualizada.getGolesAnotados());
        assertEquals(2, estadisticaActualizada.getAsistencias());
        assertEquals(1, estadisticaActualizada.getTarjetaAmarillas());
        assertEquals(18, estadisticaActualizada.getPuntosJornada());
        System.out.println("✓ UPDATE: Estadística actualizada - Goles: " +
                         estadisticaActualizada.getGolesAnotados() +
                         ", Puntos: " + estadisticaActualizada.getPuntosJornada());

        // DELETE
        estadisticaRepository.delete(estadistica);
        List<EstadisticaJugadorPartido> estadisticasEliminadas =
            estadisticaRepository.findByJugadorIdJugador(jugador.getIdJugador());
        assertTrue(estadisticasEliminadas.isEmpty(), "No debe haber estadísticas después de eliminar");
        System.out.println("✓ DELETE: Estadística eliminada correctamente");
    }
}
