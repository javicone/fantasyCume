package com.example.Liga_Del_Cume.data.repository;

import com.example.Liga_Del_Cume.data.model.Alineacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlineacionRepository extends JpaRepository<Alineacion, Long> {
    // Buscar alineaciones por usuario
    List<Alineacion> findByUsuarioIdUsuario(Long idUsuario);
}

