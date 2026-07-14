package com.transandina.sigepro.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.transandina.sigepro.entity.Auditoria;
import com.transandina.sigepro.entity.Proyecto;
import com.transandina.sigepro.entity.Tarea;
import com.transandina.sigepro.entity.Usuario;
import com.transandina.sigepro.exception.ResourceNotFoundException;
import com.transandina.sigepro.repository.AuditoriaRepository;
import com.transandina.sigepro.repository.ProyectoRepository;
import com.transandina.sigepro.repository.TareaRepository;
import com.transandina.sigepro.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExportacionPdfService {

    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaRepository auditoriaRepository;

    public ExportacionPdfService(ProyectoRepository proyectoRepository, TareaRepository tareaRepository,
                                  UsuarioRepository usuarioRepository, AuditoriaRepository auditoriaRepository) {
        this.proyectoRepository = proyectoRepository;
        this.tareaRepository = tareaRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    public byte[] exportarPlanProyecto(Integer idProyecto) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + idProyecto));

        List<Tarea> tareas = tareaRepository.findByProyectoId(idProyecto);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Color primaryColor = new Color(26, 86, 219);
            Color primaryDark = new Color(15, 23, 42);
            Color successColor = new Color(16, 185, 129);
            Color dangerColor = new Color(239, 68, 68);
            Color warningColor = new Color(245, 158, 11);
            Color grayLight = new Color(248, 250, 252);
            Color grayBorder = new Color(226, 232, 240);
            Color grayText = new Color(100, 116, 139);

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, primaryColor);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, grayText);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, primaryDark);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(71, 85, 105));
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, primaryDark);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            Font tableCellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(30, 41, 59));
            Font smallNoteFont = FontFactory.getFont(FontFactory.HELVETICA, 8, grayText);

            Paragraph spacer = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 6));
            document.add(spacer);

            // --- TOP BAR ---
            PdfPTable topBar = new PdfPTable(2);
            topBar.setWidthPercentage(100);
            topBar.setWidths(new float[]{1f, 6f});

            PdfPCell logoCell = new PdfPCell(new Phrase("SIGEPRO", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.WHITE)));
            logoCell.setBackgroundColor(primaryColor);
            logoCell.setPadding(10);
            logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setBorder(Rectangle.NO_BORDER);
            topBar.addCell(logoCell);

            PdfPCell brandCell = new PdfPCell();
            brandCell.setBackgroundColor(primaryColor);
            brandCell.setPadding(10);
            brandCell.setBorder(Rectangle.NO_BORDER);
            Paragraph brandP = new Paragraph("TransAndina S.A.", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.WHITE));
            brandP.setSpacingAfter(2);
            brandCell.addElement(brandP);
            brandCell.addElement(new Paragraph("Sistema de Gestion de Proyectos", FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(191, 219, 254))));
            topBar.addCell(brandCell);
            document.add(topBar);

            // --- TITLE ---
            Paragraph title = new Paragraph("Plan de Proyecto", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(16);
            title.setSpacingAfter(2);
            document.add(title);
            Paragraph titleSub = new Paragraph(proyecto.getNombre(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, primaryDark));
            titleSub.setAlignment(Element.ALIGN_CENTER);
            titleSub.setSpacingAfter(10);
            document.add(titleSub);

            // --- INFO SECTION ---
            Paragraph infoSection = new Paragraph("Informacion General", sectionFont);
            infoSection.setSpacingBefore(6);
            infoSection.setSpacingAfter(8);
            document.add(infoSection);

            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1.2f, 2f, 1.2f, 2f});
            infoTable.setSpacingAfter(14);

            addInfoGroup(infoTable, "Descripcion", proyecto.getDescripcion() != null ? proyecto.getDescripcion() : "N/A", labelFont, valueFont, grayLight, grayBorder, 0);
            addInfoGroup(infoTable, "Estado", getEstadoLabel(proyecto.getEstado().name()), labelFont, valueFont, grayLight, grayBorder, 0);
            addInfoGroup(infoTable, "Fecha Inicio", proyecto.getFechaInicio().toString(), labelFont, valueFont, grayLight, grayBorder, 1);
            addInfoGroup(infoTable, "Fecha Fin", proyecto.getFechaFin().toString(), labelFont, valueFont, grayLight, grayBorder, 1);
            addInfoGroup(infoTable, "Presupuesto Total", "Bs " + String.format("%,.2f", proyecto.getPresupuestoTotal()), labelFont, valueFont, grayLight, grayBorder, 2);
            addInfoGroup(infoTable, "Costo Real", "Bs " + String.format("%,.2f", proyecto.getCostoRealTotal()), labelFont, valueFont, grayLight, grayBorder, 2);
            double diff = proyecto.getPresupuestoTotal().subtract(proyecto.getCostoRealTotal()).doubleValue();
            Color diffInfoColor = diff >= 0 ? successColor : dangerColor;
            addInfoGroup(infoTable, "Diferencia", "Bs " + String.format("%,.2f", diff), labelFont, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, diffInfoColor), grayLight, grayBorder, 3);
            String jefeNombre = proyecto.getJefeProyecto() != null ? proyecto.getJefeProyecto().getNombre() : "No asignado";
            addInfoGroup(infoTable, "Jefe Proyecto", jefeNombre, labelFont, valueFont, grayLight, grayBorder, 3);
            document.add(infoTable);

            // --- TASKS SECTION ---
            Paragraph tasksSection = new Paragraph("Tareas del Proyecto", sectionFont);
            tasksSection.setSpacingBefore(4);
            tasksSection.setSpacingAfter(8);
            document.add(tasksSection);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.8f, 1.5f, 1.5f, 1.2f, 1.2f, 1.5f, 1.5f});
            table.setHeaderRows(1);
            table.setSpacingAfter(12);

            Color headerBg = primaryColor;
            String[] headers = {"Nombre", "Inicio", "Fin", "Avance", "Estado", "Presupuesto", "Costo"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, tableHeaderFont));
                cell.setBackgroundColor(headerBg);
                cell.setPadding(7);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
            }

            boolean alternate = false;
            for (Tarea t : tareas) {
                PdfPCell nameCell = new PdfPCell(new Phrase(t.getNombre(), tableCellFont));
                nameCell.setPadding(5);
                if (alternate) nameCell.setBackgroundColor(grayLight);
                table.addCell(nameCell);

                addCell(table, t.getFechaInicio().toString(), tableCellFont, alternate);
                addCell(table, t.getFechaFin().toString(), tableCellFont, alternate);

                int progress = t.getPorcentajeAvance();
                Color progressColor = progress >= 100 ? successColor : progress >= 50 ? primaryColor : warningColor;

                PdfPCell progressCell = new PdfPCell();
                progressCell.setPadding(5);
                progressCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                if (alternate) progressCell.setBackgroundColor(grayLight);

                Paragraph progressP = new Paragraph();
                progressP.add(new Chunk(progress + "% ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, progressColor)));
                progressP.add(new Chunk(getProgressBar(progress), FontFactory.getFont(FontFactory.HELVETICA, 7, grayText)));
                progressCell.addElement(progressP);
                table.addCell(progressCell);

                String taskStatus = progress >= 100 ? "Completado" : progress >= 50 ? "En Progreso" : "Pendiente";
                Color taskStatusColor = progress >= 100 ? successColor : progress >= 50 ? primaryColor : warningColor;
                PdfPCell statusCell = new PdfPCell(new Phrase(taskStatus, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, taskStatusColor)));
                statusCell.setPadding(5);
                statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                if (alternate) statusCell.setBackgroundColor(grayLight);
                table.addCell(statusCell);

                addCell(table, "Bs " + String.format("%,.2f", t.getPresupuestoEstimado()), tableCellFont, alternate);
                addCell(table, "Bs " + String.format("%,.2f", t.getCostoEjecutado()), tableCellFont, alternate);
                alternate = !alternate;
            }

            document.add(table);

            // --- FINANCIAL SUMMARY ---
            if (!tareas.isEmpty()) {
                double totalPresupuesto = tareas.stream().mapToDouble(t -> t.getPresupuestoEstimado().doubleValue()).sum();
                double totalCosto = tareas.stream().mapToDouble(t -> t.getCostoEjecutado().doubleValue()).sum();
                double totalDiff = totalPresupuesto - totalCosto;
                Color diffSummaryColor = totalDiff >= 0 ? successColor : dangerColor;

                Paragraph summaryTitle = new Paragraph("Resumen Financiero", sectionFont);
                summaryTitle.setSpacingBefore(6);
                summaryTitle.setSpacingAfter(8);
                document.add(summaryTitle);

                PdfPTable summaryTable = new PdfPTable(3);
                summaryTable.setWidthPercentage(70);
                summaryTable.setHorizontalAlignment(Element.ALIGN_CENTER);

                addSummaryCell(summaryTable, "Presupuesto Total", "Bs " + String.format("%,.2f", totalPresupuesto), primaryColor);
                addSummaryCell(summaryTable, "Costo Ejecutado", "Bs " + String.format("%,.2f", totalCosto), dangerColor);
                addSummaryCell(summaryTable, totalDiff >= 0 ? "A Favor" : "En Contra", "Bs " + String.format("%,.2f", totalDiff), diffSummaryColor);
                document.add(summaryTable);
            }

            // --- CONCLUSION ---
            Paragraph conclusionTitle = new Paragraph("Conclusion", sectionFont);
            conclusionTitle.setSpacingBefore(14);
            conclusionTitle.setSpacingAfter(6);
            document.add(conclusionTitle);

            double avancePromedio = tareas.stream().mapToInt(Tarea::getPorcentajeAvance).average().orElse(0);
            String conclusion = "El proyecto '" + proyecto.getNombre() + "' presenta un avance promedio del "
                    + String.format("%.1f", avancePromedio) + "% con un presupuesto total de Bs "
                    + String.format("%,.2f", proyecto.getPresupuestoTotal())
                    + " y un costo ejecutado de Bs " + String.format("%,.2f", proyecto.getCostoRealTotal()) + ".";
            Paragraph conclusionP = new Paragraph(conclusion, valueFont);
            conclusionP.setSpacingAfter(2);
            document.add(conclusionP);

            String recomendacion;
            if (avancePromedio >= 80) {
                recomendacion = "El proyecto se encuentra en una etapa avanzada. Se recomienda continuar con la supervision para asegurar una finalizacion exitosa dentro del presupuesto establecido.";
            } else if (avancePromedio >= 40) {
                recomendacion = "El proyecto se encuentra en desarrollo activo. Se recomienda monitorear el cronograma y los costos para evitar desviaciones significativas.";
            } else {
                recomendacion = "El proyecto se encuentra en sus etapas iniciales. Se recomienda establecer controles de seguimiento periodicos para garantizar el cumplimiento de los objetivos.";
            }
            Paragraph recP = new Paragraph(recomendacion, FontFactory.getFont(FontFactory.HELVETICA, 9, grayText));
            recP.setSpacingBefore(4);
            recP.setSpacingAfter(10);
            document.add(recP);

            // --- FOOTER ---
            Paragraph divider = new Paragraph("_" .repeat(85), FontFactory.getFont(FontFactory.HELVETICA, 8, grayBorder));
            divider.setAlignment(Element.ALIGN_CENTER);
            divider.setSpacingAfter(6);
            document.add(divider);

            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            PdfPCell footerCell1 = new PdfPCell(new Phrase("Documento generado por SIGEPRO", FontFactory.getFont(FontFactory.HELVETICA, 8, grayText)));
            footerCell1.setBorder(Rectangle.NO_BORDER);
            footerCell1.setPadding(2);
            footerTable.addCell(footerCell1);
            PdfPCell footerCell2 = new PdfPCell(new Phrase(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), FontFactory.getFont(FontFactory.HELVETICA, 8, grayText)));
            footerCell2.setBorder(Rectangle.NO_BORDER);
            footerCell2.setPadding(2);
            footerCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            footerTable.addCell(footerCell2);
            document.add(footerTable);

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }

        return baos.toByteArray();
    }

    private void addInfoGroup(PdfPTable table, String label, String value, Font labelFont, Font valueFont, Color bgColor, Color borderColor, int rowGroup) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setPadding(5);
        labelCell.setBorder(Rectangle.BOX);
        labelCell.setBorderColor(borderColor);
        labelCell.setBackgroundColor(bgColor);
        if (rowGroup % 2 == 0) {
            labelCell.setBackgroundColor(bgColor);
        }
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(5);
        valueCell.setBorder(Rectangle.BOX);
        valueCell.setBorderColor(borderColor);
        if (rowGroup % 2 == 0) {
            valueCell.setBackgroundColor(bgColor);
        }
        table.addCell(valueCell);
    }

    private void addCell(PdfPTable table, String text, Font font, boolean alternate) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        if (alternate) cell.setBackgroundColor(new Color(248, 250, 252));
        table.addCell(cell);
    }

    private void addSummaryCell(PdfPTable table, String label, String value, Color color) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
        labelCell.setBackgroundColor(color);
        labelCell.setPadding(8);
        labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, color)));
        valueCell.setPadding(8);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        valueCell.setBorderColor(new Color(226, 232, 240));
        table.addCell(valueCell);
    }

    private String getEstadoLabel(String estado) {
        switch (estado) {
            case "PLANIFICADO": return "Planificado";
            case "EN_CURSO": return "En Curso";
            case "FINALIZADO": return "Finalizado";
            case "CANCELADO": return "Cancelado";
            default: return estado;
        }
    }

    private String getProgressBar(int progress) {
        int bars = Math.min(progress / 10, 10);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bars; i++) sb.append("|");
        for (int i = bars; i < 10; i++) sb.append(".");
        return sb.toString();
    }

    // ==================== LISTADO DE PROYECTOS ====================
    public byte[] exportarListadoProyectos() {
        List<Proyecto> proyectos = proyectoRepository.findAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            addHeader(document, "Reporte de Proyectos", "Listado general de proyectos - TransAndina S.A.");
            if (proyectos.isEmpty()) {
                document.add(new Paragraph("No hay proyectos registrados.", FontFactory.getFont(FontFactory.HELVETICA, 11, new Color(100, 116, 139))));
            } else {
                double totalPresupuesto = proyectos.stream().mapToDouble(p -> p.getPresupuestoTotal().doubleValue()).sum();
                double totalCosto = proyectos.stream().mapToDouble(p -> p.getCostoRealTotal().doubleValue()).sum();

                PdfPTable summaryTable = new PdfPTable(3);
                summaryTable.setWidthPercentage(60);
                summaryTable.setHorizontalAlignment(Element.ALIGN_CENTER);
                summaryTable.setSpacingAfter(14);
                addSummaryCell(summaryTable, "Total Proyectos", String.valueOf(proyectos.size()), new Color(26, 86, 219));
                addSummaryCell(summaryTable, "Presupuesto Total", "Bs " + String.format("%,.2f", totalPresupuesto), new Color(16, 185, 129));
                addSummaryCell(summaryTable, "Costo Total", "Bs " + String.format("%,.2f", totalCosto), new Color(239, 68, 68));
                document.add(summaryTable);

                PdfPTable table = new PdfPTable(7);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2.5f, 1.5f, 1.5f, 1.2f, 1.5f, 1.5f, 1.5f});
                table.setHeaderRows(1);
                Color headerBg = new Color(26, 86, 219);
                String[] headers = {"Nombre", "Jefe", "Inicio", "Estado", "Presupuesto", "Costo", "Diferencia"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
                    cell.setBackgroundColor(headerBg); cell.setPadding(6); cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                boolean alt = false;
                for (Proyecto p : proyectos) {
                    Color rowBg = alt ? new Color(248, 250, 252) : Color.WHITE;
                    addCellStyle(table, p.getNombre(), rowBg, Element.ALIGN_LEFT);
                    addCellStyle(table, p.getJefeProyecto() != null ? p.getJefeProyecto().getNombre() : "N/A", rowBg, Element.ALIGN_LEFT);
                    addCellStyle(table, p.getFechaInicio().toString(), rowBg, Element.ALIGN_CENTER);
                    String estado = getEstadoLabel(p.getEstado().name());
                    Color estadoColor = p.getEstado().name().equals("FINALIZADO") ? new Color(16, 185, 129) : p.getEstado().name().equals("EN_CURSO") ? new Color(59, 130, 246) : p.getEstado().name().equals("CANCELADO") ? new Color(239, 68, 68) : new Color(245, 158, 11);
                    addCellStyle(table, estado, rowBg, Element.ALIGN_CENTER, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, estadoColor));
                    addCellStyle(table, "Bs " + String.format("%,.2f", p.getPresupuestoTotal()), rowBg, Element.ALIGN_RIGHT);
                    addCellStyle(table, "Bs " + String.format("%,.2f", p.getCostoRealTotal()), rowBg, Element.ALIGN_RIGHT);
                    double d = p.getPresupuestoTotal().subtract(p.getCostoRealTotal()).doubleValue();
                    Color diffC = d >= 0 ? new Color(16, 185, 129) : new Color(239, 68, 68);
                    addCellStyle(table, "Bs " + String.format("%,.2f", d), rowBg, Element.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, diffC));
                    alt = !alt;
                }
                document.add(table);
            }
            addFooter(document);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar PDF de proyectos", e);
        }
        return baos.toByteArray();
    }

    // ==================== LISTADO DE USUARIOS ====================
    public byte[] exportarListadoUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            addHeader(document, "Reporte de Usuarios", "Listado general de usuarios - TransAndina S.A.");

            if (usuarios.isEmpty()) {
                document.add(new Paragraph("No hay usuarios registrados.", FontFactory.getFont(FontFactory.HELVETICA, 11, new Color(100, 116, 139))));
            } else {
                long activos = usuarios.stream().filter(Usuario::getActivo).count();
                long inactivos = usuarios.size() - activos;
                PdfPTable sumTable = new PdfPTable(3);
                sumTable.setWidthPercentage(60);
                sumTable.setHorizontalAlignment(Element.ALIGN_CENTER);
                sumTable.setSpacingAfter(14);
                addSummaryCell(sumTable, "Total Usuarios", String.valueOf(usuarios.size()), new Color(26, 86, 219));
                addSummaryCell(sumTable, "Activos", String.valueOf(activos), new Color(16, 185, 129));
                addSummaryCell(sumTable, "Inactivos", String.valueOf(inactivos), new Color(239, 68, 68));
                document.add(sumTable);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2.5f, 2.5f, 1.5f, 1f, 1.5f});
                table.setHeaderRows(1);
                Color hBg = new Color(26, 86, 219);
                for (String h : new String[]{"Nombre", "Email", "Rol", "Activo", "Creado"}) {
                    PdfPCell c = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
                    c.setBackgroundColor(hBg); c.setPadding(6); c.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(c);
                }
                boolean alt = false;
                for (Usuario u : usuarios) {
                    Color rb = alt ? new Color(248, 250, 252) : Color.WHITE;
                    addCellStyle(table, u.getNombre(), rb, Element.ALIGN_LEFT);
                    addCellStyle(table, u.getEmail(), rb, Element.ALIGN_LEFT);
                    String rol = u.getRol().name().replace("_", " ");
                    addCellStyle(table, rol, rb, Element.ALIGN_CENTER);
                    addCellStyle(table, u.getActivo() ? "Si" : "No", rb, Element.ALIGN_CENTER, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, u.getActivo() ? new Color(16, 185, 129) : new Color(239, 68, 68)));
                    addCellStyle(table, u.getFechaCreacion().toLocalDate().toString(), rb, Element.ALIGN_CENTER);
                    alt = !alt;
                }
                document.add(table);
            }
            addFooter(document);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar PDF de usuarios", e);
        }
        return baos.toByteArray();
    }

    // ==================== AUDITORIA ====================
    public byte[] exportarAuditoria() {
        List<Auditoria> logs = auditoriaRepository.findAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            addHeader(document, "Reporte de Auditoria", "Registro de eventos del sistema - TransAndina S.A.");

            if (logs.isEmpty()) {
                document.add(new Paragraph("No hay eventos registrados.", FontFactory.getFont(FontFactory.HELVETICA, 11, new Color(100, 116, 139))));
            } else {
                long creaciones = logs.stream().filter(l -> l.getAccion().equals("CREAR")).count();
                long actualizaciones = logs.stream().filter(l -> l.getAccion().equals("ACTUALIZAR")).count();
                long eliminaciones = logs.stream().filter(l -> l.getAccion().equals("ELIMINAR")).count();
                PdfPTable sumTable = new PdfPTable(4);
                sumTable.setWidthPercentage(80);
                sumTable.setHorizontalAlignment(Element.ALIGN_CENTER);
                sumTable.setSpacingAfter(14);
                addSummaryCell(sumTable, "Total Eventos", String.valueOf(logs.size()), new Color(26, 86, 219));
                addSummaryCell(sumTable, "Creaciones", String.valueOf(creaciones), new Color(16, 185, 129));
                addSummaryCell(sumTable, "Actualizaciones", String.valueOf(actualizaciones), new Color(245, 158, 11));
                addSummaryCell(sumTable, "Eliminaciones", String.valueOf(eliminaciones), new Color(239, 68, 68));
                document.add(sumTable);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{1.5f, 2f, 1.2f, 1.2f, 1f});
                table.setHeaderRows(1);
                Color hBg = new Color(26, 86, 219);
                for (String h : new String[]{"Fecha", "Usuario", "Accion", "Entidad", "ID"}) {
                    PdfPCell c = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
                    c.setBackgroundColor(hBg); c.setPadding(6); c.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(c);
                }
                boolean alt = false;
                for (Auditoria l : logs) {
                    Color rb = alt ? new Color(248, 250, 252) : Color.WHITE;
                    addCellStyle(table, l.getFecha().toString().replace("T", " "), rb, Element.ALIGN_CENTER);
                    addCellStyle(table, l.getUsuario().getNombre(), rb, Element.ALIGN_LEFT);
                    String accionLabel = l.getAccion().equals("CREAR") ? "Creacion" : l.getAccion().equals("ACTUALIZAR") ? "Actualizacion" : "Eliminacion";
                    Color accC = l.getAccion().equals("CREAR") ? new Color(16, 185, 129) : l.getAccion().equals("ACTUALIZAR") ? new Color(245, 158, 11) : new Color(239, 68, 68);
                    addCellStyle(table, accionLabel, rb, Element.ALIGN_CENTER, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, accC));
                    addCellStyle(table, l.getEntidad(), rb, Element.ALIGN_CENTER);
                    addCellStyle(table, String.valueOf(l.getIdEntidad()), rb, Element.ALIGN_CENTER);
                    alt = !alt;
                }
                document.add(table);
            }
            addFooter(document);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar PDF de auditoria", e);
        }
        return baos.toByteArray();
    }

    // ==================== HELPERS ====================
    private void addHeader(Document doc, String title, String subtitle) throws DocumentException {
        PdfPTable topBar = new PdfPTable(2);
        topBar.setWidthPercentage(100);
        topBar.setWidths(new float[]{1f, 6f});
        PdfPCell logoCell = new PdfPCell(new Phrase("SIGEPRO", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.WHITE)));
        logoCell.setBackgroundColor(new Color(26, 86, 219)); logoCell.setPadding(10);
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER); logoCell.setBorder(Rectangle.NO_BORDER);
        topBar.addCell(logoCell);
        PdfPCell brandCell = new PdfPCell();
        brandCell.setBackgroundColor(new Color(26, 86, 219)); brandCell.setPadding(10); brandCell.setBorder(Rectangle.NO_BORDER);
        brandCell.addElement(new Paragraph("TransAndina S.A.", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.WHITE)));
        brandCell.addElement(new Paragraph("Sistema de Gestion de Proyectos", FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(191, 219, 254))));
        topBar.addCell(brandCell);
        doc.add(topBar);
        Paragraph t = new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new Color(26, 86, 219)));
        t.setAlignment(Element.ALIGN_CENTER); t.setSpacingBefore(14); t.setSpacingAfter(2);
        doc.add(t);
        Paragraph s = new Paragraph(subtitle, FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(100, 116, 139)));
        s.setAlignment(Element.ALIGN_CENTER); s.setSpacingAfter(12);
        doc.add(s);
    }

    private void addFooter(Document doc) throws DocumentException {
        Paragraph divider = new Paragraph("_" .repeat(85), FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(226, 232, 240)));
        divider.setAlignment(Element.ALIGN_CENTER); divider.setSpacingAfter(6);
        doc.add(divider);
        PdfPTable ft = new PdfPTable(2);
        ft.setWidthPercentage(100);
        PdfPCell fc1 = new PdfPCell(new Phrase("Documento generado por SIGEPRO", FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(100, 116, 139))));
        fc1.setBorder(Rectangle.NO_BORDER); fc1.setPadding(2);
        ft.addCell(fc1);
        PdfPCell fc2 = new PdfPCell(new Phrase(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(100, 116, 139))));
        fc2.setBorder(Rectangle.NO_BORDER); fc2.setPadding(2); fc2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        ft.addCell(fc2);
        doc.add(ft);
    }

    private void addCellStyle(PdfPTable table, String text, Color bgColor, int align) {
        PdfPCell c = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(30, 41, 59))));
        c.setPadding(5); c.setBackgroundColor(bgColor); c.setHorizontalAlignment(align);
        table.addCell(c);
    }

    private void addCellStyle(PdfPTable table, String text, Color bgColor, int align, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setPadding(5); c.setBackgroundColor(bgColor); c.setHorizontalAlignment(align);
        table.addCell(c);
    }
}
