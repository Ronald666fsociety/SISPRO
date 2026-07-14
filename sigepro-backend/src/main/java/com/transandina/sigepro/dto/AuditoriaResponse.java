package com.transandina.sigepro.dto;

import com.transandina.sigepro.entity.Auditoria;
import java.time.LocalDateTime;

public class AuditoriaResponse {

    private Integer id;
    private Integer idUsuario;
    private String nombreUsuario;
    private String accion;
    private String entidad;
    private Integer idEntidad;
    private LocalDateTime fecha;

    public static AuditoriaResponse fromEntity(Auditoria a) {
        AuditoriaResponse dto = new AuditoriaResponse();
        dto.setId(a.getId());
        dto.setIdUsuario(a.getUsuario().getId());
        dto.setNombreUsuario(a.getUsuario().getNombre());
        dto.setAccion(a.getAccion());
        dto.setEntidad(a.getEntidad());
        dto.setIdEntidad(a.getIdEntidad());
        dto.setFecha(a.getFecha());
        return dto;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getEntidad() { return entidad; }
    public void setEntidad(String entidad) { this.entidad = entidad; }
    public Integer getIdEntidad() { return idEntidad; }
    public void setIdEntidad(Integer idEntidad) { this.idEntidad = idEntidad; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
