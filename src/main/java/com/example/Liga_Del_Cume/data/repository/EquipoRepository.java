package com.example.Liga_Del_Cume.data.repository;

import com.example.Liga_Del_Cume.data.model.Equipo;
import com.example.Liga_Del_Cume.data.model.LigaCume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    // Buscar equipo por nombre
    Equipo findByNombreEquipo(String nombreEquipo);

    List<Equipo> findByLigaIdLigaCume(Long ligaIdLigaCume);
    // Ignora mayúsculas/minúsculas
    Equipo findByNombreEquipoIgnoreCase(String nombreEquipo);

    // Búsqueda parcial (por ejemplo, "Real" devuelve "Real Cume", "Real Mérida"...)
    List<Equipo> findByNombreEquipoContainingIgnoreCase(String nombreParcial);

}

