package com.transandina.sigepro.dto;

import com.transandina.sigepro.enums.EstadoProyecto;
import com.transandina.sigepro.validation.FechaCoherente;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@FechaCoherente(fechaInicio = "fechaInicio", fechaFin = "fechaFin", message = "La fecha de fin no puede ser anterior a la fecha de inicio")
public class ProyectoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no debe exceder 150 caracteres")
    private String nombre;

    private String descripcion;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    private EstadoProyecto estado;

    @NotNull(message = "El jefe de proyecto es obligatorio")
    private Integer idJefeProyecto;

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
    public Integer getIdJefeProyecto() { return idJefeProyecto; }
    public void setIdJefeProyecto(Integer idJefeProyecto) { this.idJefeProyecto = idJefeProyecto; }
}
