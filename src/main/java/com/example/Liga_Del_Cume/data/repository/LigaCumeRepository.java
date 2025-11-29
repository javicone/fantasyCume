package com.example.Liga_Del_Cume.data.repository;
import com.example.Liga_Del_Cume.data.model.LigaCume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LigaCumeRepository extends JpaRepository<LigaCume, Long> {
    // Buscar liga por nombre
    LigaCume findByNombreLigaCume(String nombreLigaCume);

    //buscar ligas por usuario


}