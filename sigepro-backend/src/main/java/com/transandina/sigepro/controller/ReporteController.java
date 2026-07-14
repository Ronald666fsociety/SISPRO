package com.transandina.sigepro.controller;

import com.transandina.sigepro.dto.*;
import com.transandina.sigepro.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/presupuesto/{idProyecto}")
    public ResponseEntity<PresupuestoResponse> presupuestoVsCosto(@PathVariable Integer idProyecto) {
        return ResponseEntity.ok(reporteService.presupuestoVsCosto(idProyecto));
    }

    @GetMapping("/carga-trabajo/{idUsuario}")
    public ResponseEntity<CargaTrabajoResponse> cargaTrabajo(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(reporteService.cargaTrabajoPorUsuario(idUsuario));
    }

    @GetMapping("/semaforo/{idProyecto}")
    public ResponseEntity<SemaforoResponse> semaforo(@PathVariable Integer idProyecto) {
        return ResponseEntity.ok(reporteService.calcularSemaforo(idProyecto));
    }
}
