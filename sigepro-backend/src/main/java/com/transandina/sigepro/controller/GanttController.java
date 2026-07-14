package com.transandina.sigepro.controller;

import com.transandina.sigepro.dto.GanttDataResponse;
import com.transandina.sigepro.service.GanttService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gantt")
public class GanttController {

    private final GanttService ganttService;

    public GanttController(GanttService ganttService) {
        this.ganttService = ganttService;
    }

    @GetMapping("/proyecto/{idProyecto}")
    public ResponseEntity<GanttDataResponse> obtenerGantt(@PathVariable Integer idProyecto) {
        return ResponseEntity.ok(ganttService.obtenerGanttPorProyecto(idProyecto));
    }
}
