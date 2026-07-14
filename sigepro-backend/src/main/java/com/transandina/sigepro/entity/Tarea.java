package com.transandina.sigepro.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tarea")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarea")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyecto", nullable = false)
    private Proyecto proyecto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tarea_padre")
    private Tarea tareaPadre;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "porcentaje_avance")
    private Integer porcentajeAvance = 0;

    @Column(name = "presupuesto_estimado", precision = 12, scale = 2)
    private BigDecimal presupuestoEstimado = BigDecimal.ZERO;

    @Column(name = "costo_ejecutado", precision = 12, scale = 2)
    private BigDecimal costoEjecutado = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_responsable", nullable = false)
    private Usuario responsable;

    public Tarea() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Proyecto getProyecto() { return proyecto; }
    public void setProyecto(Proyecto proyecto) { this.proyecto = proyecto; }
    public Tarea getTareaPadre() { return tareaPadre; }
    public void setTareaPadre(Tarea tareaPadre) { this.tareaPadre = tareaPadre; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public Integer getPorcentajeAvance() { return porcentajeAvance; }
    public void setPorcentajeAvance(Integer porcentajeAvance) { this.porcentajeAvance = porcentajeAvance; }
    public BigDecimal getPresupuestoEstimado() { return presupuestoEstimado; }
    public void setPresupuestoEstimado(BigDecimal presupuestoEstimado) { this.presupuestoEstimado = presupuestoEstimado; }
    public BigDecimal getCostoEjecutado() { return costoEjecutado; }
    public void setCostoEjecutado(BigDecimal costoEjecutado) { this.costoEjecutado = costoEjecutado; }
    public Usuario getResponsable() { return responsable; }
    public void setResponsable(Usuario responsable) { this.responsable = responsable; }
}
