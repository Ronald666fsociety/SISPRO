package com.transandina.sigepro.dto;

import com.transandina.sigepro.entity.RecursoTarea;
import java.math.BigDecimal;

public class RecursoTareaResponse {

    private Integer id;
    private Integer idTarea;
    private String nombreTarea;
    private Integer idUsuario;
    private String nombreUsuario;
    private BigDecimal horasEstimadas;
    private BigDecimal horasReales;

    public static RecursoTareaResponse fromEntity(RecursoTarea rt) {
        RecursoTareaResponse dto = new RecursoTareaResponse();
        dto.setId(rt.getId());
        dto.setIdTarea(rt.getTarea().getId());
        dto.setNombreTarea(rt.getTarea().getNombre());
        dto.setIdUsuario(rt.getUsuario().getId());
        dto.setNombreUsuario(rt.getUsuario().getNombre());
        dto.setHorasEstimadas(rt.getHorasEstimadas());
        dto.setHorasReales(rt.getHorasReales());
        return dto;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIdTarea() { return idTarea; }
    public void setIdTarea(Integer idTarea) { this.idTarea = idTarea; }
    public String getNombreTarea() { return nombreTarea; }
    public void setNombreTarea(String nombreTarea) { this.nombreTarea = nombreTarea; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public BigDecimal getHorasEstimadas() { return horasEstimadas; }
    public void setHorasEstimadas(BigDecimal horasEstimadas) { this.horasEstimadas = horasEstimadas; }
    public BigDecimal getHorasReales() { return horasReales; }
    public void setHorasReales(BigDecimal horasReales) { this.horasReales = horasReales; }
}
