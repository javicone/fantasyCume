package com.example.Liga_Del_Cume.data.repository;

import com.example.Liga_Del_Cume.data.model.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {
    // Buscar jugadores por equipo
    List<Jugador> findByEquipoIdEquipo(Long idEquipo);

    // Buscar jugadores porteros
    List<Jugador> findByEsPortero(boolean esPortero);

    // Buscar jugadores por equipo y si es portero
    List<Jugador> findByEquipoIdEquipoAndEsPortero(Long idEquipo, boolean esPortero);
}

