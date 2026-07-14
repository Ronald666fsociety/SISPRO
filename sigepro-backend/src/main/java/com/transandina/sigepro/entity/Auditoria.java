package com.transandina.sigepro.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String accion;

    @Column(nullable = false, length = 50)
    private String entidad;

    @Column(name = "id_entidad")
    private Integer idEntidad;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    public Auditoria() {}

    public Auditoria(Usuario usuario, String accion, String entidad, Integer idEntidad) {
        this.usuario = usuario;
        this.accion = accion;
        this.entidad = entidad;
        this.idEntidad = idEntidad;
        this.fecha = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getEntidad() { return entidad; }
    public void setEntidad(String entidad) { this.entidad = entidad; }
    public Integer getIdEntidad() { return idEntidad; }
    public void setIdEntidad(Integer idEntidad) { this.idEntidad = idEntidad; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
