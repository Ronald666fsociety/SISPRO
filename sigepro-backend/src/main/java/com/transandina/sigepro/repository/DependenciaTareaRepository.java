package com.transandina.sigepro.repository;

import com.transandina.sigepro.entity.DependenciaTarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DependenciaTareaRepository extends JpaRepository<DependenciaTarea, Integer> {

    List<DependenciaTarea> findByTareaOrigenId(Integer idTareaOrigen);

    List<DependenciaTarea> findByTareaDestinoId(Integer idTareaDestino);

    List<DependenciaTarea> findByTareaOrigenProyectoId(Integer idProyecto);

    boolean existsByTareaOrigenIdAndTareaDestinoId(Integer idOrigen, Integer idDestino);
}
