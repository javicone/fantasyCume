package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.repository.LigaCumeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LigaServiceTest {

    @Mock
    private LigaCumeRepository ligaCumeRepository;

    @InjectMocks
    private LigaService ligaService;

    private LigaCume ligaExistente;

    @BeforeEach
    void setUp() {
        ligaExistente = new LigaCume();
        ligaExistente.setIdLigaCume(1L);
        ligaExistente.setNombreLiga("Liga Cume 2024");
        ligaExistente.setPresupuestoMaximo(5000000L);
    }

    // ==================== TESTS PARA crearLiga ====================

    @Test
    void crearLiga_ok() {
        when(ligaCumeRepository.findByNombreLigaCume("Nueva Liga")).thenReturn(null);

        LigaCume ligaGuardada = new LigaCume();
        ligaGuardada.setIdLigaCume(10L);
        ligaGuardada.setNombreLiga("Nueva Liga");
        ligaGuardada.setPresupuestoMaximo(2000000L);
        when(ligaCumeRepository.save(any(LigaCume.class))).thenReturn(ligaGuardada);

        LigaCume result = ligaService.crearLiga("Nueva Liga", 2000000L);

        assertNotNull(result);
        assertEquals(10L, result.getIdLigaCume());
        assertEquals("Nueva Liga", result.getNombreLiga());
        assertEquals(2000000L, result.getPresupuestoMaximo());
        verify(ligaCumeRepository).save(any(LigaCume.class));
    }

    @Test
    void crearLiga_nombreDuplicado_debeFallar() {
        when(ligaCumeRepository.findByNombreLigaCume("Liga Cume 2024")).thenReturn(ligaExistente);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> ligaService.crearLiga("Liga Cume 2024", 3000000L));
        assertTrue(ex.getMessage().toLowerCase().contains("ya existe"));
        verify(ligaCumeRepository, never()).save(any(LigaCume.class));
    }

    @Test
    void crearLiga_presupuestoNulo_debeFallar() {
        when(ligaCumeRepository.findByNombreLigaCume("Liga Test")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> ligaService.crearLiga("Liga Test", null));
        assertTrue(ex.getMessage().toLowerCase().contains("presupuesto"));
        verify(ligaCumeRepository, never()).save(any(LigaCume.class));
    }

    @Test
    void crearLiga_presupuestoMenorAlMinimo_estableceMinimo() {
        when(ligaCumeRepository.findByNombreLigaCume("Liga Pequeña")).thenReturn(null);

        LigaCume ligaGuardada = new LigaCume();
        ligaGuardada.setIdLigaCume(15L);
        ligaGuardada.setNombreLiga("Liga Pequeña");
        ligaGuardada.setPresupuestoMaximo(1000000L);
        when(ligaCumeRepository.save(any(LigaCume.class))).thenReturn(ligaGuardada);

        LigaCume result = ligaService.crearLiga("Liga Pequeña", 500000L);

        assertNotNull(result);
        assertEquals(1000000L, result.getPresupuestoMaximo()); // Se establece el mínimo
    }

    @Test
    void crearLiga_presupuestoIgualAlMinimo_ok() {
        when(ligaCumeRepository.findByNombreLigaCume("Liga Mínima")).thenReturn(null);

        LigaCume ligaGuardada = new LigaCume();
        ligaGuardada.setIdLigaCume(16L);
        ligaGuardada.setNombreLiga("Liga Mínima");
        ligaGuardada.setPresupuestoMaximo(1000000L);
        when(ligaCumeRepository.save(any(LigaCume.class))).thenReturn(ligaGuardada);

        LigaCume result = ligaService.crearLiga("Liga Mínima", 1000000L);

        assertNotNull(result);
        assertEquals(1000000L, result.getPresupuestoMaximo());
    }

    @Test
    void crearLiga_presupuestoCero_estableceMinimo() {
        when(ligaCumeRepository.findByNombreLigaCume("Liga Cero")).thenReturn(null);

        LigaCume ligaGuardada = new LigaCume();
        ligaGuardada.setIdLigaCume(17L);
        ligaGuardada.setNombreLiga("Liga Cero");
        ligaGuardada.setPresupuestoMaximo(1000000L);
        when(ligaCumeRepository.save(any(LigaCume.class))).thenReturn(ligaGuardada);

        LigaCume result = ligaService.crearLiga("Liga Cero", 0L);

        assertNotNull(result);
        assertEquals(1000000L, result.getPresupuestoMaximo());
    }

    @Test
    void crearLiga_presupuestoNegativo_estableceMinimo() {
        when(ligaCumeRepository.findByNombreLigaCume("Liga Negativa")).thenReturn(null);

        LigaCume ligaGuardada = new LigaCume();
        ligaGuardada.setIdLigaCume(18L);
        ligaGuardada.setNombreLiga("Liga Negativa");
        ligaGuardada.setPresupuestoMaximo(1000000L);
        when(ligaCumeRepository.save(any(LigaCume.class))).thenReturn(ligaGuardada);

        LigaCume result = ligaService.crearLiga("Liga Negativa", -500000L);

        assertNotNull(result);
        assertEquals(1000000L, result.getPresupuestoMaximo());
    }

    @Test
    void crearLiga_presupuestoAlto_ok() {
        when(ligaCumeRepository.findByNombreLigaCume("Liga Premium")).thenReturn(null);

        LigaCume ligaGuardada = new LigaCume();
        ligaGuardada.setIdLigaCume(19L);
        ligaGuardada.setNombreLiga("Liga Premium");
        ligaGuardada.setPresupuestoMaximo(10000000L);
        when(ligaCumeRepository.save(any(LigaCume.class))).thenReturn(ligaGuardada);

        LigaCume result = ligaService.crearLiga("Liga Premium", 10000000L);

        assertNotNull(result);
        assertEquals(10000000L, result.getPresupuestoMaximo());
    }

    // ==================== TESTS PARA obtenerLiga ====================

    @Test
    void obtenerLiga_ok() {
        when(ligaCumeRepository.findByNombreLigaCume("Liga Cume 2024")).thenReturn(ligaExistente);

        LigaCume result = ligaService.obtenerLiga("Liga Cume 2024");

        assertNotNull(result);
        assertEquals(1L, result.getIdLigaCume());
        assertEquals("Liga Cume 2024", result.getNombreLiga());
    }

    @Test
    void obtenerLiga_noExiste_debeFallar() {
        when(ligaCumeRepository.findByNombreLigaCume("Liga Inexistente")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> ligaService.obtenerLiga("Liga Inexistente"));
        assertTrue(ex.getMessage().toLowerCase().contains("no encontrada"));
    }

    // ==================== TESTS PARA listarTodasLasLigas ====================

    @Test
    void listarTodasLasLigas_ok() {
        List<LigaCume> ligas = new ArrayList<>();
        ligas.add(ligaExistente);
        ligas.add(new LigaCume());
        ligas.add(new LigaCume());
        when(ligaCumeRepository.findAll()).thenReturn(ligas);

        List<LigaCume> result = ligaService.listarTodasLasLigas();

        assertEquals(3, result.size());
        verify(ligaCumeRepository).findAll();
    }

    @Test
    void listarTodasLasLigas_vacio_ok() {
        when(ligaCumeRepository.findAll()).thenReturn(new ArrayList<>());

        List<LigaCume> result = ligaService.listarTodasLasLigas();

        assertEquals(0, result.size());
    }

    // ==================== TESTS PARA modificarLiga ====================

    @Test
    void modificarLiga_ok() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));
        when(ligaCumeRepository.save(any(LigaCume.class))).thenAnswer(i -> i.getArgument(0));

        LigaCume result = ligaService.modificarLiga(1L, "Liga Modificada", 8000000L);

        assertEquals("Liga Modificada", result.getNombreLiga());
        assertEquals(8000000L, result.getPresupuestoMaximo());
        verify(ligaCumeRepository).save(any(LigaCume.class));
    }

    @Test
    void modificarLiga_ligaNoExiste_debeFallar() {
        when(ligaCumeRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> ligaService.modificarLiga(999L, "Nombre", 2000000L));
        assertTrue(ex.getMessage().toLowerCase().contains("no encontrada"));
        verify(ligaCumeRepository, never()).save(any(LigaCume.class));
    }

    @Test
    void modificarLiga_nombreNulo_debeFallar() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> ligaService.modificarLiga(1L, null, 2000000L));
        assertTrue(ex.getMessage().toLowerCase().contains("nulo"));
        verify(ligaCumeRepository, never()).save(any(LigaCume.class));
    }

    @Test
    void modificarLiga_presupuestoNulo_debeFallar() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> ligaService.modificarLiga(1L, "Nombre Valido", null));
        assertTrue(ex.getMessage().toLowerCase().contains("nulo"));
        verify(ligaCumeRepository, never()).save(any(LigaCume.class));
    }

    @Test
    void modificarLiga_ambosNulos_debeFallar() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> ligaService.modificarLiga(1L, null, null));
        assertTrue(ex.getMessage().toLowerCase().contains("nulo"));
        verify(ligaCumeRepository, never()).save(any(LigaCume.class));
    }

    @Test
    void modificarLiga_presupuestoNegativo_ok() {
        // El método no valida presupuesto negativo en modificarLiga
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));
        when(ligaCumeRepository.save(any(LigaCume.class))).thenAnswer(i -> i.getArgument(0));

        LigaCume result = ligaService.modificarLiga(1L, "Liga Test", -500000L);

        assertEquals(-500000L, result.getPresupuestoMaximo());
    }

    @Test
    void modificarLiga_presupuestoCero_ok() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));
        when(ligaCumeRepository.save(any(LigaCume.class))).thenAnswer(i -> i.getArgument(0));

        LigaCume result = ligaService.modificarLiga(1L, "Liga Cero", 0L);

        assertEquals(0L, result.getPresupuestoMaximo());
    }

    // ==================== TESTS PARA eliminarLiga ====================

    @Test
    void eliminarLiga_ok() {
        ligaService.eliminarLiga(1L);

        verify(ligaCumeRepository).deleteById(1L);
    }

    @Test
    void eliminarLiga_idNulo_noLanzaExcepcion() {
        // El método actual no valida ID nulo, simplemente llama a deleteById
        // El repository puede lanzar excepción
        ligaService.eliminarLiga(null);

        verify(ligaCumeRepository).deleteById(null);
    }

    @Test
    void eliminarLiga_idNoExistente_noLanzaExcepcion() {
        // El método actual no verifica si existe antes de eliminar
        ligaService.eliminarLiga(999L);

        verify(ligaCumeRepository).deleteById(999L);
    }

    @Test
    void eliminarLiga_multipleVeces_ok() {
        ligaService.eliminarLiga(5L);
        ligaService.eliminarLiga(6L);
        ligaService.eliminarLiga(7L);

        verify(ligaCumeRepository).deleteById(5L);
        verify(ligaCumeRepository).deleteById(6L);
        verify(ligaCumeRepository).deleteById(7L);
    }
}

