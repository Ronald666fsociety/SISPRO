package com.transandina.sigepro.service;

import com.transandina.sigepro.dto.CargaTrabajoResponse;
import com.transandina.sigepro.dto.RecursoTareaRequest;
import com.transandina.sigepro.dto.RecursoTareaResponse;
import com.transandina.sigepro.entity.RecursoTarea;
import com.transandina.sigepro.entity.Tarea;
import com.transandina.sigepro.entity.Usuario;
import com.transandina.sigepro.exception.ResourceNotFoundException;
import com.transandina.sigepro.repository.RecursoTareaRepository;
import com.transandina.sigepro.repository.TareaRepository;
import com.transandina.sigepro.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecursoTareaService {

    private final RecursoTareaRepository recursoTareaRepository;
    private final TareaRepository tareaRepository;
    private final UsuarioRepository usuarioRepository;

    public RecursoTareaService(RecursoTareaRepository recursoTareaRepository,
                                TareaRepository tareaRepository,
                                UsuarioRepository usuarioRepository) {
        this.recursoTareaRepository = recursoTareaRepository;
        this.tareaRepository = tareaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<RecursoTareaResponse> listarPorProyecto(Integer idProyecto) {
        return recursoTareaRepository.findByTareaProyectoId(idProyecto)
                .stream()
                .map(RecursoTareaResponse::fromEntity)
                .toList();
    }

    public List<RecursoTareaResponse> listarPorTarea(Integer idTarea) {
        return recursoTareaRepository.findByTareaId(idTarea)
                .stream()
                .map(RecursoTareaResponse::fromEntity)
                .toList();
    }

    @Transactional
    public RecursoTareaResponse crear(RecursoTareaRequest request) {
        Tarea tarea = tareaRepository.findById(request.getIdTarea())
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con id: " + request.getIdTarea()));

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + request.getIdUsuario()));

        RecursoTarea rt = new RecursoTarea();
        rt.setTarea(tarea);
        rt.setUsuario(usuario);
        rt.setHorasEstimadas(request.getHorasEstimadas() != null ? request.getHorasEstimadas() : java.math.BigDecimal.ZERO);
        rt.setHorasReales(request.getHorasReales() != null ? request.getHorasReales() : java.math.BigDecimal.ZERO);

        rt = recursoTareaRepository.save(rt);
        return RecursoTareaResponse.fromEntity(rt);
    }

    @Transactional
    public RecursoTareaResponse actualizar(Integer id, RecursoTareaRequest request) {
        RecursoTarea rt = recursoTareaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignacion no encontrada con id: " + id));

        Tarea tarea = tareaRepository.findById(request.getIdTarea())
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con id: " + request.getIdTarea()));

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + request.getIdUsuario()));

        rt.setTarea(tarea);
        rt.setUsuario(usuario);
        rt.setHorasEstimadas(request.getHorasEstimadas() != null ? request.getHorasEstimadas() : java.math.BigDecimal.ZERO);
        rt.setHorasReales(request.getHorasReales() != null ? request.getHorasReales() : java.math.BigDecimal.ZERO);

        rt = recursoTareaRepository.save(rt);
        return RecursoTareaResponse.fromEntity(rt);
    }

    @Transactional
    public void eliminar(Integer id) {
        RecursoTarea rt = recursoTareaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignacion no encontrada con id: " + id));
        recursoTareaRepository.delete(rt);
    }

    public CargaTrabajoResponse obtenerCargaTrabajo(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + idUsuario));

        CargaTrabajoResponse dto = new CargaTrabajoResponse();
        dto.setIdUsuario(usuario.getId());
        dto.setNombreUsuario(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setTotalHorasEstimadas(recursoTareaRepository.sumHorasEstimadasByUsuarioId(idUsuario));
        dto.setTotalHorasReales(recursoTareaRepository.sumHorasRealesByUsuarioId(idUsuario));
        dto.setCantidadTareas(recursoTareaRepository.countTareasByUsuarioId(idUsuario));
        return dto;
    }
}
