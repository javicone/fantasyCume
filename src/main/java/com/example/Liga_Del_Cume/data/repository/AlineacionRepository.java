package com.example.Liga_Del_Cume.data.repository;

import com.example.Liga_Del_Cume.data.model.Alineacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlineacionRepository extends JpaRepository<Alineacion, Long> {
    // Buscar alineaciones por usuario
    List<Alineacion> findByUsuarioIdUsuario(Long idUsuario);

    // Buscar alineaciones por jornada
    List<Alineacion> findByJornadaIdJornada(Long idJornada);

    // Buscar la alineación de un usuario en una jornada específica
    Optional<Alineacion> findByUsuarioIdUsuarioAndJornadaIdJornada(Long idUsuario, Long idJornada);

    // Buscar alineaciones de un usuario ordenadas por puntos descendente
    List<Alineacion> findByUsuarioIdUsuarioOrderByPuntosJornadaDesc(Long idUsuario);

    // Buscar las mejores alineaciones de una jornada (ordenadas por puntos)
    List<Alineacion> findByJornadaIdJornadaOrderByPuntosJornadaDesc(Long idJornada);

    // Buscar alineaciones con puntos superiores a un valor
    List<Alineacion> findByPuntosJornadaGreaterThanEqual(int puntos);

    // Verificar si un usuario ya tiene alineación en una jornada
    boolean existsByUsuarioIdUsuarioAndJornadaIdJornada(Long idUsuario, Long idJornada);

    // Contar alineaciones de un usuario
    long countByUsuarioIdUsuario(Long idUsuario);
}
