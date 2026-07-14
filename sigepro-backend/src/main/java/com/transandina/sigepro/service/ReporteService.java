package com.transandina.sigepro.service;

import com.transandina.sigepro.dto.*;
import com.transandina.sigepro.entity.Proyecto;
import com.transandina.sigepro.entity.Tarea;
import com.transandina.sigepro.exception.ResourceNotFoundException;
import com.transandina.sigepro.repository.ProyectoRepository;
import com.transandina.sigepro.repository.TareaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReporteService {

    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;
    private final RecursoTareaService recursoTareaService;

    public ReporteService(ProyectoRepository proyectoRepository,
                          TareaRepository tareaRepository,
                          RecursoTareaService recursoTareaService) {
        this.proyectoRepository = proyectoRepository;
        this.tareaRepository = tareaRepository;
        this.recursoTareaService = recursoTareaService;
    }

    public SemaforoResponse calcularSemaforo(Integer idProyecto) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + idProyecto));

        List<Tarea> tareas = tareaRepository.findByProyectoId(idProyecto);

        double avanceReal = tareas.stream()
                .mapToInt(t -> t.getPorcentajeAvance() != null ? t.getPorcentajeAvance() : 0)
                .average()
                .orElse(0.0);

        avanceReal = BigDecimal.valueOf(avanceReal).setScale(2, RoundingMode.HALF_UP).doubleValue();

        double avancePlanificado = calcularAvancePlanificado(proyecto);

        return SemaforoResponse.fromData(
                proyecto.getId(),
                proyecto.getNombre(),
                proyecto.getEstado().name(),
                avanceReal,
                avancePlanificado,
                proyecto.getPresupuestoTotal(),
                proyecto.getCostoRealTotal()
        );
    }

    public PresupuestoResponse presupuestoVsCosto(Integer idProyecto) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + idProyecto));

        return PresupuestoResponse.fromData(
                proyecto.getId(),
                proyecto.getNombre(),
                proyecto.getPresupuestoTotal(),
                proyecto.getCostoRealTotal()
        );
    }

    public CargaTrabajoResponse cargaTrabajoPorUsuario(Integer idUsuario) {
        return recursoTareaService.obtenerCargaTrabajo(idUsuario);
    }

    private double calcularAvancePlanificado(Proyecto proyecto) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = proyecto.getFechaInicio();
        LocalDate fin = proyecto.getFechaFin();

        if (hoy.isBefore(inicio)) return 0.0;
        if (hoy.isAfter(fin) || hoy.isEqual(fin)) return 100.0;

        long total = ChronoUnit.DAYS.between(inicio, fin);
        long transcurrido = ChronoUnit.DAYS.between(inicio, hoy);

        if (total == 0) return 100.0;

        double porcentaje = ((double) transcurrido / total) * 100.0;
        return BigDecimal.valueOf(porcentaje).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
