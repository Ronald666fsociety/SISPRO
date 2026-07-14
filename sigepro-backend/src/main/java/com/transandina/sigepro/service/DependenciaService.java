package com.transandina.sigepro.service;

import com.transandina.sigepro.dto.DependenciaRequest;
import com.transandina.sigepro.dto.DependenciaResponse;
import com.transandina.sigepro.entity.DependenciaTarea;
import com.transandina.sigepro.entity.Tarea;
import com.transandina.sigepro.enums.TipoDependencia;
import com.transandina.sigepro.exception.ResourceNotFoundException;
import com.transandina.sigepro.repository.DependenciaTareaRepository;
import com.transandina.sigepro.repository.TareaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DependenciaService {

    private final DependenciaTareaRepository dependenciaRepository;
    private final TareaRepository tareaRepository;

    public DependenciaService(DependenciaTareaRepository dependenciaRepository,
                              TareaRepository tareaRepository) {
        this.dependenciaRepository = dependenciaRepository;
        this.tareaRepository = tareaRepository;
    }

    public List<DependenciaResponse> listarPorProyecto(Integer idProyecto) {
        return dependenciaRepository.findByTareaOrigenProyectoId(idProyecto)
                .stream()
                .map(DependenciaResponse::fromEntity)
                .toList();
    }

    @Transactional
    public DependenciaResponse crear(DependenciaRequest request) {
        if (request.getIdTareaOrigen().equals(request.getIdTareaDestino())) {
            throw new IllegalArgumentException("Una tarea no puede depender de si misma");
        }

        Tarea origen = tareaRepository.findById(request.getIdTareaOrigen())
                .orElseThrow(() -> new ResourceNotFoundException("Tarea origen no encontrada con id: " + request.getIdTareaOrigen()));

        Tarea destino = tareaRepository.findById(request.getIdTareaDestino())
                .orElseThrow(() -> new ResourceNotFoundException("Tarea destino no encontrada con id: " + request.getIdTareaDestino()));

        if (!origen.getProyecto().getId().equals(destino.getProyecto().getId())) {
            throw new IllegalArgumentException("Ambas tareas deben pertenecer al mismo proyecto");
        }

        if (dependenciaRepository.existsByTareaOrigenIdAndTareaDestinoId(
                request.getIdTareaOrigen(), request.getIdTareaDestino())) {
            throw new IllegalArgumentException("Esta dependencia ya existe");
        }

        if (creaCiclo(request.getIdTareaOrigen(), request.getIdTareaDestino())) {
            throw new IllegalArgumentException("La dependencia crearia un ciclo");
        }

        DependenciaTarea dep = new DependenciaTarea();
        dep.setTareaOrigen(origen);
        dep.setTareaDestino(destino);
        dep.setTipo(request.getTipo() != null ? request.getTipo() : TipoDependencia.FIN_INICIO);

        dep = dependenciaRepository.save(dep);
        return DependenciaResponse.fromEntity(dep);
    }

    @Transactional
    public void eliminar(Integer id) {
        DependenciaTarea dep = dependenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dependencia no encontrada con id: " + id));
        dependenciaRepository.delete(dep);
    }

    private boolean creaCiclo(Integer idOrigen, Integer idDestino) {
        Set<Integer> visitados = new HashSet<>();
        return tieneCamino(idDestino, idOrigen, visitados);
    }

    private boolean tieneCamino(Integer actual, Integer objetivo, Set<Integer> visitados) {
        if (actual.equals(objetivo)) return true;
        if (visitados.contains(actual)) return false;
        visitados.add(actual);

        List<DependenciaTarea> dependencias = dependenciaRepository.findByTareaOrigenId(actual);
        for (DependenciaTarea dep : dependencias) {
            if (tieneCamino(dep.getTareaDestino().getId(), objetivo, visitados)) {
                return true;
            }
        }
        return false;
    }
}
