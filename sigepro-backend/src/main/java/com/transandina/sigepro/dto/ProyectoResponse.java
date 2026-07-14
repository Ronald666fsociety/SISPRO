package com.transandina.sigepro.dto;

import com.transandina.sigepro.entity.Proyecto;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ProyectoResponse {

    private Integer id;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private Integer idJefeProyecto;
    private String nombreJefeProyecto;
    private BigDecimal presupuestoTotal;
    private BigDecimal costoRealTotal;
    private Boolean activo;

    public static ProyectoResponse fromEntity(Proyecto proyecto) {
        ProyectoResponse dto = new ProyectoResponse();
        dto.setId(proyecto.getId());
        dto.setNombre(proyecto.getNombre());
        dto.setDescripcion(proyecto.getDescripcion());
        dto.setFechaInicio(proyecto.getFechaInicio());
        dto.setFechaFin(proyecto.getFechaFin());
        dto.setEstado(proyecto.getEstado().name());
        dto.setIdJefeProyecto(proyecto.getJefeProyecto().getId());
        dto.setNombreJefeProyecto(proyecto.getJefeProyecto().getNombre());
        dto.setPresupuestoTotal(proyecto.getPresupuestoTotal());
        dto.setCostoRealTotal(proyecto.getCostoRealTotal());
        dto.setActivo(proyecto.getActivo());
        return dto;
    }

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
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Integer getIdJefeProyecto() { return idJefeProyecto; }
    public void setIdJefeProyecto(Integer idJefeProyecto) { this.idJefeProyecto = idJefeProyecto; }
    public String getNombreJefeProyecto() { return nombreJefeProyecto; }
    public void setNombreJefeProyecto(String nombreJefeProyecto) { this.nombreJefeProyecto = nombreJefeProyecto; }
    public BigDecimal getPresupuestoTotal() { return presupuestoTotal; }
    public void setPresupuestoTotal(BigDecimal presupuestoTotal) { this.presupuestoTotal = presupuestoTotal; }
    public BigDecimal getCostoRealTotal() { return costoRealTotal; }
    public void setCostoRealTotal(BigDecimal costoRealTotal) { this.costoRealTotal = costoRealTotal; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
