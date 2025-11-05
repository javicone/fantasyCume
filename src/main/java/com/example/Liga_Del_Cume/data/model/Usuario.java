package com.example.Liga_Del_Cume.data.model;
import jakarta.persistence.*;
import java.util.*;

// Entidad que representa un usuario dentro de una liga
@Entity
public class Usuario {
    // Primary key para la entidad Usuario
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;
    
    // Relación N a 1: Muchos usuarios pertenecen a una liga
    @ManyToOne
    @JoinColumn(name = "liga_id")
    private LigaCume liga;

    private String nombreUsuario;
    private String password;
    private int puntosAcumulados;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Alineacion> alineaciones = new ArrayList<>();

    public Usuario() {}

    public Usuario(String nombreUsuario, int puntosAcumulados, LigaCume liga) {
        this.nombreUsuario = nombreUsuario;
        this.puntosAcumulados = puntosAcumulados;
        this.liga = liga;
    }

    // Getters y Setters
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public LigaCume getLiga() { return liga; }
    public void setLiga(LigaCume liga) { this.liga = liga; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getPuntosAcumulados() { return puntosAcumulados; }
    public void setPuntosAcumulados(int puntosAcumulados) { this.puntosAcumulados = puntosAcumulados; }
    public List<Alineacion> getAlineaciones() { return alineaciones; }
    public void setAlineaciones(List<Alineacion> alineaciones) { this.alineaciones = alineaciones; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return puntosAcumulados == usuario.puntosAcumulados && Objects.equals(idUsuario, usuario.idUsuario) && Objects.equals(liga, usuario.liga) && Objects.equals(nombreUsuario, usuario.nombreUsuario) && Objects.equals(password, usuario.password) && Objects.equals(alineaciones, usuario.alineaciones);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, liga, nombreUsuario, password, puntosAcumulados, alineaciones);
    }
}
