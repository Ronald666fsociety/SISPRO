package com.transandina.sigepro.repository;

import com.transandina.sigepro.entity.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Integer> {

    List<Tarea> findByProyectoId(Integer idProyecto);

    List<Tarea> findByTareaPadreId(Integer idTareaPadre);

    List<Tarea> findByResponsableId(Integer idResponsable);
}
