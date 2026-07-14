package com.transandina.sigepro.controller;

import com.transandina.sigepro.dto.MensajeResponse;
import com.transandina.sigepro.dto.TareaRequest;
import com.transandina.sigepro.dto.TareaResponse;
import com.transandina.sigepro.service.TareaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping("/proyecto/{idProyecto}")
    public ResponseEntity<List<TareaResponse>> listarPorProyecto(@PathVariable Integer idProyecto) {
        return ResponseEntity.ok(tareaService.listarPorProyecto(idProyecto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TareaResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(tareaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<TareaResponse> crear(@Valid @RequestBody TareaRequest request) {
        TareaResponse response = tareaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TareaResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody TareaRequest request) {
        return ResponseEntity.ok(tareaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Integer id) {
        tareaService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Tarea eliminada"));
    }
}
