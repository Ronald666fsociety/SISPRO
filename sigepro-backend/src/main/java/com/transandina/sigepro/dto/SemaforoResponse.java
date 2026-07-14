package com.transandina.sigepro.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SemaforoResponse {

    private Integer idProyecto;
    private String nombreProyecto;
    private String estadoProyecto;
    private String color;
    private Double avanceReal;
    private Double avancePlanificado;
    private Double retrasoPorcentaje;
    private BigDecimal presupuestoTotal;
    private BigDecimal costoReal;
    private Double sobrecostoPorcentaje;

    public static SemaforoResponse fromData(Integer idProyecto, String nombreProyecto, String estadoProyecto,
                                             Double avanceReal, Double avancePlanificado,
                                             BigDecimal presupuestoTotal, BigDecimal costoReal) {
        SemaforoResponse dto = new SemaforoResponse();
        dto.setIdProyecto(idProyecto);
        dto.setNombreProyecto(nombreProyecto);
        dto.setEstadoProyecto(estadoProyecto);

        double avanceRealVal = avanceReal != null ? avanceReal : 0.0;
        double avancePlanVal = avancePlanificado != null ? avancePlanificado : 0.0;
        BigDecimal presupuesto = presupuestoTotal != null ? presupuestoTotal : BigDecimal.ZERO;
        BigDecimal costo = costoReal != null ? costoReal : BigDecimal.ZERO;

        dto.setAvanceReal(avanceRealVal);
        dto.setAvancePlanificado(avancePlanVal);
        dto.setPresupuestoTotal(presupuesto);
        dto.setCostoReal(costo);

        double retraso = 0.0;
        if (avancePlanVal > 0 && avanceRealVal < avancePlanVal) {
            retraso = ((avancePlanVal - avanceRealVal) / avancePlanVal) * 100.0;
            retraso = BigDecimal.valueOf(retraso).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }
        dto.setRetrasoPorcentaje(retraso);

        double sobrecosto = 0.0;
        if (presupuesto.compareTo(BigDecimal.ZERO) > 0 && costo.compareTo(presupuesto) > 0) {
            sobrecosto = costo.subtract(presupuesto)
                    .divide(presupuesto, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }
        dto.setSobreCostoPorcentaje(sobrecosto);

        if (retraso <= 0 && sobrecosto <= 0) {
            dto.setColor("VERDE");
        } else if (retraso <= 15.0 && sobrecosto <= 10.0) {
            dto.setColor("AMARILLO");
        } else {
            dto.setColor("ROJO");
        }

        return dto;
    }

    public Integer getIdProyecto() { return idProyecto; }
    public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }
    public String getNombreProyecto() { return nombreProyecto; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }
    public String getEstadoProyecto() { return estadoProyecto; }
    public void setEstadoProyecto(String estadoProyecto) { this.estadoProyecto = estadoProyecto; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Double getAvanceReal() { return avanceReal; }
    public void setAvanceReal(Double avanceReal) { this.avanceReal = avanceReal; }
    public Double getAvancePlanificado() { return avancePlanificado; }
    public void setAvancePlanificado(Double avancePlanificado) { this.avancePlanificado = avancePlanificado; }
    public Double getRetrasoPorcentaje() { return retrasoPorcentaje; }
    public void setRetrasoPorcentaje(Double retrasoPorcentaje) { this.retrasoPorcentaje = retrasoPorcentaje; }
    public BigDecimal getPresupuestoTotal() { return presupuestoTotal; }
    public void setPresupuestoTotal(BigDecimal presupuestoTotal) { this.presupuestoTotal = presupuestoTotal; }
    public BigDecimal getCostoReal() { return costoReal; }
    public void setCostoReal(BigDecimal costoReal) { this.costoReal = costoReal; }
    public Double getSobreCostoPorcentaje() { return sobrecostoPorcentaje; }
    public void setSobreCostoPorcentaje(Double sobrecostoPorcentaje) { this.sobrecostoPorcentaje = sobrecostoPorcentaje; }
}
