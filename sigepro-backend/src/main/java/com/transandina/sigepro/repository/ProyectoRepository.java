package com.transandina.sigepro.repository;

import com.transandina.sigepro.entity.Proyecto;
import com.transandina.sigepro.enums.EstadoProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {

    List<Proyecto> findByActivoTrue();

    List<Proyecto> findByActivoTrueAndEstado(EstadoProyecto estado);

    List<Proyecto> findByActivoTrueAndJefeProyectoId(Integer idJefeProyecto);
}
