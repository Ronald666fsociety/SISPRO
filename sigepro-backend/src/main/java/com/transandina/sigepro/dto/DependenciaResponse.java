package com.transandina.sigepro.dto;

import com.transandina.sigepro.entity.DependenciaTarea;

public class DependenciaResponse {

    private Integer id;
    private Integer idTareaOrigen;
    private String nombreTareaOrigen;
    private Integer idTareaDestino;
    private String nombreTareaDestino;
    private String tipo;

    public static DependenciaResponse fromEntity(DependenciaTarea dep) {
        DependenciaResponse dto = new DependenciaResponse();
        dto.setId(dep.getId());
        dto.setIdTareaOrigen(dep.getTareaOrigen().getId());
        dto.setNombreTareaOrigen(dep.getTareaOrigen().getNombre());
        dto.setIdTareaDestino(dep.getTareaDestino().getId());
        dto.setNombreTareaDestino(dep.getTareaDestino().getNombre());
        dto.setTipo(dep.getTipo().name());
        return dto;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIdTareaOrigen() { return idTareaOrigen; }
    public void setIdTareaOrigen(Integer idTareaOrigen) { this.idTareaOrigen = idTareaOrigen; }
    public String getNombreTareaOrigen() { return nombreTareaOrigen; }
    public void setNombreTareaOrigen(String nombreTareaOrigen) { this.nombreTareaOrigen = nombreTareaOrigen; }
    public Integer getIdTareaDestino() { return idTareaDestino; }
    public void setIdTareaDestino(Integer idTareaDestino) { this.idTareaDestino = idTareaDestino; }
    public String getNombreTareaDestino() { return nombreTareaDestino; }
    public void setNombreTareaDestino(String nombreTareaDestino) { this.nombreTareaDestino = nombreTareaDestino; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
