package com.transandina.sigepro.controller;

import com.transandina.sigepro.dto.DependenciaRequest;
import com.transandina.sigepro.dto.DependenciaResponse;
import com.transandina.sigepro.dto.MensajeResponse;
import com.transandina.sigepro.service.DependenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dependencias")
public class DependenciaController {

    private final DependenciaService dependenciaService;

    public DependenciaController(DependenciaService dependenciaService) {
        this.dependenciaService = dependenciaService;
    }

    @GetMapping("/proyecto/{idProyecto}")
    public ResponseEntity<List<DependenciaResponse>> listarPorProyecto(@PathVariable Integer idProyecto) {
        return ResponseEntity.ok(dependenciaService.listarPorProyecto(idProyecto));
    }

    @PostMapping
    public ResponseEntity<DependenciaResponse> crear(@Valid @RequestBody DependenciaRequest request) {
        DependenciaResponse response = dependenciaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Integer id) {
        dependenciaService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Dependencia eliminada"));
    }
}
