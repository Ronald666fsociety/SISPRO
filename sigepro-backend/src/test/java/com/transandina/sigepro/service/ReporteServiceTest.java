package com.transandina.sigepro.service;

import com.transandina.sigepro.dto.PresupuestoResponse;
import com.transandina.sigepro.dto.SemaforoResponse;
import com.transandina.sigepro.entity.Proyecto;
import com.transandina.sigepro.entity.Tarea;
import com.transandina.sigepro.enums.EstadoProyecto;
import com.transandina.sigepro.repository.ProyectoRepository;
import com.transandina.sigepro.repository.TareaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock private ProyectoRepository proyectoRepository;
    @Mock private TareaRepository tareaRepository;
    @Mock private RecursoTareaService recursoTareaService;

    @InjectMocks
    private ReporteService reporteService;

    private Proyecto proyecto;
    private List<Tarea> tareas;

    @BeforeEach
    void setUp() {
        proyecto = new Proyecto();
        proyecto.setId(1);
        proyecto.setNombre("Test Proyecto");
        proyecto.setEstado(EstadoProyecto.EN_CURSO);
        proyecto.setPresupuestoTotal(new BigDecimal("100000.00"));
        proyecto.setCostoRealTotal(new BigDecimal("45000.00"));
        proyecto.setFechaInicio(LocalDate.of(2026, 1, 1));
        proyecto.setFechaFin(LocalDate.of(2026, 6, 30));

        Tarea t1 = new Tarea();
        t1.setId(1);
        t1.setNombre("Tarea 1");
        t1.setPorcentajeAvance(100);
        t1.setPresupuestoEstimado(new BigDecimal("50000.00"));
        t1.setCostoEjecutado(new BigDecimal("25000.00"));
        t1.setFechaInicio(LocalDate.of(2026, 1, 1));
        t1.setFechaFin(LocalDate.of(2026, 3, 31));

        Tarea t2 = new Tarea();
        t2.setId(2);
        t2.setNombre("Tarea 2");
        t2.setPorcentajeAvance(50);
        t2.setPresupuestoEstimado(new BigDecimal("30000.00"));
        t2.setCostoEjecutado(new BigDecimal("15000.00"));
        t2.setFechaInicio(LocalDate.of(2026, 2, 1));
        t2.setFechaFin(LocalDate.of(2026, 5, 31));

        Tarea t3 = new Tarea();
        t3.setId(3);
        t3.setNombre("Tarea 3");
        t3.setPorcentajeAvance(0);
        t3.setPresupuestoEstimado(new BigDecimal("20000.00"));
        t3.setCostoEjecutado(new BigDecimal("5000.00"));
        t3.setFechaInicio(LocalDate.of(2026, 4, 1));
        t3.setFechaFin(LocalDate.of(2026, 6, 30));

        tareas = List.of(t1, t2, t3);
    }

    @Test
    @DisplayName("Calcular semaforo - proyecto dentro de presupuesto")
    void calcularSemaforo_DentroPresupuesto_DevuelveVerde() {
        when(proyectoRepository.findById(1)).thenReturn(Optional.of(proyecto));
        when(tareaRepository.findByProyectoId(1)).thenReturn(tareas);

        SemaforoResponse result = reporteService.calcularSemaforo(1);

        assertNotNull(result);
        assertEquals("EN_CURSO", result.getEstadoProyecto());
        assertTrue(result.getAvanceReal() >= 0);
        assertTrue(result.getAvancePlanificado() > 0);
    }

    @Test
    @DisplayName("Calcular semaforo - proyecto sobre presupuesto")
    void calcularSemaforo_SobrePresupuesto_DevuelveRojo() {
        proyecto.setCostoRealTotal(new BigDecimal("110000.00"));
        when(proyectoRepository.findById(1)).thenReturn(Optional.of(proyecto));
        when(tareaRepository.findByProyectoId(1)).thenReturn(tareas);

        SemaforoResponse result = reporteService.calcularSemaforo(1);

        assertNotNull(result);
        assertTrue(result.getSobreCostoPorcentaje() > 0);
    }

    @Test
    @DisplayName("Presupuesto vs Costo - diferencia calculada correctamente")
    void presupuestoVsCosto_DiferenciaCorrecta() {
        when(proyectoRepository.findById(1)).thenReturn(Optional.of(proyecto));

        PresupuestoResponse result = reporteService.presupuestoVsCosto(1);

        assertNotNull(result);
        assertEquals(new BigDecimal("100000.00"), result.getPresupuestoTotalEstimado());
        assertEquals(new BigDecimal("45000.00"), result.getCostoTotalEjecutado());
        assertEquals(new BigDecimal("55000.00"), result.getDiferencia());
        assertEquals("DENTRO_PRESUPUESTO", result.getEstado());
    }

    @Test
    @DisplayName("Presupuesto vs Costo - diferencia negativa cuando costo supera presupuesto")
    void presupuestoVsCosto_DiferenciaNegativa() {
        proyecto.setCostoRealTotal(new BigDecimal("110000.00"));
        when(proyectoRepository.findById(1)).thenReturn(Optional.of(proyecto));

        PresupuestoResponse result = reporteService.presupuestoVsCosto(1);

        assertEquals(new BigDecimal("-10000.00"), result.getDiferencia());
        assertEquals("SOBRE_PRESUPUESTO", result.getEstado());
    }

    @Test
    @DisplayName("Presupuesto vs Costo - proyecto inexistente lanza excepcion")
    void presupuestoVsCosto_ProyectoInexistente_LanzaExcepcion() {
        when(proyectoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> reporteService.presupuestoVsCosto(99));
    }

    @Test
    @DisplayName("Calcular semaforo - avance promedio con tareas mixtas")
    void calcularSemaforo_AvancePromedio_Correcto() {
        when(proyectoRepository.findById(1)).thenReturn(Optional.of(proyecto));
        when(tareaRepository.findByProyectoId(1)).thenReturn(tareas);

        SemaforoResponse result = reporteService.calcularSemaforo(1);

        // (100 + 50 + 0) / 3 = 50
        assertEquals(50.0, result.getAvanceReal(), 0.01);
    }
}
