package com.example.Liga_Del_Cume.data.repository;

import com.example.Liga_Del_Cume.data.model.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {
    // Buscar partidos por jornada
    List<Partido> findByJornadaIdJornada(Long idJornada);

    // Buscar partidos de un equipo (como local o visitante)
    List<Partido> findByEquipoLocalIdEquipoOrEquipoVisitanteIdEquipo(Long idEquipoLocal, Long idEquipoVisitante);

    // Buscar partidos donde un equipo es local
    List<Partido> findByEquipoLocalIdEquipo(Long idEquipo);

    // Buscar partidos donde un equipo es visitante
    List<Partido> findByEquipoVisitanteIdEquipo(Long idEquipo);
}

