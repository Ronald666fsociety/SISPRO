package com.transandina.sigepro.repository;

import com.transandina.sigepro.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {

    List<Auditoria> findByEntidadAndIdEntidadOrderByFechaDesc(String entidad, Integer idEntidad);

    List<Auditoria> findByUsuarioIdOrderByFechaDesc(Integer idUsuario);

    List<Auditoria> findAllByOrderByFechaDesc();
}
