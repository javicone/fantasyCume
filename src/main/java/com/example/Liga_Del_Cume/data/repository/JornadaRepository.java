package com.example.Liga_Del_Cume.data.repository;

import com.example.Liga_Del_Cume.data.model.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JornadaRepository extends JpaRepository<Jornada, Long> {
    // Buscar jornadas por liga
    List<Jornada> findByLigaIdLigaCume(Long idLiga);

    // Buscar jornadas ordenadas por ID
    List<Jornada> findByLigaIdLigaCumeOrderByIdJornadaAsc(Long idLiga);
}
