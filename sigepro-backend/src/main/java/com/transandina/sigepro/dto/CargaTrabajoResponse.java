package com.transandina.sigepro.dto;

import java.math.BigDecimal;

public class CargaTrabajoResponse {

    private Integer idUsuario;
    private String nombreUsuario;
    private String email;
    private BigDecimal totalHorasEstimadas;
    private BigDecimal totalHorasReales;
    private Long cantidadTareas;

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public BigDecimal getTotalHorasEstimadas() { return totalHorasEstimadas; }
    public void setTotalHorasEstimadas(BigDecimal totalHorasEstimadas) { this.totalHorasEstimadas = totalHorasEstimadas; }
    public BigDecimal getTotalHorasReales() { return totalHorasReales; }
    public void setTotalHorasReales(BigDecimal totalHorasReales) { this.totalHorasReales = totalHorasReales; }
    public Long getCantidadTareas() { return cantidadTareas; }
    public void setCantidadTareas(Long cantidadTareas) { this.cantidadTareas = cantidadTareas; }
}
