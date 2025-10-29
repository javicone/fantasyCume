package com.example.Liga_Del_Cume.data.repository;

import com.example.Liga_Del_Cume.data.model.EstadisticaJugadorPartido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EstadisticaJugadorPartidoRepository extends JpaRepository<EstadisticaJugadorPartido, Long> {
    // Obtenemos todas las estadisticas de un jugador
    List<EstadisticaJugadorPartido> findByJugadorNombreJugador(String nombreJugador);

    //
    // Obtenemos las estadisticas de todos los jugadores de un partido
    List<EstadisticaJugadorPartido> findByPartidoIdPartido(Long idPartido);
}

