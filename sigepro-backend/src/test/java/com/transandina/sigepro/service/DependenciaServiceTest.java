package com.transandina.sigepro.service;

import com.transandina.sigepro.dto.DependenciaRequest;
import com.transandina.sigepro.dto.DependenciaResponse;
import com.transandina.sigepro.entity.DependenciaTarea;
import com.transandina.sigepro.entity.Proyecto;
import com.transandina.sigepro.entity.Tarea;
import com.transandina.sigepro.enums.TipoDependencia;
import com.transandina.sigepro.repository.DependenciaTareaRepository;
import com.transandina.sigepro.repository.TareaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DependenciaServiceTest {

    @Mock private DependenciaTareaRepository dependenciaRepository;
    @Mock private TareaRepository tareaRepository;

    @InjectMocks
    private DependenciaService dependenciaService;

    @Captor
    private ArgumentCaptor<DependenciaTarea> depCaptor;

    private Proyecto proyecto;
    private Tarea t1, t2, t3;
    private DependenciaTarea dep1, dep2;

    @BeforeEach
    void setUp() {
        proyecto = new Proyecto();
        proyecto.setId(1);

        t1 = new Tarea(); t1.setId(1); t1.setNombre("Tarea A"); t1.setProyecto(proyecto);
        t2 = new Tarea(); t2.setId(2); t2.setNombre("Tarea B"); t2.setProyecto(proyecto);
        t3 = new Tarea(); t3.setId(3); t3.setNombre("Tarea C"); t3.setProyecto(proyecto);

        dep1 = new DependenciaTarea();
        dep1.setId(1);
        dep1.setTareaOrigen(t1);
        dep1.setTareaDestino(t2);

        dep2 = new DependenciaTarea();
        dep2.setId(2);
        dep2.setTareaOrigen(t2);
        dep2.setTareaDestino(t3);
    }

    @Test
    @DisplayName("Detectar ciclo directo A->B, B->A")
    void crearDependencia_CicloDirecto_LanzaExcepcion() {
        DependenciaRequest request = new DependenciaRequest();
        request.setIdTareaOrigen(2);
        request.setIdTareaDestino(1);
        request.setTipo(TipoDependencia.FIN_INICIO);

        when(tareaRepository.findById(1)).thenReturn(Optional.of(t1));
        when(tareaRepository.findById(2)).thenReturn(Optional.of(t2));
        when(dependenciaRepository.existsByTareaOrigenIdAndTareaDestinoId(2, 1)).thenReturn(false);
        when(dependenciaRepository.findByTareaOrigenId(1)).thenReturn(List.of(dep1));

        assertThrows(IllegalArgumentException.class,
                () -> dependenciaService.crear(request),
                "La dependencia crearia un ciclo");
    }

    @Test
    @DisplayName("Dependencia valida A->B se crea exitosamente")
    void crearDependencia_SinCiclo_Exitoso() {
        DependenciaRequest request = new DependenciaRequest();
        request.setIdTareaOrigen(1);
        request.setIdTareaDestino(2);
        request.setTipo(TipoDependencia.FIN_INICIO);

        DependenciaTarea savedDep = new DependenciaTarea();
        savedDep.setId(10);
        savedDep.setTareaOrigen(t1);
        savedDep.setTareaDestino(t2);
        savedDep.setTipo(TipoDependencia.FIN_INICIO);

        when(tareaRepository.findById(1)).thenReturn(Optional.of(t1));
        when(tareaRepository.findById(2)).thenReturn(Optional.of(t2));
        when(dependenciaRepository.existsByTareaOrigenIdAndTareaDestinoId(1, 2)).thenReturn(false);
        when(dependenciaRepository.findByTareaOrigenId(2)).thenReturn(List.of());
        when(dependenciaRepository.save(any(DependenciaTarea.class))).thenReturn(savedDep);

        DependenciaResponse result = dependenciaService.crear(request);

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("Tarea A", result.getNombreTareaOrigen());
        assertEquals("Tarea B", result.getNombreTareaDestino());
    }

    @Test
    @DisplayName("Auto-dependencia lanza excepcion")
    void crearDependencia_AutoReferencia_LanzaExcepcion() {
        DependenciaRequest request = new DependenciaRequest();
        request.setIdTareaOrigen(1);
        request.setIdTareaDestino(1);

        assertThrows(IllegalArgumentException.class,
                () -> dependenciaService.crear(request),
                "Una tarea no puede depender de si misma");
    }
}
