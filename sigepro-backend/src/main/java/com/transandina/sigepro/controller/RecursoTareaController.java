package com.transandina.sigepro.controller;

import com.transandina.sigepro.dto.MensajeResponse;
import com.transandina.sigepro.dto.RecursoTareaRequest;
import com.transandina.sigepro.dto.RecursoTareaResponse;
import com.transandina.sigepro.service.RecursoTareaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
public class RecursoTareaController {

    private final RecursoTareaService recursoTareaService;

    public RecursoTareaController(RecursoTareaService recursoTareaService) {
        this.recursoTareaService = recursoTareaService;
    }

    @GetMapping("/proyecto/{idProyecto}")
    public ResponseEntity<List<RecursoTareaResponse>> listarPorProyecto(@PathVariable Integer idProyecto) {
        return ResponseEntity.ok(recursoTareaService.listarPorProyecto(idProyecto));
    }

    @GetMapping("/tarea/{idTarea}")
    public ResponseEntity<List<RecursoTareaResponse>> listarPorTarea(@PathVariable Integer idTarea) {
        return ResponseEntity.ok(recursoTareaService.listarPorTarea(idTarea));
    }

    @PostMapping
    public ResponseEntity<RecursoTareaResponse> crear(@Valid @RequestBody RecursoTareaRequest request) {
        RecursoTareaResponse response = recursoTareaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecursoTareaResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody RecursoTareaRequest request) {
        return ResponseEntity.ok(recursoTareaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Integer id) {
        recursoTareaService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Asignacion eliminada"));
    }
}
