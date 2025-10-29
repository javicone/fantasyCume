package com.example.Liga_Del_Cume.data.repository;

import com.example.Liga_Del_Cume.data.model.Alineacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlineacionRepository extends JpaRepository<Alineacion, Long> {


    // obtener alineacion de usuario por jornada
    Alineacion findByUsuarioNombreUsuarioAndJornadaIdJornada(String nombreUsuario, Long idJornada);
    
    // Buscar alineaciones por usuario
    List<Alineacion> findByUsuarioIdUsuario(Long idUsuario);

    //Buscar alineaciones por nombre de usuario
    List<Alineacion> findByUsuarioNombreUsuario(String nombreUsuario);

    // Buscar alineaciones por jornada
    List<Alineacion> findByJornadaIdJornada(Long idJornada);

    // Buscar la alineación de un usuario en una jornada específica
    Optional<Alineacion> findByUsuarioIdUsuarioAndJornadaIdJornada(Long idUsuario, Long idJornada);

    // Buscar alineaciones de un usuario ordenadas por puntos descendente
    List<Alineacion> findByUsuarioIdUsuarioOrderByPuntosJornadaDesc(Long idUsuario);

    // Buscar las mejores alineaciones de una jornada (ordenadas por puntos)
    List<Alineacion> findByJornadaIdJornadaOrderByPuntosJornadaDesc(Long idJornada);


    // Verificar si un usuario ya tiene alineación en una jornada
    boolean existsByUsuarioIdUsuarioAndJornadaIdJornada(Long idUsuario, Long idJornada);


}
