package com.transandina.sigepro.controller;

import com.transandina.sigepro.dto.AuditoriaResponse;
import com.transandina.sigepro.service.AuditoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public ResponseEntity<List<AuditoriaResponse>> listarTodas() {
        return ResponseEntity.ok(auditoriaService.listarTodas());
    }

    @GetMapping("/{entidad}/{idEntidad}")
    public ResponseEntity<List<AuditoriaResponse>> listarPorEntidad(
            @PathVariable String entidad,
            @PathVariable Integer idEntidad) {
        return ResponseEntity.ok(auditoriaService.listarPorEntidad(entidad, idEntidad));
    }
}
