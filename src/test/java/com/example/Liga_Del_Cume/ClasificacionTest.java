package com.example.Liga_Del_Cume;

import com.example.Liga_Del_Cume.data.model.ClasificacionEquipo;
import com.example.Liga_Del_Cume.data.model.Equipo;
import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.model.Partido;
import com.example.Liga_Del_Cume.data.model.Jornada;
import com.example.Liga_Del_Cume.data.repository.EquipoRepository;
import com.example.Liga_Del_Cume.data.repository.LigaCumeRepository;
import com.example.Liga_Del_Cume.data.repository.PartidoRepository;
import com.example.Liga_Del_Cume.data.repository.JornadaRepository;
import com.example.Liga_Del_Cume.data.service.ClasificacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para la funcionalidad de clasificación de equipos
 */
@SpringBootTest
@Transactional
public class ClasificacionTest {

    @Autowired
    private ClasificacionService clasificacionService;

    @Autowired
    private LigaCumeRepository ligaCumeRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private PartidoRepository partidoRepository;

    @Autowired
    private JornadaRepository jornadaRepository;

    /**
     * Test básico: Clasificación con equipos sin partidos
     * Todos los equipos deben tener 0 puntos
     */
    @Test
    public void testClasificacionSinPartidos() {
        // Crear liga
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("Liga Test");
        liga = ligaCumeRepository.save(liga);

        // Crear equipos
        Equipo equipo1 = new Equipo();
        equipo1.setNombreEquipo("Equipo A");
        equipo1.setEscudoURL("url1");
        equipo1.setLiga(liga);
        equipoRepository.save(equipo1);

        Equipo equipo2 = new Equipo();
        equipo2.setNombreEquipo("Equipo B");
        equipo2.setEscudoURL("url2");
        equipo2.setLiga(liga);
        equipoRepository.save(equipo2);

        // Obtener clasificación
        List<ClasificacionEquipo> clasificacion = clasificacionService.obtenerClasificacionLiga(liga.getIdLigaCume());

        // Verificaciones
        assertNotNull(clasificacion);
        assertEquals(2, clasificacion.size());

        // Todos deben tener 0 puntos
        for (ClasificacionEquipo ce : clasificacion) {
            assertEquals(0, ce.getPuntosTotales());
            assertEquals(0, ce.getVictorias());
            assertEquals(0, ce.getEmpates());
            assertEquals(0, ce.getDerrotas());
        }
    }

    /**
     * Test: Victoria otorga 3 puntos
     */
    @Test
    public void testVictoriaOtorga3Puntos() {
        // Crear liga
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("Liga Test Victoria");
        liga = ligaCumeRepository.save(liga);

        // Crear jornada
        Jornada jornada = new Jornada();
        jornada.setNumeroJornada(1);
        jornada.setLiga(liga);
        jornada = jornadaRepository.save(jornada);

        // Crear equipos
        Equipo equipo1 = new Equipo();
        equipo1.setNombreEquipo("Ganador");
        equipo1.setEscudoURL("url1");
        equipo1.setLiga(liga);
        equipo1 = equipoRepository.save(equipo1);

        Equipo equipo2 = new Equipo();
        equipo2.setNombreEquipo("Perdedor");
        equipo2.setEscudoURL("url2");
        equipo2.setLiga(liga);
        equipo2 = equipoRepository.save(equipo2);

        // Crear partido: Ganador 3-0 Perdedor
        Partido partido = new Partido();
        partido.setEquipoLocal(equipo1);
        partido.setEquipoVisitante(equipo2);
        partido.setGolesLocal(3);
        partido.setGolesVisitante(0);
        partido.setJornada(jornada);
        partidoRepository.save(partido);

        // Obtener clasificación
        List<ClasificacionEquipo> clasificacion = clasificacionService.obtenerClasificacionLiga(liga.getIdLigaCume());

        // Verificaciones
        ClasificacionEquipo ganador = clasificacion.stream()
            .filter(c -> c.getNombreEquipo().equals("Ganador"))
            .findFirst()
            .orElse(null);

        ClasificacionEquipo perdedor = clasificacion.stream()
            .filter(c -> c.getNombreEquipo().equals("Perdedor"))
            .findFirst()
            .orElse(null);

        assertNotNull(ganador);
        assertNotNull(perdedor);

        // Ganador debe tener 3 puntos
        assertEquals(3, ganador.getPuntosTotales());
        assertEquals(1, ganador.getVictorias());
        assertEquals(0, ganador.getEmpates());
        assertEquals(0, ganador.getDerrotas());

        // Perdedor debe tener 0 puntos
        assertEquals(0, perdedor.getPuntosTotales());
        assertEquals(0, perdedor.getVictorias());
        assertEquals(0, perdedor.getEmpates());
        assertEquals(1, perdedor.getDerrotas());
    }

    /**
     * Test: Empate otorga 1 punto a ambos equipos
     */
    @Test
    public void testEmpateOtorga1Punto() {
        // Crear liga
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("Liga Test Empate");
        liga = ligaCumeRepository.save(liga);

        // Crear jornada
        Jornada jornada = new Jornada();
        jornada.setNumeroJornada(1);
        jornada.setLiga(liga);
        jornada = jornadaRepository.save(jornada);

        // Crear equipos
        Equipo equipo1 = new Equipo();
        equipo1.setNombreEquipo("Equipo Uno");
        equipo1.setEscudoURL("url1");
        equipo1.setLiga(liga);
        equipo1 = equipoRepository.save(equipo1);

        Equipo equipo2 = new Equipo();
        equipo2.setNombreEquipo("Equipo Dos");
        equipo2.setEscudoURL("url2");
        equipo2.setLiga(liga);
        equipo2 = equipoRepository.save(equipo2);

        // Crear partido: Empate 2-2
        Partido partido = new Partido();
        partido.setEquipoLocal(equipo1);
        partido.setEquipoVisitante(equipo2);
        partido.setGolesLocal(2);
        partido.setGolesVisitante(2);
        partido.setJornada(jornada);
        partidoRepository.save(partido);

        // Obtener clasificación
        List<ClasificacionEquipo> clasificacion = clasificacionService.obtenerClasificacionLiga(liga.getIdLigaCume());

        // Ambos equipos deben tener 1 punto
        for (ClasificacionEquipo ce : clasificacion) {
            assertEquals(1, ce.getPuntosTotales());
            assertEquals(0, ce.getVictorias());
            assertEquals(1, ce.getEmpates());
            assertEquals(0, ce.getDerrotas());
        }
    }

    /**
     * Test: Ordenación correcta por puntos
     */
    @Test
    public void testOrdenacionPorPuntos() {
        // Crear liga
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("Liga Test Orden");
        liga = ligaCumeRepository.save(liga);

        // Crear jornada
        Jornada jornada = new Jornada();
        jornada.setNumeroJornada(1);
        jornada.setLiga(liga);
        jornada = jornadaRepository.save(jornada);

        // Crear 3 equipos
        Equipo primero = new Equipo();
        primero.setNombreEquipo("Primero");
        primero.setEscudoURL("url1");
        primero.setLiga(liga);
        primero = equipoRepository.save(primero);

        Equipo segundo = new Equipo();
        segundo.setNombreEquipo("Segundo");
        segundo.setEscudoURL("url2");
        segundo.setLiga(liga);
        segundo = equipoRepository.save(segundo);

        Equipo tercero = new Equipo();
        tercero.setNombreEquipo("Tercero");
        tercero.setEscudoURL("url3");
        tercero.setLiga(liga);
        tercero = equipoRepository.save(tercero);

        // Crear partidos
        // Primero 3-0 Tercero -> Primero tiene 3 pts
        Partido p1 = new Partido();
        p1.setEquipoLocal(primero);
        p1.setEquipoVisitante(tercero);
        p1.setGolesLocal(3);
        p1.setGolesVisitante(0);
        p1.setJornada(jornada);
        partidoRepository.save(p1);

        // Segundo 1-1 Tercero -> Segundo tiene 1 pt, Tercero tiene 1 pt
        Partido p2 = new Partido();
        p2.setEquipoLocal(segundo);
        p2.setEquipoVisitante(tercero);
        p2.setGolesLocal(1);
        p2.setGolesVisitante(1);
        p2.setJornada(jornada);
        partidoRepository.save(p2);

        // Obtener clasificación
        List<ClasificacionEquipo> clasificacion = clasificacionService.obtenerClasificacionLiga(liga.getIdLigaCume());

        // Verificar orden
        assertEquals(3, clasificacion.size());
        assertEquals("Primero", clasificacion.get(0).getNombreEquipo());
        assertEquals(3, clasificacion.get(0).getPuntosTotales());

        // Segundo y Tercero ambos tienen 1 punto
        assertTrue(clasificacion.get(1).getPuntosTotales() == 1 || clasificacion.get(2).getPuntosTotales() == 1);
    }

    /**
     * Test: Cálculo correcto de goles
     */
    @Test
    public void testCalculoGoles() {
        // Crear liga
        LigaCume liga = new LigaCume();
        liga.setNombreLiga("Liga Test Goles");
        liga = ligaCumeRepository.save(liga);

        // Crear jornada
        Jornada jornada = new Jornada();
        jornada.setNumeroJornada(1);
        jornada.setLiga(liga);
        jornada = jornadaRepository.save(jornada);

        // Crear equipos
        Equipo equipo1 = new Equipo();
        equipo1.setNombreEquipo("Equipo Test");
        equipo1.setEscudoURL("url1");
        equipo1.setLiga(liga);
        equipo1 = equipoRepository.save(equipo1);

        Equipo equipo2 = new Equipo();
        equipo2.setNombreEquipo("Equipo Rival");
        equipo2.setEscudoURL("url2");
        equipo2.setLiga(liga);
        equipo2 = equipoRepository.save(equipo2);

        // Crear partido: 4-2
        Partido partido = new Partido();
        partido.setEquipoLocal(equipo1);
        partido.setEquipoVisitante(equipo2);
        partido.setGolesLocal(4);
        partido.setGolesVisitante(2);
        partido.setJornada(jornada);
        partidoRepository.save(partido);

        // Obtener clasificación
        List<ClasificacionEquipo> clasificacion = clasificacionService.obtenerClasificacionLiga(liga.getIdLigaCume());

        ClasificacionEquipo test = clasificacion.stream()
            .filter(c -> c.getNombreEquipo().equals("Equipo Test"))
            .findFirst()
            .orElse(null);

        assertNotNull(test);
        assertEquals(4, test.getGolesAFavor());
        assertEquals(2, test.getGolesEnContra());
        assertEquals(2, test.getDiferenciaGoles());
    }
}

