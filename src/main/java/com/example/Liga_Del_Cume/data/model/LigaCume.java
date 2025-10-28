package com.example.Liga_Del_Cume.data.model;
import jakarta.persistence.*;
import java.util.*;

// Entidad principal que representa una liga. De aquí derivan usuarios, equipos y jornadas.
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class LigaCume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLigaCume;
    private Long presupuestoMaximo;
    // Relación 1 a N: Una liga tiene muchos equipos
    @OneToMany(mappedBy = "liga", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Equipo> equipos = new ArrayList<>();

    // Relación 1 a N: Una liga tiene muchos usuarios
    @OneToMany(mappedBy = "liga", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Usuario> usuarios = new ArrayList<>();

    // Relación 1 a N: Una liga tiene muchas jornadas
    @OneToMany(mappedBy = "liga", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Jornada> jornadas = new ArrayList<>();

    public LigaCume() {}

    // Getters y Setters
    public Long getPresupuestoMaximo() {
        return presupuestoMaximo;
    }
    public void setPresupuestoMaximo(Long presupuestoMaximo) {
        this.presupuestoMaximo = presupuestoMaximo;
    }
    public Long getIdLigaCume() { return idLigaCume; }
    public void setIdLigaCume(Long idLigaCume) { this.idLigaCume = idLigaCume; }
    public List<Equipo> getEquipos() { return equipos; }
    public void setEquipos(List<Equipo> equipos) { this.equipos = equipos; }
    public List<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Usuario> usuarios) { this.usuarios = usuarios; }
    public List<Jornada> getJornadas() { return jornadas; }
    public void setJornadas(List<Jornada> jornadas) { this.jornadas = jornadas; }
}
