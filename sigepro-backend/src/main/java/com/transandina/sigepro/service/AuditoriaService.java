package com.transandina.sigepro.service;

import com.transandina.sigepro.dto.AuditoriaResponse;
import com.transandina.sigepro.entity.Auditoria;
import com.transandina.sigepro.entity.Usuario;
import com.transandina.sigepro.repository.AuditoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public void registrar(Usuario usuario, String accion, String entidad, Integer idEntidad) {
        Auditoria auditoria = new Auditoria(usuario, accion, entidad, idEntidad);
        auditoriaRepository.save(auditoria);
    }

    public List<AuditoriaResponse> listarTodas() {
        return auditoriaRepository.findAllByOrderByFechaDesc()
                .stream()
                .map(AuditoriaResponse::fromEntity)
                .toList();
    }

    public List<AuditoriaResponse> listarPorEntidad(String entidad, Integer idEntidad) {
        return auditoriaRepository.findByEntidadAndIdEntidadOrderByFechaDesc(entidad, idEntidad)
                .stream()
                .map(AuditoriaResponse::fromEntity)
                .toList();
    }
}
