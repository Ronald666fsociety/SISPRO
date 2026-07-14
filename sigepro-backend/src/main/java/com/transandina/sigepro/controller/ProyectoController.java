package com.transandina.sigepro.controller;

import com.transandina.sigepro.dto.MensajeResponse;
import com.transandina.sigepro.dto.ProyectoRequest;
import com.transandina.sigepro.dto.ProyectoResponse;
import com.transandina.sigepro.enums.EstadoProyecto;
import com.transandina.sigepro.service.ProyectoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @GetMapping
    public ResponseEntity<List<ProyectoResponse>> listarTodos(
            @RequestParam(required = false) EstadoProyecto estado) {
        if (estado != null) {
            return ResponseEntity.ok(proyectoService.listarPorEstado(estado));
        }
        return ResponseEntity.ok(proyectoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProyectoResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(proyectoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProyectoResponse> crear(@Valid @RequestBody ProyectoRequest request) {
        ProyectoResponse response = proyectoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProyectoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ProyectoRequest request) {
        return ResponseEntity.ok(proyectoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Integer id) {
        proyectoService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Proyecto eliminado logicamente"));
    }
}
