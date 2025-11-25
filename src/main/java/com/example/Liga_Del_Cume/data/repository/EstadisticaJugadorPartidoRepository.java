package com.example.Liga_Del_Cume.data.repository;

import com.example.Liga_Del_Cume.data.model.EstadisticaJugadorPartido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EstadisticaJugadorPartidoRepository extends JpaRepository<EstadisticaJugadorPartido, Long> {
    // Obtenemos todas las estadisticas de un jugador
    List<EstadisticaJugadorPartido> findByJugadorNombreJugador(String nombreJugador);

    // Obtenemos todas las estadisticas de un jugador por su ID
    List<EstadisticaJugadorPartido> findByJugadorIdJugador(Long idJugador);
    //
    // Obtenemos las estadisticas de todos los jugadores de un partido
    List<EstadisticaJugadorPartido> findByPartidoIdPartido(Long idPartido);

    // Obtenemos las estadisticas de un jugador en un partido específico
    EstadisticaJugadorPartido findByJugadorIdJugadorAndPartidoJornadaIdJornada(Long idJugador, Long idJornada);

    //Encontrar estadistica por jugador y partido
    EstadisticaJugadorPartido findByJugadorIdJugadorAndPartidoIdPartido(Long idJugador, Long idPartido);

    // Obtenemos todas las estadisticas de una jornada
    List<EstadisticaJugadorPartido> findByPartidoJornadaIdJornada(Long idJornada);

}

