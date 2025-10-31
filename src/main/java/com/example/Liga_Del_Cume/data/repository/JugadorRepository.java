package com.example.Liga_Del_Cume.data.repository;

import com.example.Liga_Del_Cume.data.model.Equipo;
import com.example.Liga_Del_Cume.data.model.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {
    // Buscar jugadores por equipo
    List<Jugador> findByEquipoNombreEquipo(String nombreEquipo);

    //BUSCAR JUGADORES POR EQUIPO ORDENADOS POR NOMBRE
    List<Jugador> findByEquipoIdEquipo(Long idEquipo);

    // Buscar jugador por nombre exacto
    Jugador findByNombreJugador(String nombreJugador);

    //Buscar jugador por nombre parcial
    List<Jugador> findByNombreJugadorContainingIgnoreCase(String nombreParcial);


    //Buscar jugadores ordenados por goles descendentes
    @Query("SELECT j FROM Jugador j JOIN j.estadisticas e GROUP BY j ORDER BY SUM(e.golesAnotados) DESC")
    List<Jugador> findJugadoresOrdenadosPorGoles();

    //

    // Obtener totales básicos de un jugador para calcular puntuaciones totales.
    @Query("SELECT " +
            " SUM(CASE WHEN e.minMinutosJugados = true THEN 1 ELSE 0 END)," +
            " SUM(e.tarjetaAmarillas)," +
            " SUM(CASE WHEN e.tarjetaRojas = true THEN 1 ELSE 0 END)," +
            " SUM(e.golesAnotados)," +
            " SUM(e.asistencias), " +
            " SUM(e.golesRecibidos)" +
            " FROM EstadisticaJugadorPartido e" +
            " WHERE e.jugador.nombreJugador = :nombreJugador")
    Object[] obtenerEstadisticasTotalesJugador(@Param("nombreJugador") String nombreJugador);

    //obtener totales basicos de un jugador dada una jornada
    @Query("SELECT e.golesAnotados," +
            " e.asistencias," +
            " e.tarjetaRojas," +
            " e.tarjetaAmarillas," +
            " e.golesRecibidos," +
            " e.minMinutosJugados" +
            " FROM EstadisticaJugadorPartido e " +
            " WHERE e.jugador.nombreJugador = :nombreJugador " +
            " AND e.partido.jornada.idJornada = :numeroJornada")
    Object[] obtenerEstadisticaPorJornadaDeJugador(@Param("nombreJugador") String nombre, @Param("idJornada") int numeroJornada);


    //Buscar jugadores ordenados por precio de mercado ascendente
    List<Jugador> findAllByOrderByPrecioMercadoAsc();

    //Buscar jugadores ordenados por precio de mercado descendente
    List<Jugador> findAllByOrderByPrecioMercadoDesc();

    // Buscar jugadores porteros
    List<Jugador> findByEsPortero(boolean esPortero);

    //buscar jugadores porteros ordenados por precio de mercado descendente
    List<Jugador> findByEsPorteroOrderByPrecioMercadoDesc(boolean esPortero);

    //buscar jugadores porteros ordenados por precio de mercado ascendente
    List<Jugador> findByEsPorteroOrderByPrecioMercadoAsc(boolean esPortero);

}

