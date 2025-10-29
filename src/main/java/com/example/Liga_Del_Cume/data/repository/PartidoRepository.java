package com.example.Liga_Del_Cume.data.repository;

import com.example.Liga_Del_Cume.data.model.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {
    // Buscar partidos por jornada
    List<Partido> findByJornadaIdJornada(Long idJornada);

    // Buscar partidos de un equipo (como local o visitante) por nombre de equipo,
    // NOTA/JUSTIFIACION: Se deben pasar dos veces el mismo nombre de equipo, PQ INTERNAMENTE INTERPRETA QUE TIENE DOS PARAMETROS DISTINTOS
    //
    List<Partido> findByEquipoLocalNombreEquipoOrEquipoVisitanteNombreEquipo(String nombreEquipo, String nombreEquipo2);

    // Buscar partidos donde un equipo es local
    List<Partido> findByEquipoLocalNombreEquipo(String nombreEquipo);

    // Buscar partidos donde un equipo es visitante
    List<Partido> findByEquipoVisitanteNombreEquipo(String nombreEquipo);

    // Partidos ganados por un equipo
    @Query("SELECT p FROM Partido p " +
            "WHERE (p.equipoLocal.nombreEquipo = :nombreEquipo AND p.golesLocal > p.golesVisitante) " +
            "   OR (p.equipoVisitante.nombreEquipo = :nombreEquipo AND p.golesVisitante > p.golesLocal)")
    List<Partido> findPartidosGanados(@Param("nombreEquipo") String nombreEquipo);

    // Partidos perdidos por un equipo
    @Query("SELECT p FROM Partido p " +
            "WHERE (p.equipoLocal.nombreEquipo = :nombreEquipo AND p.golesLocal < p.golesVisitante) " +
            "   OR (p.equipoVisitante.nombreEquipo = :nombreEquipo AND p.golesVisitante < p.golesLocal)")
    List<Partido> findPartidosPerdidos(@Param("nombreEquipo") String nombreEquipo);

    // Partidos empatados
    @Query("SELECT p FROM Partido p " +
            "WHERE (p.equipoLocal.nombreEquipo = :nombreEquipo OR p.equipoVisitante.nombreEquipo = :nombreEquipo) " +
            "   AND p.golesLocal = p.golesVisitante")
    List<Partido> findPartidosEmpatados(@Param("nombreEquipo") String nombreEquipo);

 

}

