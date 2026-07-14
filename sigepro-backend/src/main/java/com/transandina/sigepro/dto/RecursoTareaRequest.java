package com.transandina.sigepro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class RecursoTareaRequest {

    @NotNull(message = "La tarea es obligatoria")
    private Integer idTarea;

    @NotNull(message = "El usuario es obligatorio")
    private Integer idUsuario;

    @DecimalMin(value = "0.00", message = "Las horas estimadas no pueden ser negativas")
    private BigDecimal horasEstimadas;

    @DecimalMin(value = "0.00", message = "Las horas reales no pueden ser negativas")
    private BigDecimal horasReales;

    public Integer getIdTarea() { return idTarea; }
    public void setIdTarea(Integer idTarea) { this.idTarea = idTarea; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public BigDecimal getHorasEstimadas() { return horasEstimadas; }
    public void setHorasEstimadas(BigDecimal horasEstimadas) { this.horasEstimadas = horasEstimadas; }
    public BigDecimal getHorasReales() { return horasReales; }
    public void setHorasReales(BigDecimal horasReales) { this.horasReales = horasReales; }
}
