package com.transandina.sigepro.entity;

import com.transandina.sigepro.enums.EstadoProyecto;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "proyecto")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyecto")
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProyecto estado = EstadoProyecto.PLANIFICADO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jefe_proyecto", nullable = false)
    private Usuario jefeProyecto;

    @Column(name = "presupuesto_total", precision = 12, scale = 2)
    private BigDecimal presupuestoTotal = BigDecimal.ZERO;

    @Column(name = "costo_real_total", precision = 12, scale = 2)
    private BigDecimal costoRealTotal = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean activo = true;

    public Proyecto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public EstadoProyecto getEstado() { return estado; }
    public void setEstado(EstadoProyecto estado) { this.estado = estado; }
    public Usuario getJefeProyecto() { return jefeProyecto; }
    public void setJefeProyecto(Usuario jefeProyecto) { this.jefeProyecto = jefeProyecto; }
    public BigDecimal getPresupuestoTotal() { return presupuestoTotal; }
    public void setPresupuestoTotal(BigDecimal presupuestoTotal) { this.presupuestoTotal = presupuestoTotal; }
    public BigDecimal getCostoRealTotal() { return costoRealTotal; }
    public void setCostoRealTotal(BigDecimal costoRealTotal) { this.costoRealTotal = costoRealTotal; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
