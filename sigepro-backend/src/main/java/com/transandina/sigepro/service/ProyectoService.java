package com.transandina.sigepro.service;

import com.transandina.sigepro.dto.ProyectoRequest;
import com.transandina.sigepro.dto.ProyectoResponse;
import com.transandina.sigepro.entity.Proyecto;
import com.transandina.sigepro.entity.Usuario;
import com.transandina.sigepro.enums.EstadoProyecto;
import com.transandina.sigepro.exception.ResourceNotFoundException;
import com.transandina.sigepro.repository.ProyectoRepository;
import com.transandina.sigepro.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    public ProyectoService(ProyectoRepository proyectoRepository,
                           UsuarioRepository usuarioRepository,
                           AuditoriaService auditoriaService) {
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    private Usuario getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) auth.getPrincipal();
    }

    public List<ProyectoResponse> listarTodos() {
        return proyectoRepository.findByActivoTrue()
                .stream()
                .map(ProyectoResponse::fromEntity)
                .toList();
    }

    public List<ProyectoResponse> listarPorEstado(EstadoProyecto estado) {
        return proyectoRepository.findByActivoTrueAndEstado(estado)
                .stream()
                .map(ProyectoResponse::fromEntity)
                .toList();
    }

    public ProyectoResponse buscarPorId(Integer id) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + id));
        return ProyectoResponse.fromEntity(proyecto);
    }

    @Transactional
    public ProyectoResponse crear(ProyectoRequest request) {
        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        Usuario jefe = usuarioRepository.findById(request.getIdJefeProyecto())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + request.getIdJefeProyecto()));

        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(request.getNombre());
        proyecto.setDescripcion(request.getDescripcion());
        proyecto.setFechaInicio(request.getFechaInicio());
        proyecto.setFechaFin(request.getFechaFin());
        proyecto.setEstado(request.getEstado() != null ? request.getEstado() : EstadoProyecto.PLANIFICADO);
        proyecto.setJefeProyecto(jefe);

        proyecto = proyectoRepository.save(proyecto);
        auditoriaService.registrar(getCurrentUser(), "CREAR", "Proyecto", proyecto.getId());
        return ProyectoResponse.fromEntity(proyecto);
    }

    @Transactional
    public ProyectoResponse actualizar(Integer id, ProyectoRequest request) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + id));

        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        Usuario jefe = usuarioRepository.findById(request.getIdJefeProyecto())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + request.getIdJefeProyecto()));

        proyecto.setNombre(request.getNombre());
        proyecto.setDescripcion(request.getDescripcion());
        proyecto.setFechaInicio(request.getFechaInicio());
        proyecto.setFechaFin(request.getFechaFin());
        proyecto.setEstado(request.getEstado() != null ? request.getEstado() : proyecto.getEstado());
        proyecto.setJefeProyecto(jefe);

        proyecto = proyectoRepository.save(proyecto);
        auditoriaService.registrar(getCurrentUser(), "ACTUALIZAR", "Proyecto", proyecto.getId());
        return ProyectoResponse.fromEntity(proyecto);
    }

    @Transactional
    public void eliminar(Integer id) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + id));
        proyecto.setActivo(false);
        proyectoRepository.save(proyecto);
        auditoriaService.registrar(getCurrentUser(), "ELIMINAR", "Proyecto", id);
    }
}
