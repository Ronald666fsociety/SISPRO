package com.transandina.sigepro.service;

import com.transandina.sigepro.entity.Proyecto;
import com.transandina.sigepro.entity.Tarea;
import com.transandina.sigepro.exception.ResourceNotFoundException;
import com.transandina.sigepro.repository.ProyectoRepository;
import com.transandina.sigepro.repository.TareaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExportacionExcelService {

    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;

    public ExportacionExcelService(ProyectoRepository proyectoRepository, TareaRepository tareaRepository) {
        this.proyectoRepository = proyectoRepository;
        this.tareaRepository = tareaRepository;
    }

    public byte[] exportarPlanProyecto(Integer idProyecto) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + idProyecto));

        List<Tarea> tareas = tareaRepository.findByProyectoId(idProyecto);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Plan de Proyecto");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            int rowIdx = 0;

            Row titleRow = sheet.createRow(rowIdx++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("SIGEPRO - Plan de Proyecto: " + proyecto.getNombre());
            titleCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

            rowIdx++;
            sheet.createRow(rowIdx++).createCell(0).setCellValue("Descripcion: " + (proyecto.getDescripcion() != null ? proyecto.getDescripcion() : "N/A"));
            sheet.createRow(rowIdx++).createCell(0).setCellValue("Fecha Inicio: " + proyecto.getFechaInicio());
            sheet.createRow(rowIdx++).createCell(0).setCellValue("Fecha Fin: " + proyecto.getFechaFin());
            sheet.createRow(rowIdx++).createCell(0).setCellValue("Estado: " + proyecto.getEstado().name());
            sheet.createRow(rowIdx++).createCell(0).setCellValue("Presupuesto Total: Bs " + proyecto.getPresupuestoTotal());
            sheet.createRow(rowIdx++).createCell(0).setCellValue("Costo Real: Bs " + proyecto.getCostoRealTotal());

            rowIdx++;

            String[] headers = {"Nombre", "Inicio", "Fin", "% Avance", "Presupuesto (S/)", "Costo (S/)"};
            Row headerRow = sheet.createRow(rowIdx++);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (Tarea t : tareas) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(t.getNombre());
                row.createCell(1).setCellValue(t.getFechaInicio().toString());
                row.createCell(2).setCellValue(t.getFechaFin().toString());
                row.createCell(3).setCellValue(t.getPorcentajeAvance());
                row.createCell(4).setCellValue(t.getPresupuestoEstimado().doubleValue());
                row.createCell(5).setCellValue(t.getCostoEjecutado().doubleValue());
            }

            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el Excel", e);
        }
    }
}
