package com.example.Liga_Del_Cume.data.repository;

import com.example.Liga_Del_Cume.data.model.EstadisticaJugadorPartido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EstadisticaJugadorPartidoRepository extends JpaRepository<EstadisticaJugadorPartido, Long> {
    // Buscar estadísticas por jugador
    List<EstadisticaJugadorPartido> findByJugadorIdJugador(Long idJugador);

    // Buscar estadísticas por partido
    List<EstadisticaJugadorPartido> findByPartidoIdPartido(Long idPartido);
}

