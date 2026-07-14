package com.transandina.sigepro.dto;

import com.transandina.sigepro.entity.Tarea;
import com.transandina.sigepro.entity.DependenciaTarea;

import java.util.List;
import java.util.stream.Collectors;

public class GanttDataResponse {

    private List<GanttTask> tareas;

    public static GanttDataResponse fromData(List<Tarea> tareas, List<DependenciaTarea> dependencias) {
        GanttDataResponse response = new GanttDataResponse();

        response.tareas = tareas.stream().map(t -> {
            GanttTask task = new GanttTask();
            task.setId(t.getId().toString());
            task.setName(t.getNombre());
            task.setStart(t.getFechaInicio().toString());
            task.setEnd(t.getFechaFin().toString());
            task.setProgress(t.getPorcentajeAvance().doubleValue());

            String deps = dependencias.stream()
                    .filter(d -> d.getTareaDestino().getId().equals(t.getId()))
                    .map(d -> d.getTareaOrigen().getId().toString())
                    .collect(Collectors.joining(", "));
            task.setDependencies(deps);

            return task;
        }).toList();

        return response;
    }

    public List<GanttTask> getTareas() { return tareas; }
    public void setTareas(List<GanttTask> tareas) { this.tareas = tareas; }

    public static class GanttTask {
        private String id;
        private String name;
        private String start;
        private String end;
        private double progress;
        private String dependencies;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getStart() { return start; }
        public void setStart(String start) { this.start = start; }
        public String getEnd() { return end; }
        public void setEnd(String end) { this.end = end; }
        public double getProgress() { return progress; }
        public void setProgress(double progress) { this.progress = progress; }
        public String getDependencies() { return dependencies; }
        public void setDependencies(String dependencies) { this.dependencies = dependencies; }
    }
}
