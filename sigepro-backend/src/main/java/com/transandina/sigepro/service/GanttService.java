package com.transandina.sigepro.service;

import com.transandina.sigepro.dto.GanttDataResponse;
import com.transandina.sigepro.entity.DependenciaTarea;
import com.transandina.sigepro.entity.Tarea;
import com.transandina.sigepro.exception.ResourceNotFoundException;
import com.transandina.sigepro.repository.DependenciaTareaRepository;
import com.transandina.sigepro.repository.TareaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GanttService {

    private final TareaRepository tareaRepository;
    private final DependenciaTareaRepository dependenciaRepository;

    public GanttService(TareaRepository tareaRepository,
                        DependenciaTareaRepository dependenciaRepository) {
        this.tareaRepository = tareaRepository;
        this.dependenciaRepository = dependenciaRepository;
    }

    public GanttDataResponse obtenerGanttPorProyecto(Integer idProyecto) {
        List<Tarea> tareas = tareaRepository.findByProyectoId(idProyecto);
        if (tareas.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron tareas para el proyecto con id: " + idProyecto);
        }

        List<DependenciaTarea> dependencias = dependenciaRepository.findByTareaOrigenProyectoId(idProyecto);
        return GanttDataResponse.fromData(tareas, dependencias);
    }
}
