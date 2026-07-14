package com.transandina.sigepro.dto;

import com.transandina.sigepro.validation.FechaCoherente;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@FechaCoherente(fechaInicio = "fechaInicio", fechaFin = "fechaFin", message = "La fecha de fin no puede ser anterior a la fecha de inicio")
public class TareaRequest {

    @NotNull(message = "El proyecto es obligatorio")
    private Integer idProyecto;

    private Integer idTareaPadre;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no debe exceder 150 caracteres")
    private String nombre;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @Min(value = 0, message = "El porcentaje de avance no puede ser negativo")
    @Max(value = 100, message = "El porcentaje de avance no puede superar 100")
    private Integer porcentajeAvance;

    @DecimalMin(value = "0.00", message = "El presupuesto estimado no puede ser negativo")
    private BigDecimal presupuestoEstimado;

    @DecimalMin(value = "0.00", message = "El costo ejecutado no puede ser negativo")
    private BigDecimal costoEjecutado;

    @NotNull(message = "El responsable es obligatorio")
    private Integer idResponsable;

    public Integer getIdProyecto() { return idProyecto; }
    public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }
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
}
