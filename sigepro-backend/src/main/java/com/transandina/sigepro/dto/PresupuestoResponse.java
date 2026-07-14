package com.transandina.sigepro.dto;

import java.math.BigDecimal;

public class PresupuestoResponse {

    private Integer idProyecto;
    private String nombreProyecto;
    private BigDecimal presupuestoTotalEstimado;
    private BigDecimal costoTotalEjecutado;
    private BigDecimal diferencia;
    private String estado;

    public static PresupuestoResponse fromData(Integer idProyecto, String nombreProyecto,
                                                BigDecimal presupuestoTotal, BigDecimal costoTotal) {
        PresupuestoResponse dto = new PresupuestoResponse();
        dto.setIdProyecto(idProyecto);
        dto.setNombreProyecto(nombreProyecto);
        dto.setPresupuestoTotalEstimado(presupuestoTotal);
        dto.setCostoTotalEjecutado(costoTotal);
        dto.setDiferencia(presupuestoTotal.subtract(costoTotal));
        dto.setEstado(dto.getDiferencia().compareTo(BigDecimal.ZERO) >= 0 ? "DENTRO_PRESUPUESTO" : "SOBRE_PRESUPUESTO");
        return dto;
    }

    public Integer getIdProyecto() { return idProyecto; }
    public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }
    public String getNombreProyecto() { return nombreProyecto; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }
    public BigDecimal getPresupuestoTotalEstimado() { return presupuestoTotalEstimado; }
    public void setPresupuestoTotalEstimado(BigDecimal presupuestoTotalEstimado) { this.presupuestoTotalEstimado = presupuestoTotalEstimado; }
    public BigDecimal getCostoTotalEjecutado() { return costoTotalEjecutado; }
    public void setCostoTotalEjecutado(BigDecimal costoTotalEjecutado) { this.costoTotalEjecutado = costoTotalEjecutado; }
    public BigDecimal getDiferencia() { return diferencia; }
    public void setDiferencia(BigDecimal diferencia) { this.diferencia = diferencia; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
