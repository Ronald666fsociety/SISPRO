package com.transandina.sigepro.repository;

import com.transandina.sigepro.entity.RecursoTarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RecursoTareaRepository extends JpaRepository<RecursoTarea, Integer> {

    List<RecursoTarea> findByTareaId(Integer idTarea);

    List<RecursoTarea> findByTareaProyectoId(Integer idProyecto);

    List<RecursoTarea> findByUsuarioId(Integer idUsuario);

    @Query("SELECT COALESCE(SUM(rt.horasEstimadas), 0) FROM RecursoTarea rt WHERE rt.usuario.id = :idUsuario")
    BigDecimal sumHorasEstimadasByUsuarioId(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT COALESCE(SUM(rt.horasReales), 0) FROM RecursoTarea rt WHERE rt.usuario.id = :idUsuario")
    BigDecimal sumHorasRealesByUsuarioId(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT COUNT(DISTINCT rt.tarea.id) FROM RecursoTarea rt WHERE rt.usuario.id = :idUsuario")
    Long countTareasByUsuarioId(@Param("idUsuario") Integer idUsuario);
}
