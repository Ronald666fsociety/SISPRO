package com.transandina.sigepro.controller;

import com.transandina.sigepro.service.ExportacionExcelService;
import com.transandina.sigepro.service.ExportacionPdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exportar")
public class ExportacionController {

    private final ExportacionPdfService exportacionPdfService;
    private final ExportacionExcelService exportacionExcelService;

    public ExportacionController(ExportacionPdfService exportacionPdfService,
                                 ExportacionExcelService exportacionExcelService) {
        this.exportacionPdfService = exportacionPdfService;
        this.exportacionExcelService = exportacionExcelService;
    }

    @GetMapping("/pdf/{idProyecto}")
    public ResponseEntity<byte[]> exportarPdf(@PathVariable Integer idProyecto) {
        byte[] pdf = exportacionPdfService.exportarPlanProyecto(idProyecto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "plan_proyecto_" + idProyecto + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping("/excel/{idProyecto}")
    public ResponseEntity<byte[]> exportarExcel(@PathVariable Integer idProyecto) {
        byte[] excel = exportacionExcelService.exportarPlanProyecto(idProyecto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "plan_proyecto_" + idProyecto + ".xlsx");

        return ResponseEntity.ok().headers(headers).body(excel);
    }

    @GetMapping("/pdf/proyectos")
    public ResponseEntity<byte[]> exportarListadoProyectos() {
        byte[] pdf = exportacionPdfService.exportarListadoProyectos();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_proyectos.pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping("/pdf/usuarios")
    public ResponseEntity<byte[]> exportarListadoUsuarios() {
        byte[] pdf = exportacionPdfService.exportarListadoUsuarios();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_usuarios.pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping("/pdf/auditoria")
    public ResponseEntity<byte[]> exportarAuditoria() {
        byte[] pdf = exportacionPdfService.exportarAuditoria();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_auditoria.pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
