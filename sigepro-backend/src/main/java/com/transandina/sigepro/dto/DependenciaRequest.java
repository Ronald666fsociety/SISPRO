package com.transandina.sigepro.dto;

import com.transandina.sigepro.enums.TipoDependencia;
import jakarta.validation.constraints.NotNull;

public class DependenciaRequest {

    @NotNull(message = "La tarea origen es obligatoria")
    private Integer idTareaOrigen;

    @NotNull(message = "La tarea destino es obligatoria")
    private Integer idTareaDestino;

    private TipoDependencia tipo;

    public Integer getIdTareaOrigen() { return idTareaOrigen; }
    public void setIdTareaOrigen(Integer idTareaOrigen) { this.idTareaOrigen = idTareaOrigen; }
    public Integer getIdTareaDestino() { return idTareaDestino; }
    public void setIdTareaDestino(Integer idTareaDestino) { this.idTareaDestino = idTareaDestino; }
    public TipoDependencia getTipo() { return tipo; }
    public void setTipo(TipoDependencia tipo) { this.tipo = tipo; }
}
