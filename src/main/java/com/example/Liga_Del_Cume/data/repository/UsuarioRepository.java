package com.example.Liga_Del_Cume.data.repository;

import com.example.Liga_Del_Cume.data.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Buscar usuarios por liga
    List<Usuario> findByLigaIdLigaCume(Long idLiga);

    // Buscar usuario por nombre
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    // Buscar usuarios ordenados por puntos acumulados (ranking)
    List<Usuario> findByLigaIdLigaCumeOrderByPuntosAcumuladosDesc(Long idLiga);
}

