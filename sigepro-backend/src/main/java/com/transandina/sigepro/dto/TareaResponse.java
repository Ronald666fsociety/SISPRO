package com.transandina.sigepro.dto;

import com.transandina.sigepro.entity.Tarea;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TareaResponse {

    private Integer id;
    private Integer idProyecto;
    private String nombreProyecto;
    private Integer idTareaPadre;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer porcentajeAvance;
    private BigDecimal presupuestoEstimado;
    private BigDecimal costoEjecutado;
    private Integer idResponsable;
    private String nombreResponsable;

    public static TareaResponse fromEntity(Tarea tarea) {
        TareaResponse dto = new TareaResponse();
        dto.setId(tarea.getId());
        dto.setIdProyecto(tarea.getProyecto().getId());
        dto.setNombreProyecto(tarea.getProyecto().getNombre());
        dto.setIdTareaPadre(tarea.getTareaPadre() != null ? tarea.getTareaPadre().getId() : null);
        dto.setNombre(tarea.getNombre());
        dto.setFechaInicio(tarea.getFechaInicio());
        dto.setFechaFin(tarea.getFechaFin());
        dto.setPorcentajeAvance(tarea.getPorcentajeAvance());
        dto.setPresupuestoEstimado(tarea.getPresupuestoEstimado());
        dto.setCostoEjecutado(tarea.getCostoEjecutado());
        dto.setIdResponsable(tarea.getResponsable().getId());
        dto.setNombreResponsable(tarea.getResponsable().getNombre());
        return dto;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIdProyecto() { return idProyecto; }
    public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }
    public String getNombreProyecto() { return nombreProyecto; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }
    public Integer getIdTareaPadre() { return idTareaPadre; }
    public void setIdTareaPadre(Integer idTareaPadre) { this.idTareaPadre = idTareaPadre; }
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
    public Integer getIdResponsable() { return idResponsable; }
    public void setIdResponsable(Integer idResponsable) { this.idResponsable = idResponsable; }
    public String getNombreResponsable() { return nombreResponsable; }
    public void setNombreResponsable(String nombreResponsable) { this.nombreResponsable = nombreResponsable; }
}
