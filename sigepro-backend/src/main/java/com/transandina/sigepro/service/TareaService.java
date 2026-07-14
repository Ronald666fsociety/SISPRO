package com.transandina.sigepro.service;

import com.transandina.sigepro.dto.TareaRequest;
import com.transandina.sigepro.dto.TareaResponse;
import com.transandina.sigepro.entity.Proyecto;
import com.transandina.sigepro.entity.Tarea;
import com.transandina.sigepro.entity.Usuario;
import com.transandina.sigepro.exception.ResourceNotFoundException;
import com.transandina.sigepro.repository.ProyectoRepository;
import com.transandina.sigepro.repository.TareaRepository;
import com.transandina.sigepro.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TareaService {

    private final TareaRepository tareaRepository;
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;

    public TareaService(TareaRepository tareaRepository,
                        ProyectoRepository proyectoRepository,
                        UsuarioRepository usuarioRepository) {
        this.tareaRepository = tareaRepository;
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<TareaResponse> listarPorProyecto(Integer idProyecto) {
        return tareaRepository.findByProyectoId(idProyecto)
                .stream()
                .map(TareaResponse::fromEntity)
                .toList();
    }

    public TareaResponse buscarPorId(Integer id) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con id: " + id));
        return TareaResponse.fromEntity(tarea);
    }

    @Transactional
    public TareaResponse crear(TareaRequest request) {
        Proyecto proyecto = proyectoRepository.findById(request.getIdProyecto())
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + request.getIdProyecto()));

        Usuario responsable = usuarioRepository.findById(request.getIdResponsable())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + request.getIdResponsable()));

        validarFechas(request.getFechaInicio(), request.getFechaFin());

        Tarea tarea = new Tarea();
        tarea.setProyecto(proyecto);
        tarea.setNombre(request.getNombre());
        tarea.setFechaInicio(request.getFechaInicio());
        tarea.setFechaFin(request.getFechaFin());
        tarea.setPorcentajeAvance(request.getPorcentajeAvance() != null ? request.getPorcentajeAvance() : 0);
        tarea.setPresupuestoEstimado(request.getPresupuestoEstimado() != null ? request.getPresupuestoEstimado() : java.math.BigDecimal.ZERO);
        tarea.setCostoEjecutado(request.getCostoEjecutado() != null ? request.getCostoEjecutado() : java.math.BigDecimal.ZERO);
        tarea.setResponsable(responsable);

        if (request.getIdTareaPadre() != null) {
            Tarea padre = tareaRepository.findById(request.getIdTareaPadre())
                    .orElseThrow(() -> new ResourceNotFoundException("Tarea padre no encontrada con id: " + request.getIdTareaPadre()));
            if (!padre.getProyecto().getId().equals(request.getIdProyecto())) {
                throw new IllegalArgumentException("La tarea padre debe pertenecer al mismo proyecto");
            }
            tarea.setTareaPadre(padre);
        }

        tarea = tareaRepository.save(tarea);
        recalcularTotalesProyecto(tarea.getProyecto().getId());
        return TareaResponse.fromEntity(tarea);
    }

    @Transactional
    public TareaResponse actualizar(Integer id, TareaRequest request) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con id: " + id));

        Proyecto proyecto = proyectoRepository.findById(request.getIdProyecto())
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + request.getIdProyecto()));

        Usuario responsable = usuarioRepository.findById(request.getIdResponsable())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + request.getIdResponsable()));

        validarFechas(request.getFechaInicio(), request.getFechaFin());

        tarea.setProyecto(proyecto);
        tarea.setNombre(request.getNombre());
        tarea.setFechaInicio(request.getFechaInicio());
        tarea.setFechaFin(request.getFechaFin());
        tarea.setPorcentajeAvance(request.getPorcentajeAvance() != null ? request.getPorcentajeAvance() : 0);
        tarea.setPresupuestoEstimado(request.getPresupuestoEstimado() != null ? request.getPresupuestoEstimado() : java.math.BigDecimal.ZERO);
        tarea.setCostoEjecutado(request.getCostoEjecutado() != null ? request.getCostoEjecutado() : java.math.BigDecimal.ZERO);
        tarea.setResponsable(responsable);

        if (request.getIdTareaPadre() != null) {
            Tarea padre = tareaRepository.findById(request.getIdTareaPadre())
                    .orElseThrow(() -> new ResourceNotFoundException("Tarea padre no encontrada con id: " + request.getIdTareaPadre()));
            if (!padre.getProyecto().getId().equals(request.getIdProyecto())) {
                throw new IllegalArgumentException("La tarea padre debe pertenecer al mismo proyecto");
            }
            tarea.setTareaPadre(padre);
        } else {
            tarea.setTareaPadre(null);
        }

        tarea = tareaRepository.save(tarea);
        recalcularTotalesProyecto(tarea.getProyecto().getId());
        return TareaResponse.fromEntity(tarea);
    }

    @Transactional
    public void eliminar(Integer id) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con id: " + id));

        List<Tarea> subtareas = tareaRepository.findByTareaPadreId(id);
        if (!subtareas.isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar la tarea porque tiene subtareas asociadas");
        }

        Integer idProyecto = tarea.getProyecto().getId();
        tareaRepository.delete(tarea);
        recalcularTotalesProyecto(idProyecto);
    }

    private void recalcularTotalesProyecto(Integer idProyecto) {
        List<Tarea> tareas = tareaRepository.findByProyectoId(idProyecto);
        BigDecimal totalPresupuesto = tareas.stream()
                .map(t -> t.getPresupuestoEstimado() != null ? t.getPresupuestoEstimado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCosto = tareas.stream()
                .map(t -> t.getCostoEjecutado() != null ? t.getCostoEjecutado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        proyectoRepository.findById(idProyecto).ifPresent(p -> {
            p.setPresupuestoTotal(totalPresupuesto);
            p.setCostoRealTotal(totalCosto);
            proyectoRepository.save(p);
        });
    }

    private void validarFechas(java.time.LocalDate inicio, java.time.LocalDate fin) {
        if (fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
    }
}
