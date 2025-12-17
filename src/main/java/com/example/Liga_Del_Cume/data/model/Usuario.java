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
    private String email;
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Alineacion> alineaciones = new ArrayList<>();

    public Usuario() {}

    public Usuario(String nombreUsuario, int puntosAcumulados, LigaCume liga) {
        this.nombreUsuario = nombreUsuario;
        this.puntosAcumulados = puntosAcumulados;
        this.liga = liga;
    }

    public Usuario(String nombreUsuario, String email, String password) {
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.email = email;
        this.puntosAcumulados = 0;
        this.liga = null;
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
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(idUsuario, usuario.idUsuario);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", email='" + email + '\'' +
                ", puntosAcumulados=" + puntosAcumulados +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario);
    }
}
