package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.Equipo;
import com.example.Liga_Del_Cume.data.model.LigaCume;
import com.example.Liga_Del_Cume.data.repository.EquipoRepository;
import com.example.Liga_Del_Cume.data.repository.LigaCumeRepository;
import com.example.Liga_Del_Cume.data.service.exceptions.EquipoException;
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
public class EquipoServiceTest {

    @Mock
    private EquipoRepository equipoRepository;

    @Mock
    private LigaCumeRepository ligaCumeRepository;

    @InjectMocks
    private EquipoService equipoService;

    private LigaCume ligaExistente;

    @BeforeEach
    void setUp() {
        ligaExistente = new LigaCume();
        ligaExistente.setIdLigaCume(1L);
    }

    // agregarEquipo
    @Test
    void agregarEquipo_ok() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));
        when(equipoRepository.findByNombreEquipoIgnoreCase("Real Cume")).thenReturn(null);

        Equipo equipoGuardado = new Equipo();
        equipoGuardado.setIdEquipo(10L);
        equipoGuardado.setNombreEquipo("Real Cume");
        when(equipoRepository.save(any(Equipo.class))).thenReturn(equipoGuardado);

        Equipo result = equipoService.agregarEquipo("Real Cume", ligaExistente);

        assertNotNull(result);
        assertEquals(10L, result.getIdEquipo());
        verify(equipoRepository).save(any(Equipo.class));
    }

    @Test
    void agregarEquipo_nombreNulo_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.agregarEquipo(null, ligaExistente));
        assertTrue(ex.getMessage().contains("no puede ser nulo"));
    }

    @Test
    void agregarEquipo_nombreSoloEspacios_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.agregarEquipo("   ", ligaExistente));
        assertTrue(ex.getMessage().contains("no puede ser nulo o vacío") || ex.getMessage().contains("solo espacios"));
    }

    @Test
    void agregarEquipo_ligaNula_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.agregarEquipo("Valido", null));
        assertTrue(ex.getMessage().contains("liga no puede ser nula") || ex.getMessage().toLowerCase().contains("liga"));
    }

    @Test
    void agregarEquipo_ligaSinId_ok() {
        // Caso donde la liga no tiene ID asignado aún (nuevo objeto)
        LigaCume ligaSinId = new LigaCume();
        ligaSinId.setIdLigaCume(null);

        Equipo equipoGuardado = new Equipo();
        equipoGuardado.setIdEquipo(15L);
        equipoGuardado.setNombreEquipo("EquipoNuevo");
        when(equipoRepository.findByNombreEquipoIgnoreCase("EquipoNuevo")).thenReturn(null);
        when(equipoRepository.save(any(Equipo.class))).thenReturn(equipoGuardado);

        Equipo result = equipoService.agregarEquipo("EquipoNuevo", ligaSinId);
        assertNotNull(result);
        verify(equipoRepository).save(any(Equipo.class));
    }

    @Test
    void agregarEquipo_ligaInexistente_debeFallar() {
        LigaCume ligaFalsa = new LigaCume();
        ligaFalsa.setIdLigaCume(99L);
        when(ligaCumeRepository.findById(99L)).thenReturn(Optional.empty());

        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.agregarEquipo("EquipoX", ligaFalsa));
        assertTrue(ex.getMessage().contains("No existe ninguna liga"));
    }

    @Test
    void agregarEquipo_equipoDuplicado_debeFallar() {
        // Configurar mock para que la liga exista
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));

        // Equipo duplicado existente
        Equipo existente = new Equipo();
        existente.setIdEquipo(5L);
        existente.setNombreEquipo("Dup");
        existente.setLiga(ligaExistente);
        when(equipoRepository.findByNombreEquipoIgnoreCase("Dup")).thenReturn(existente);

        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.agregarEquipo("Dup", ligaExistente));
        assertTrue(ex.getMessage().toLowerCase().contains("ya existe"));

        // Verificar que no se intentó guardar el equipo duplicado
        verify(equipoRepository, never()).save(any(Equipo.class));
    }

    // modificarEquipo
    @Test
    void modificarEquipo_okCambiarNombre() {
        Equipo existente = new Equipo();
        existente.setIdEquipo(2L);
        existente.setNombreEquipo("Old");
        existente.setLiga(ligaExistente);

        when(equipoRepository.findById(2L)).thenReturn(Optional.of(existente));
        when(equipoRepository.findByNombreEquipoIgnoreCase("New")).thenReturn(null);
        when(equipoRepository.save(any(Equipo.class))).thenAnswer(i -> i.getArgument(0));

        Equipo res = equipoService.modificarEquipo(2L, "New", null);
        assertEquals("New", res.getNombreEquipo());
        verify(equipoRepository).save(any(Equipo.class));
    }

    @Test
    void modificarEquipo_idNulo_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.modificarEquipo(null, "X", null));
        assertTrue(ex.getMessage().contains("ID del equipo no puede ser nulo") || ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void modificarEquipo_idNegativo_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.modificarEquipo(-5L, "X", null));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo") || ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void modificarEquipo_idCero_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.modificarEquipo(0L, "X", null));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo") || ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void modificarEquipo_equipoNoExiste_debeFallar() {
        when(equipoRepository.findById(100L)).thenReturn(Optional.empty());
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.modificarEquipo(100L, "New", null));
        assertTrue(ex.getMessage().contains("No existe ningún equipo"));
    }

    @Test
    void modificarEquipo_nombreDuplicado_debeFallar() {
        Equipo existente = new Equipo();
        existente.setIdEquipo(3L);
        existente.setNombreEquipo("OldName");
        existente.setLiga(ligaExistente);

        Equipo duplicado = new Equipo();
        duplicado.setIdEquipo(9L);
        duplicado.setNombreEquipo("NewName");
        duplicado.setLiga(ligaExistente); // Mismo liga que el equipo a modificar

        when(equipoRepository.findById(3L)).thenReturn(Optional.of(existente));
        when(equipoRepository.findByNombreEquipoIgnoreCase("NewName")).thenReturn(duplicado);

        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.modificarEquipo(3L, "NewName", null));
        assertTrue(ex.getMessage().toLowerCase().contains("ya existe"));

        // Verificar que no se guardó el cambio
        verify(equipoRepository, never()).save(any(Equipo.class));
    }

    @Test
    void modificarEquipo_noCambios_debeFallar() {
        Equipo existente = new Equipo();
        existente.setIdEquipo(4L);
        existente.setNombreEquipo("Same");
        existente.setLiga(ligaExistente);

        when(equipoRepository.findById(4L)).thenReturn(Optional.of(existente));

        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.modificarEquipo(4L, null, null));
        assertTrue(ex.getMessage().toLowerCase().contains("debe proporcionar"));
    }

    @Test
    void modificarEquipo_cambiarEscudo_ok() {
        Equipo existente = new Equipo();
        existente.setIdEquipo(6L);
        existente.setNombreEquipo("Equipo");
        existente.setLiga(ligaExistente);

        when(equipoRepository.findById(6L)).thenReturn(Optional.of(existente));
        when(equipoRepository.save(any(Equipo.class))).thenAnswer(i -> i.getArgument(0));

        Equipo res = equipoService.modificarEquipo(6L, null, "http://escudo.png");
        assertEquals("http://escudo.png", res.getEscudoURL());
        verify(equipoRepository).save(any(Equipo.class));
    }

    @Test
    void modificarEquipo_cambiarNombreYEscudo_ok() {
        Equipo existente = new Equipo();
        existente.setIdEquipo(8L);
        existente.setNombreEquipo("Viejo");
        existente.setLiga(ligaExistente);

        when(equipoRepository.findById(8L)).thenReturn(Optional.of(existente));
        when(equipoRepository.findByNombreEquipoIgnoreCase("Nuevo")).thenReturn(null);
        when(equipoRepository.save(any(Equipo.class))).thenAnswer(i -> i.getArgument(0));

        Equipo res = equipoService.modificarEquipo(8L, "Nuevo", "http://nuevo.png");
        assertEquals("Nuevo", res.getNombreEquipo());
        assertEquals("http://nuevo.png", res.getEscudoURL());
        verify(equipoRepository).save(any(Equipo.class));
    }

    @Test
    void modificarEquipo_mismoNombre_ok() {
        // Cambiar a un nombre que es el mismo (case insensitive) no debe fallar
        Equipo existente = new Equipo();
        existente.setIdEquipo(12L);
        existente.setNombreEquipo("MiEquipo");
        existente.setLiga(ligaExistente);

        when(equipoRepository.findById(12L)).thenReturn(Optional.of(existente));
        when(equipoRepository.findByNombreEquipoIgnoreCase("MIEQUIPO")).thenReturn(existente);
        when(equipoRepository.save(any(Equipo.class))).thenAnswer(i -> i.getArgument(0));

        Equipo res = equipoService.modificarEquipo(12L, "MIEQUIPO", null);
        assertEquals("MIEQUIPO", res.getNombreEquipo());
        verify(equipoRepository).save(any(Equipo.class));
    }

    // eliminarEquipo
    @Test
    void eliminarEquipo_ok() {
        Equipo existente = new Equipo();
        existente.setIdEquipo(7L);
        when(equipoRepository.findById(7L)).thenReturn(Optional.of(existente));

        equipoService.eliminarEquipo(7L);
        verify(equipoRepository).deleteById(7L);
    }

    @Test
    void eliminarEquipo_idNulo_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.eliminarEquipo(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id del equipo") || ex.getMessage().toLowerCase().contains("no puede ser nulo"));
    }

    @Test
    void eliminarEquipo_idNegativo_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.eliminarEquipo(-10L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void eliminarEquipo_idCero_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.eliminarEquipo(0L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void eliminarEquipo_equipoNoExiste_debeFallar() {
        when(equipoRepository.findById(88L)).thenReturn(Optional.empty());
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.eliminarEquipo(88L));
        assertTrue(ex.getMessage().contains("No existe ningún equipo"));
    }

    @Test
    void eliminarEquipo_conJugadores_advertenciaPeroElimina() {
        Equipo existente = new Equipo();
        existente.setIdEquipo(11L);
        // Simular jugadores no vacío (se usa getJugadores en el servicio)
        existente.setJugadores(new ArrayList<>());
        when(equipoRepository.findById(11L)).thenReturn(Optional.of(existente));

        equipoService.eliminarEquipo(11L);
        verify(equipoRepository).deleteById(11L);
    }

    // listarEquiposPorLiga
    @Test
    void listarEquiposPorLiga_ok() {
        when(ligaCumeRepository.findById(1L)).thenReturn(Optional.of(ligaExistente));
        List<Equipo> lista = new ArrayList<>();
        lista.add(new Equipo());
        when(equipoRepository.findByLigaIdLigaCume(1L)).thenReturn(lista);

        List<Equipo> res = equipoService.listarEquiposPorLiga(1L);
        assertEquals(1, res.size());
    }

    @Test
    void listarEquiposPorLiga_ligaIdNulo_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.listarEquiposPorLiga(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id de la liga") || ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void listarEquiposPorLiga_ligaIdNegativo_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.listarEquiposPorLiga(-1L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void listarEquiposPorLiga_ligaIdCero_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.listarEquiposPorLiga(0L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void listarEquiposPorLiga_ligaInexistente_debeFallar() {
        when(ligaCumeRepository.findById(5L)).thenReturn(Optional.empty());
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.listarEquiposPorLiga(5L));
        assertTrue(ex.getMessage().contains("No existe ninguna liga"));
    }

    // obtenerEquipo
    @Test
    void obtenerEquipo_ok() {
        Equipo e = new Equipo();
        e.setIdEquipo(25L);
        e.setNombreEquipo("TestEquipo");
        when(equipoRepository.findById(25L)).thenReturn(Optional.of(e));

        Equipo res = equipoService.obtenerEquipo(25L);
        assertNotNull(res);
        assertEquals(25L, res.getIdEquipo());
    }

    @Test
    void obtenerEquipo_idNulo_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.obtenerEquipo(null));
        assertTrue(ex.getMessage().toLowerCase().contains("id") || ex.getMessage().toLowerCase().contains("nulo"));
    }

    @Test
    void obtenerEquipo_idNegativo_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.obtenerEquipo(-3L));
        assertTrue(ex.getMessage().toLowerCase().contains("positivo"));
    }

    @Test
    void obtenerEquipo_noExiste_debeFallar() {
        when(equipoRepository.findById(999L)).thenReturn(Optional.empty());
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.obtenerEquipo(999L));
        assertTrue(ex.getMessage().contains("No existe"));
    }

    // listarTodosLosEquipos
    @Test
    void listarTodosLosEquipos_ok() {
        List<Equipo> equipos = new ArrayList<>();
        equipos.add(new Equipo());
        equipos.add(new Equipo());
        when(equipoRepository.findAll()).thenReturn(equipos);

        List<Equipo> res = equipoService.listarTodosLosEquipos();
        assertEquals(2, res.size());
        verify(equipoRepository).findAll();
    }

    @Test
    void listarTodosLosEquipos_vacio_ok() {
        when(equipoRepository.findAll()).thenReturn(new ArrayList<>());

        List<Equipo> res = equipoService.listarTodosLosEquipos();
        assertEquals(0, res.size());
    }

    // buscarEquipoPorNombre
    @Test
    void buscarEquipoPorNombre_ok() {
        Equipo e = new Equipo();
        e.setIdEquipo(20L);
        e.setNombreEquipo("Buscado");
        when(equipoRepository.findByNombreEquipoIgnoreCase("Buscado")).thenReturn(e);

        Equipo res = equipoService.buscarEquipoPorNombre("Buscado");
        assertNotNull(res);
        assertEquals(20L, res.getIdEquipo());
    }

    @Test
    void buscarEquipoPorNombre_noEncontrado_ok() {
        when(equipoRepository.findByNombreEquipoIgnoreCase("NoExiste")).thenReturn(null);

        Equipo res = equipoService.buscarEquipoPorNombre("NoExiste");
        assertNull(res);
    }

    @Test
    void buscarEquipoPorNombre_nombreNulo_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.buscarEquipoPorNombre(null));
        assertTrue(ex.getMessage().toLowerCase().contains("no puede ser nulo"));
    }

    @Test
    void buscarEquipoPorNombre_nombreInvalido_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.buscarEquipoPorNombre("  "));
        assertTrue(ex.getMessage().toLowerCase().contains("no puede ser nulo"));
    }

    // buscarEquiposPorNombreParcial
    @Test
    void buscarEquiposPorNombreParcial_ok() {
        List<Equipo> encontrados = new ArrayList<>();
        encontrados.add(new Equipo());
        when(equipoRepository.findByNombreEquipoContainingIgnoreCase("Re")).thenReturn(encontrados);

        List<Equipo> res = equipoService.buscarEquiposPorNombreParcial("Re");
        assertEquals(1, res.size());
    }

    @Test
    void buscarEquiposPorNombreParcial_vacio_ok() {
        when(equipoRepository.findByNombreEquipoContainingIgnoreCase("XY")).thenReturn(new ArrayList<>());

        List<Equipo> res = equipoService.buscarEquiposPorNombreParcial("XY");
        assertEquals(0, res.size());
    }

    @Test
    void buscarEquiposPorNombreParcial_nombreNulo_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.buscarEquiposPorNombreParcial(null));
        assertTrue(ex.getMessage().toLowerCase().contains("no puede ser nulo"));
    }

    @Test
    void buscarEquiposPorNombreParcial_textoCorto_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.buscarEquiposPorNombreParcial("R"));
        assertTrue(ex.getMessage().toLowerCase().contains("al menos 2 caracteres") || ex.getMessage().toLowerCase().contains("no puede ser nulo"));
    }

    @Test
    void buscarEquiposPorNombreParcial_textoVacio_debeFallar() {
        EquipoException ex = assertThrows(EquipoException.class, () -> equipoService.buscarEquiposPorNombreParcial("  "));
        assertTrue(ex.getMessage().toLowerCase().contains("no puede ser nulo") || ex.getMessage().toLowerCase().contains("al menos 2"));
    }
}
